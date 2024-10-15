package cn.superiormc.mythictotem.objects;

import cn.superiormc.mythictotem.MythicTotem;
import cn.superiormc.mythictotem.managers.ErrorManager;
import cn.superiormc.mythictotem.objects.checks.ObjectPlaceCheck;
import cn.superiormc.mythictotem.objects.checks.ObjectCheck;
import cn.superiormc.mythictotem.utils.*;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ObjectAction {

    private final List<String> actions = new ArrayList<>();

    private final Map<Long, List<String>> delayActions = new HashMap<>();

    public ObjectAction(List<String> action) {
        for (String singleAction : action) {
            Pattern pattern = Pattern.compile("-\\d+$");
            Matcher matcher = pattern.matcher(singleAction);
            if (matcher.find() && !MythicTotem.freeVersion) {
                String number = matcher.group().substring(1);
                long time = Long.parseLong(number);
                if (!delayActions.containsKey(time)) {
                    delayActions.put(time, new ArrayList<>());
                }
                delayActions.get(time).add(singleAction.replaceAll("-\\d+$", ""));
            } else {
                actions.add(singleAction);
            }
        }
    }

    public void checkAction(Location startLocation, ObjectPlaceCheck totem, ObjectCheck manager) {
        for (Long time : delayActions.keySet()) {
            Bukkit.getScheduler().runTaskLater(MythicTotem.instance, () -> {
                for (String singleAction : delayActions.get(time)) {
                    doAction(singleAction, startLocation, totem, manager);
                }
            }, time);
        }
        Bukkit.getScheduler().runTask(MythicTotem.instance, () -> {
            for (String singleAction : actions) {
                doAction(singleAction, startLocation, totem, manager);
            }
        });
    }

    private void doAction(String singleAction, Location startLocation, ObjectPlaceCheck totem, ObjectCheck manager) {
        Player player = manager.getPlayer();
        Block block = manager.getBlock();
        singleAction = replacePlaceholder(singleAction, player, block, startLocation, totem);
        if (singleAction.startsWith("none")) {
            return;
        } else if (singleAction.startsWith("sound: ") && player != null) {
            // By: iKiwo
            String soundData = singleAction.substring(7); // "sound: LEVEL_UP;volume;pitch"
            String[] soundParts = soundData.split(";;");
            if (soundParts.length >= 1) {
                String soundName = soundParts[0];
                float volume = 1.0f;
                float pitch = 1.0f;
                if (soundParts.length >= 2) {
                    try {
                        volume = Float.parseFloat(soundParts[1]);
                    } catch (NumberFormatException e) {
                        ErrorManager.errorManager.sendErrorMessage("§x§9§8§F§B§9§8[MythicTotem] §cError: Invalid volume value in sound action.");
                    }
                }
                if (soundParts.length >= 3) {
                    try {
                        pitch = Float.parseFloat(soundParts[2]);
                    } catch (NumberFormatException e) {
                        ErrorManager.errorManager.sendErrorMessage("§x§9§8§F§B§9§8[MythicTotem] §cError: Invalid pitch value in sound action.");
                    }
                }
                Location location = player.getLocation();
                player.playSound(location, soundName, volume, pitch);
            } else {
                ErrorManager.errorManager.sendErrorMessage("§x§9§8§F§B§9§8[MythicTotem] §cError: Invalid sound action format.");
            }
        } else if (singleAction.startsWith("message: ") && player != null) {
            player.sendMessage(TextUtil.parse(singleAction.substring(9)));
        } else if (singleAction.startsWith("announcement: ")) {
            Collection<? extends Player> players = Bukkit.getOnlinePlayers();
            for (Player p : players) {
                p.sendMessage(singleAction.substring(14));
            }
        } else if (singleAction.startsWith("effect: ") && player != null) {
            try {
                PotionEffectType potionEffectType = PotionEffectType.getByName(singleAction.substring(8).split(";;")[0].toUpperCase());
                if (potionEffectType == null) {
                    ErrorManager.errorManager.sendErrorMessage("§x§9§8§F§B§9§8[MythicTotem] §cError: Can not found potion effect: " +
                            singleAction.split(";;")[0] + ".");
                    return;
                }
                PotionEffect effect = new PotionEffect(potionEffectType,
                        Integer.parseInt(singleAction.substring(8).split(";;")[2]),
                        Integer.parseInt(singleAction.substring(8).split(";;")[1]) - 1,
                        true,
                        true,
                        true);
                player.addPotionEffect(effect);
            }
            catch (ArrayIndexOutOfBoundsException e) {
                ErrorManager.errorManager.sendErrorMessage("§x§9§8§F§B§9§8[MythicTotem] §cError: Your effect action in totem configs can not being correctly load.");
            }
        } else if (singleAction.startsWith("entity_spawn: ") && player != null) {
            if (singleAction.split(";;").length == 1) {
                EntityType entity = EntityType.valueOf(singleAction.substring(14).split(";;")[0].toUpperCase());
                player.getLocation().getWorld().spawnEntity(player.getLocation(), entity);
            } else if (singleAction.split(";;").length == 5) {
                World world = Bukkit.getWorld(singleAction.substring(14).split(";;")[1]);
                Location location = new Location(world,
                        Double.parseDouble(singleAction.substring(14).split(";;")[2]),
                        Double.parseDouble(singleAction.substring(14).split(";;")[3]),
                        Double.parseDouble(singleAction.substring(14).split(";;")[4]));
                EntityType entity = EntityType.valueOf(singleAction.substring(14).split(";;")[0].toUpperCase());
                if (location.getWorld() == null) {
                    ErrorManager.errorManager.sendErrorMessage("§x§9§8§F§B§9§8[MythicTotem] §cError: Your entity_spawn action in totem configs can not being correctly load.");
                }
                location.getWorld().spawnEntity(location, entity);
            }
        } else if (singleAction.startsWith("teleport: ") && player != null) {
            try {
                if (singleAction.split(";;").length == 4) {
                    Location loc = new Location(Bukkit.getWorld(singleAction.substring(10).split(";;")[0]),
                            Double.parseDouble(singleAction.substring(10).split(";;")[1]),
                            Double.parseDouble(singleAction.substring(10).split(";;")[2]),
                            Double.parseDouble(singleAction.substring(10).split(";;")[3]),
                            player.getLocation().getYaw(),
                            player.getLocation().getPitch());
                    player.teleport(loc);
                }
                else if (singleAction.split(";;").length == 6) {
                    Location loc = new Location(Bukkit.getWorld(singleAction.split(";;")[0]),
                            Double.parseDouble(singleAction.substring(10).split(";;")[1]),
                            Double.parseDouble(singleAction.substring(10).split(";;")[2]),
                            Double.parseDouble(singleAction.substring(10).split(";;")[3]),
                            Float.parseFloat(singleAction.substring(10).split(";;")[4]),
                            Float.parseFloat(singleAction.substring(10).split(";;")[5]));
                    player.teleport(loc);
                }
                else {
                    ErrorManager.errorManager.sendErrorMessage("§x§9§8§F§B§9§8[MythicTotem] §cError: Your teleport action in totem configs can not being correctly load.");
                }
            }
            catch (ArrayIndexOutOfBoundsException e) {
                ErrorManager.errorManager.sendErrorMessage("§x§9§8§F§B§9§8[MythicTotem] §cError: Your teleport action in totem configs can not being correctly load.");
            }
        } else if (CommonUtil.checkPluginLoad("MythicMobs") && singleAction.startsWith("mythicmobs_spawn: ")) {
            try {
                if (singleAction.substring(18).split(";;").length == 1) {
                    CommonUtil.summonMythicMobs(block.getLocation(),
                            singleAction.substring(18).split(";;")[0],
                            1);
                } else if (singleAction.substring(18).split(";;").length == 2) {
                    CommonUtil.summonMythicMobs(block.getLocation(),
                            singleAction.substring(18).split(";;")[0],
                            Integer.parseInt(singleAction.substring(18).split(";;")[1]));
                } else if (singleAction.substring(18).split(";;").length == 5) {
                    World world = Bukkit.getWorld(singleAction.substring(18).split(";;")[1]);
                    Location location = new Location(world,
                            Double.parseDouble(singleAction.substring(18).split(";;")[2]),
                            Double.parseDouble(singleAction.substring(18).split(";;")[3]),
                            Double.parseDouble(singleAction.substring(18).split(";;")[4])
                    );
                    CommonUtil.summonMythicMobs(location,
                            singleAction.substring(18).split(";;")[0],
                            1);
                } else if (singleAction.substring(18).split(";;").length == 6) {
                    World world = Bukkit.getWorld(singleAction.substring(18).split(";;")[2]);
                    Location location = new Location(world,
                            Double.parseDouble(singleAction.substring(18).split(";;")[3]),
                            Double.parseDouble(singleAction.substring(18).split(";;")[4]),
                            Double.parseDouble(singleAction.substring(18).split(";;")[5])
                    );
                    CommonUtil.summonMythicMobs(location,
                            singleAction.substring(18).split(";;")[0],
                            Integer.parseInt(singleAction.substring(18).split(";;")[1]));
                } else {
                    ErrorManager.errorManager.sendErrorMessage("§x§9§8§F§B§9§8[MythicTotem] §cError: Your mythicmobs_spawn action in totem configs can not being correctly load.");
                }
            } catch (ArrayIndexOutOfBoundsException e) {
                ErrorManager.errorManager.sendErrorMessage("§x§9§8§F§B§9§8[MythicTotem] §cError: Your mythicmobs_spawn action in totem configs can not being correctly load.");
            }
        } else if (singleAction.startsWith("console_command: ")) {
            CommonUtil.dispatchCommand(singleAction.substring(17));
        } else if (singleAction.startsWith("player_command: ") && player != null) {
            CommonUtil.dispatchCommand(player, singleAction.substring(16));

        } else if (singleAction.startsWith("op_command: ") && player != null) {
            CommonUtil.dispatchOpCommand(player, singleAction.substring(12));
        }
    }

    private String replacePlaceholder(String str, Player player, Block block, Location startLocation, ObjectPlaceCheck totem){
        if (Objects.nonNull(player)) {
                str = str.replace("%world%", block.getWorld().getName())
                        .replace("%block_x%", String.valueOf(block.getX()))
                        .replace("%block_y%", String.valueOf(block.getY()))
                        .replace("%block_z%", String.valueOf(block.getZ()))
                        .replace("%player_x%", String.valueOf(player.getLocation().getX()))
                        .replace("%player_y%", String.valueOf(player.getLocation().getY()))
                        .replace("%player_z%", String.valueOf(player.getLocation().getZ()))
                        .replace("%player_pitch%", String.valueOf(player.getLocation().getPitch()))
                        .replace("%player_yaw%", String.valueOf(player.getLocation().getYaw()))
                        .replace("%player%", player.getName())
                        .replace("%totem_start_x%", String.valueOf(startLocation.getX()))
                        .replace("%totem_start_y%", String.valueOf(startLocation.getY()))
                        .replace("%totem_start_z%", String.valueOf(startLocation.getZ()))
                        .replace("%totem_column%", String.valueOf(totem.getColumn()))
                        .replace("%totem_raw%", String.valueOf(totem.getRow()));
            if (CommonUtil.checkPluginLoad("PlaceholderAPI")) {
                str = PlaceholderAPI.setPlaceholders(player, str);
            }
            return str;
        }
        else {
            return str.replace("%world%", block.getWorld().getName())
                    .replace("%block_x%", String.valueOf(block.getX()))
                    .replace("%block_y%", String.valueOf(block.getY()))
                    .replace("%block_z%", String.valueOf(block.getZ()))
                    .replace("%totem_start_x%", String.valueOf(startLocation.getX()))
                    .replace("%totem_start_y%", String.valueOf(startLocation.getY()))
                    .replace("%totem_start_z%", String.valueOf(startLocation.getZ()))
                    .replace("%totem_column%", String.valueOf(totem.getColumn()))
                    .replace("%totem_raw%", String.valueOf(totem.getRow()));
        }
    }
}
