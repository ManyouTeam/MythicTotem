package cn.superiormc.mythictotem.managers;

import cn.superiormc.mythictotem.MythicTotem;
import cn.superiormc.mythictotem.objects.ObjectTotem;
import cn.superiormc.mythictotem.objects.checks.ObjectPriceCheck;
import cn.superiormc.mythictotem.objects.effect.EffectUtil;
import cn.superiormc.mythictotem.objects.singlethings.BonusTotemData;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class BonusEffectsManager {

    public static BonusEffectsManager manager;

    public static final NamespacedKey KEY_CHUNK_TOTEMS = new NamespacedKey(MythicTotem.instance, "bonus_totems");

    private final Map<Chunk, Collection<BonusTotemData>> totemMap = new ConcurrentHashMap<>();

    private final Map<UUID, Map<Location, BonusTotemData>> playerActive = new ConcurrentHashMap<>();

    private final Map<Integer, List<double[]>> circleCache = new HashMap<>();

    private Collection<Chunk> chunkCache = new ArrayList<>();

    public BonusEffectsManager() {
        manager = this;
    }

    public void addBonusEffectBlock(Block block,
                                    int level,
                                    String totemId,
                                    boolean isCore,
                                    UUID uuid) {

        Chunk chunk = block.getChunk();

        BonusTotemData data = new BonusTotemData(
                block.getLocation(),
                level,
                System.currentTimeMillis(),
                totemId,
                isCore,
                uuid
        );

        totemMap.computeIfAbsent(chunk, k -> new TreeSet<>()).add(data);

        saveToChunkPDC(chunk);
    }

    public void tickChunks() {
        for (World world : Bukkit.getWorlds()) {
            for (Chunk chunk : world.getLoadedChunks()) {
                chunkCache.add(chunk);
                processChunk(chunk);
            }
        }
    }

    public void tick() {
        for (Chunk chunk : totemMap.keySet()) {
            if (!chunkCache.contains(chunk) || !chunk.isLoaded()) {
                totemMap.remove(chunk);
            }
        }
        for (Player player : Bukkit.getOnlinePlayers()) {
            processPlayer(player);
        }
    }

    private void processPlayer(Player player) {
        Location loc = player.getLocation();
        Chunk center = loc.getChunk();

        int radius = ConfigManager.configManager.getInt("bonus-effects.check-radius", 10);
        int chunkRadius = (int) Math.ceil(radius / 16.0);

        Map<Location, BonusTotemData> active = playerActive.computeIfAbsent(player.getUniqueId(), k -> new LinkedHashMap<>());

        Set<Location> nowInRange = new HashSet<>();
        Set<String> nowTotemID = new HashSet<>();

        for (int dx = -chunkRadius; dx <= chunkRadius; dx++) {
            for (int dz = -chunkRadius; dz <= chunkRadius; dz++) {

                Chunk chunk = loc.getWorld().getChunkAt(
                        center.getX() + dx,
                        center.getZ() + dz
                );

                Collection<BonusTotemData> list = totemMap.get(chunk);
                if (list == null) {
                    continue;
                }

                for (BonusTotemData totemData : list) {

                    if (!totemData.isCore || !totemData.location.getWorld().equals(loc.getWorld())) {
                        continue;
                    }

                    ObjectTotem totem = totemData.totem;
                    if (totem == null) {
                        continue;
                    }

                    double distSq = totemData.location.distance(loc);

                    if (distSq <= totemData.getRange()) {
                        nowInRange.add(totemData.location);
                        if (ConfigManager.configManager.getBoolean("bonus-effects.range-display.enabled", true)) {
                            showTotemRange(player, totemData);
                        }

                        if (!active.containsKey(totemData.location)) {
                            int limit = EffectUtil.getMaxEffectsAmount(player, totemData);
                            if (active.size() < limit && (ConfigManager.configManager.getBoolean("bonus-effects.limit.same-totem-only-active-once", true) &&
                                    !nowTotemID.contains(totemData.totemId))) {
                                active.put(totemData.location, totemData);
                                applyBonus(player, totemData);
                            }
                        } else {
                            circleBonus(player, totemData);
                        }
                        nowTotemID.add(totemData.totemId);
                    }
                }
            }
        }

        // 离开检测
        Iterator<Map.Entry<Location, BonusTotemData>> it = active.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Location, BonusTotemData> entry = it.next();

            if (!nowInRange.contains(entry.getKey())) {
                removeBonus(player, entry.getValue());
                it.remove();
            }
        }
    }

    public void removePlayer(Player player) {
        for (BonusTotemData totemData : getPlayerActivedBonus(player)) {
            removeBonus(player, totemData);
        }
        playerActive.remove(player.getUniqueId());
    }

    private void processChunk(Chunk chunk) {
        if (totemMap.containsKey(chunk)) {
            return;
        }

        String data = chunk.getPersistentDataContainer()
                .get(KEY_CHUNK_TOTEMS, PersistentDataType.STRING);

        if (data == null || data.isEmpty()) return;

        List<BonusTotemData> list = new ArrayList<>();

        String[] entries = data.split(";;;;");

        for (String entry : entries) {
            String[] parts = entry.split(";;");
            if (parts.length != 6) {
                continue;
            }

            Location loc = stringToLoc(chunk.getWorld(), parts[0]);
            if (loc == null) {
                continue;
            }

            int level = Integer.parseInt(parts[1]);
            long time = Long.parseLong(parts[2]);
            String id = parts[3];
            boolean isCore = Boolean.parseBoolean(parts[4]);
            UUID uuid = UUID.fromString(parts[5]);

            list.add(new BonusTotemData(loc, level, time, id, isCore, uuid));
        }

        totemMap.put(chunk, list);
    }

    private void saveToChunkPDC(Chunk chunk) {
        Collection<BonusTotemData> list = totemMap.get(chunk);

        if (list == null || list.isEmpty()) {
            chunk.getPersistentDataContainer().remove(KEY_CHUNK_TOTEMS);
            return;
        }

        StringBuilder sb = new StringBuilder();

        for (BonusTotemData data : list) {
            sb.append(locToString(data.location)).append(";;")
                    .append(data.getLevel()).append(";;")
                    .append(data.placeTime).append(";;")
                    .append(data.totemId).append(";;")
                    .append(data.isCore).append(";;")
                    .append(data.totemUUID)
                    .append(";;;;");
        }

        chunk.getPersistentDataContainer().set(
                KEY_CHUNK_TOTEMS,
                PersistentDataType.STRING,
                sb.toString()
        );
    }

    public void destroyTotem(Location brokenLocation) {

        Chunk chunk = brokenLocation.getChunk();
        Collection<BonusTotemData> list = totemMap.get(chunk);

        if (list == null) {
            return;
        }

        BonusTotemData target = null;

        for (BonusTotemData data : list) {
            if (data.location.equals(brokenLocation)) {
                target = data;
                break;
            }
        }

        if (target == null) {
            return;
        }

        destroyTotem(target);
    }

    public void destroyTotem(BonusTotemData target) {
        UUID uuid = target.totemUUID;

        for (Map.Entry<Chunk, Collection<BonusTotemData>> entry : totemMap.entrySet()) {
            Collection<BonusTotemData> chunkList = entry.getValue();

            boolean changed = chunkList.removeIf(data -> data.totemUUID.equals(uuid));

            if (changed) {
                saveToChunkPDC(entry.getKey());
            }
        }

        for (Map.Entry<UUID, Map<Location, BonusTotemData>> playerEntry : playerActive.entrySet()) {

            Player player = Bukkit.getPlayer(playerEntry.getKey());
            if (player == null) continue;

            Map<Location, BonusTotemData> map = playerEntry.getValue();

            Iterator<Map.Entry<Location, BonusTotemData>> it = map.entrySet().iterator();

            while (it.hasNext()) {
                Map.Entry<Location, BonusTotemData> e = it.next();

                if (e.getValue().totemUUID.equals(uuid)) {
                    removeBonus(player, e.getValue());
                    it.remove();
                }
            }
        }
    }


    private void applyBonus(Player player, BonusTotemData data) {
        data.runBonusEffectsApplyActions(player);
    }

    private void removeBonus(Player player, BonusTotemData data) {
        data.runBonusEffectsRemoveActions(player);
    }

    private void circleBonus(Player player, BonusTotemData data) {
        if (data.canExecuteCircleActionAgain()) {
            data.setNewLastCircleTime();
            data.runBonusEffectsCircleActions(player);
        }
    }

    private String locToString(Location loc) {
        return loc.getBlockX() + "," + loc.getBlockY() + "," + loc.getBlockZ();
    }

    private Location stringToLoc(World world, String str) {
        String[] parts = str.split(",");
        if (parts.length != 3) return null;

        return new Location(
                world,
                Double.parseDouble(parts[0]),
                Double.parseDouble(parts[1]),
                Double.parseDouble(parts[2])
        );
    }

    public Collection<BonusTotemData> getPlayerActivedBonus(Player player) {
        Map<Location, BonusTotemData> tempVal1 = playerActive.get(player.getUniqueId());
        if (tempVal1 == null) {
            return new ArrayList<>();
        }
        return tempVal1.values();
    }

    private List<double[]> getCirclePoints(double radius, int points) {
        int key = (int) (radius * 100);

        if (circleCache.containsKey(key)) {
            return circleCache.get(key);
        }

        List<double[]> list = new ArrayList<>();

        for (int i = 0; i < points; i++) {
            double angle = 2 * Math.PI * i / points;

            double x = radius * Math.cos(angle);
            double z = radius * Math.sin(angle);

            list.add(new double[]{x, z});
        }

        circleCache.put(key, list);
        return list;
    }

    public void showTotemRange(Player player, BonusTotemData data) {
        ObjectTotem totem = data.totem;
        if (totem == null) {
            return;
        }

        double radius = data.getRange();
        Location center = data.location.clone().add(0.5, 0.1, 0.5);
        World world = center.getWorld();
        if (world == null) {
            return;
        }

        int points = ConfigManager.configManager.getInt("bonus-effects.range-display.points", 40);

        List<double[]> circle = getCirclePoints(radius, points);

        for (double[] offset : circle) {
            Location loc = center.clone().add(offset[0], 0, offset[1]);

            player.spawnParticle(
                    Particle.valueOf(ConfigManager.configManager.getString("bonus-effects.range-display.particle", "END_ROD")),
                    loc,
                    1,
                    0, 0, 0,
                    0
            );
        }
    }

    public BonusTotemData getBonusTotemAt(Location location) {
        Collection<BonusTotemData> list = totemMap.get(location.getChunk());
        if (list == null) {
            return null;
        }
        for (BonusTotemData data : list) {
            if (data.location.equals(location)) {
                return data;
            }
        }
        return null;
    }

    public boolean upgradeBonusTotem(Player player, BonusTotemData data) {
        if (data.totem == null) {
            return false;
        }

        int maxLevel = data.getMaxLevel();
        if (data.getLevel() >= maxLevel) {
            LanguageManager.languageManager.sendStringText(player, "bonus-gui-max-level");
            return false;
        }

        ObjectPriceCheck priceCheck = data.getUpgradePrice(player);
        if (priceCheck == null) {
            return false;
        }

        if (!priceCheck.checkPrice(false, null)) {
            LanguageManager.languageManager.sendStringText(player, "bonus-gui-upgrade-failed");
            return false;
        }
        priceCheck.checkPrice(true, null);
        Collection<Chunk> chunks = new ArrayList<>();
        for (Collection<BonusTotemData> tempVal1 : totemMap.values()) {
            for (BonusTotemData tempVal2 : tempVal1) {
                if (tempVal2.totemUUID.equals(data.totemUUID)) {
                    removeBonus(player, tempVal2);
                    tempVal2.levelUp();
                    applyBonus(player, tempVal2);
                    chunks.add(tempVal2.location.getChunk());
                }
            }
        }

        for (Chunk tempVal3 : chunks) {
            saveToChunkPDC(tempVal3);
        }

        LanguageManager.languageManager.sendStringText(player, "bonus-gui-upgrade-success", "level", String.valueOf(data.getLevel()));
        return true;
    }

}
