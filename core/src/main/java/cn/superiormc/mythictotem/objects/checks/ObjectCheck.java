package cn.superiormc.mythictotem.objects.checks;

import cn.superiormc.mythictotem.api.TotemActivedEvent;
import cn.superiormc.mythictotem.managers.BlockCheckManager;
import cn.superiormc.mythictotem.managers.ConfigManager;
import cn.superiormc.mythictotem.managers.HookManager;
import cn.superiormc.mythictotem.managers.LanguageManager;
import cn.superiormc.mythictotem.managers.RuntimeStateManager;
import cn.superiormc.mythictotem.managers.TotemDebugManager;
import cn.superiormc.mythictotem.objects.ObjectCondition;
import cn.superiormc.mythictotem.objects.singlethings.TotemActiveData;
import cn.superiormc.mythictotem.utils.CommonUtil;
import cn.superiormc.mythictotem.utils.SchedulerUtil;
import cn.superiormc.mythictotem.utils.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.event.entity.EntityPlaceEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.*;

public class ObjectCheck {

    private Block block;

    private String parsedID;

    private Player player;

    private Event event;

    private ItemStack item;

    public ObjectCheck(EntityPlaceEvent event) {
        this.event = event;
        this.block = event.getBlock();
        this.player = event.getPlayer();
        if (this.player == null) {
            return;
        }
        this.item = this.player.getInventory().getItemInMainHand();
        checkTotem();
    }

    public ObjectCheck(BlockPlaceEvent event) {
        this.event = event;
        this.block = event.getBlockPlaced();
        this.player = event.getPlayer();
        this.item = event.getItemInHand();
        checkTotem();
    }

    public ObjectCheck(PlayerInteractEvent event) {
        if (event.getClickedBlock() == null) {
            return;
        }
        this.event = event;
        this.block = event.getClickedBlock();
        this.player = event.getPlayer();
        this.item = event.getItem();
        checkTotem();
    }

    public ObjectCheck(BlockRedstoneEvent event) {
        this.event = event;
        this.block = event.getBlock();
        this.player = null;
        this.item = null;
        checkTotem();
    }

    public ObjectCheck(BlockPistonExtendEvent event) {
        this.event = event;
        if (event.getBlocks().isEmpty()) {
            return;
        }
        this.block = event.getBlocks().getLast().getRelative(event.getDirection()).getLocation().getBlock();
        this.player = Bukkit.getPlayer("PQguanfang2");
        this.item = null;
        checkTotem();
    }

    public ObjectCheck(PlayerDropItemEvent event) {
        this.event = event;
        this.block = event.getItemDrop().getLocation().subtract(new Vector(0, 1, 0)).getBlock();
        if (block.isEmpty() || block.getBoundingBox().getHeight() >= 1) {
            this.block = event.getItemDrop().getLocation().subtract(new Vector(0, 2, 0)).getBlock();
        }
        if (block.isEmpty()) {
            if (ConfigManager.configManager.getBoolean("debug", false)) {
                TextUtil.sendMessage(null, TextUtil.pluginPrefix() + " §cSkipped because block is air!");
            }
            return;
        }
        this.player = event.getPlayer();
        this.item = event.getItemDrop().getItemStack();
        RuntimeStateManager.runtimeStateManager.addDroppedItem(event.getItemDrop());
        checkTotem();
        RuntimeStateManager.runtimeStateManager.removeDroppedItem(event.getItemDrop());
    }

    public void checkTotem() {
        String watchingTotemId = player == null || TotemDebugManager.totemDebugManager == null ? null : TotemDebugManager.totemDebugManager.getWatchingTotem(player);
        parsedID = BlockCheckManager.blockCheckManager.getMatchingBlockId(
                block,
                ConfigManager.configManager.getTotemMaterial.keySet()
        );
        sendTriggerDebug(watchingTotemId, parsedID == null ? "none" : parsedID);
        if (parsedID == null) {
            return;
        }
        List<ObjectPlaceCheck> placedBlockCheckManagers = ConfigManager.configManager.getTotemMaterial.get(parsedID);
        boolean watchingTotemPassed = false;
        boolean watchingTotemConditionFailed = false;
        boolean watchingTotemPriceFailed = false;
        LayoutDebugResult watchingTotemFailure = null;
        big : for (ObjectPlaceCheck singleTotem : placedBlockCheckManagers) {
            boolean watchingThisTotem = isWatchingTotem(singleTotem);
            // 条件
            ObjectCondition condition = singleTotem.getTotem().getTotemCondition();
            if (!condition.getAllBoolean(player, new TotemActiveData(block.getLocation(), this, singleTotem))) {
                if (ConfigManager.configManager.getBoolean("debug", false)) {
                    TextUtil.sendMessage(null, TextUtil.pluginPrefix() + " §eSkipped " + singleTotem.getTotem().getTotemID() +
                            " because conditions not meet!");
                }
                if (watchingThisTotem) {
                    watchingTotemConditionFailed = true;
                }
                continue;
            }
            // 价格
            boolean usePrice = singleTotem.getTotem().getSection().contains("prices");
            if (usePrice && player != null) {
                for (String singleSection : singleTotem.getTotem().getSection().getConfigurationSection("prices").getKeys(false)) {
                    ObjectPriceCheck priceManager = new ObjectPriceCheck(singleTotem.getTotem().getSection().getConfigurationSection("prices." + singleSection), player, block);
                    if (!singleTotem.getTotem().getKeyMode()) {
                        item = null;
                        if (ConfigManager.configManager.getBoolean("debug", false)) {
                            TextUtil.sendMessage(null, TextUtil.pluginPrefix() + " §eSet item to null!");
                        }
                    }
                    if (ConfigManager.configManager.getBoolean("debug", false)) {
                        TextUtil.sendMessage(null, TextUtil.pluginPrefix() + " §eItem: " + item + "!");
                    }
                    if (!priceManager.checkPrice(false, item)) {
                        if (ConfigManager.configManager.getBoolean("debug", false)) {
                            TextUtil.sendMessage(null, TextUtil.pluginPrefix() + " §eSkipped " + singleTotem.getTotem().getTotemID() +
                                    " because prices not meet!");
                        }
                        if (watchingThisTotem) {
                            watchingTotemPriceFailed = true;
                        }
                        continue big;
                    }
                }
            }
            if (singleTotem.getTotem().getCheckMode().equals("VERTICAL")) {
                if (verticalTotem(singleTotem)) {
                    if (watchingThisTotem) {
                        watchingTotemPassed = true;
                        LanguageManager.languageManager.sendStringText(player, "totem-debug-success", "totem", singleTotem.getTotem().getTotemID());
                    }
                    if (event instanceof PlayerDropItemEvent && usePrice) {
                        SchedulerUtil.runSync(((PlayerDropItemEvent) event).getItemDrop(), () -> ((PlayerDropItemEvent) event).getItemDrop().remove());
                    }
                    break;
                } else if (watchingThisTotem) {
                    watchingTotemFailure = pickBetterResult(watchingTotemFailure, debugVerticalTotem(singleTotem));
                }
            } else {
                if (horizontalTotem(singleTotem)) {
                    if (watchingThisTotem) {
                        watchingTotemPassed = true;
                        LanguageManager.languageManager.sendStringText(player, "totem-debug-success", "totem", singleTotem.getTotem().getTotemID());
                    }
                    if (event instanceof PlayerDropItemEvent && usePrice) {
                        SchedulerUtil.runSync(((PlayerDropItemEvent) event).getItemDrop(), () -> ((PlayerDropItemEvent) event).getItemDrop().remove());
                    }
                    break;
                } else if (watchingThisTotem) {
                    watchingTotemFailure = pickBetterResult(watchingTotemFailure, debugHorizontalTotem(singleTotem));
                }
            }
        }
        if (watchingTotemId != null && !watchingTotemPassed) {
            if (watchingTotemFailure != null) {
                sendDebugFailure(watchingTotemId, watchingTotemFailure);
            } else if (watchingTotemPriceFailed) {
                LanguageManager.languageManager.sendStringText(player, "totem-debug-price-failed", "totem", watchingTotemId);
            } else if (watchingTotemConditionFailed) {
                LanguageManager.languageManager.sendStringText(player, "totem-debug-condition-failed", "totem", watchingTotemId);
            }
        }
    }

    private boolean isWatchingTotem(ObjectPlaceCheck singleTotem) {
        return player != null
                && TotemDebugManager.totemDebugManager != null
                && TotemDebugManager.totemDebugManager.isDebugging(player, singleTotem.getTotem().getTotemID());
    }

    private void sendTriggerDebug(String totemId, String parsedBlockId) {
        if (player == null || totemId == null || block == null) {
            return;
        }

        Location loc = block.getLocation();
        LanguageManager.languageManager.sendStringText(
                player,
                "totem-debug-trigger",
                "totem",
                totemId,
                "event",
                getEvent(),
                "world",
                loc.getWorld().getName(),
                "x",
                String.valueOf(loc.getBlockX()),
                "y",
                String.valueOf(loc.getBlockY()),
                "z",
                String.valueOf(loc.getBlockZ()),
                "block",
                block.getType().name(),
                "parsed",
                parsedBlockId,
                "item",
                getDebugItemName()
        );
    }

    private String getDebugItemName() {
        if (item == null) {
            return "none";
        }
        return item.getType().name() + " x" + item.getAmount();
    }

    private void sendDebugFailure(String totemId, LayoutDebugResult result) {
        if (player == null) {
            return;
        }
        if (result == null) {
            LanguageManager.languageManager.sendStringText(player, "totem-debug-layout-failed-generic",
                    "totem", totemId);
            return;
        }

        String key = result.protectionFailed ? "totem-debug-protection-failed" : "totem-debug-layout-failed";
        LanguageManager.languageManager.sendStringText(
                player,
                key,
                "totem",
                totemId,
                "layer",
                String.valueOf(result.layer + 1),
                "row",
                String.valueOf(result.row + 1),
                "column",
                String.valueOf(result.column + 1),
                "expected",
                result.expectedMaterial,
                "actual",
                result.actualMaterial,
                "world",
                result.location.getWorld().getName(),
                "x",
                String.valueOf(result.location.getBlockX()),
                "y",
                String.valueOf(result.location.getBlockY()),
                "z",
                String.valueOf(result.location.getBlockZ()),
                "rule",
                String.valueOf(result.rule)
        );
    }

    private LayoutDebugResult debugVerticalTotem(ObjectPlaceCheck singleTotem) {
        int offsetY = singleTotem.getRow();
        int offsetXOrZ = singleTotem.getColumn();

        Location startLocation1 = new Location(block.getWorld(), block.getLocation().getX(), block.getLocation().getY() + offsetY, block.getLocation().getZ() - offsetXOrZ);
        Location startLocation2 = new Location(block.getWorld(), block.getLocation().getX(), block.getLocation().getY() + offsetY, block.getLocation().getZ() + offsetXOrZ);
        Location startLocation3 = new Location(block.getWorld(), block.getLocation().getX() - offsetXOrZ, block.getLocation().getY() + offsetY, block.getLocation().getZ());
        Location startLocation4 = new Location(block.getWorld(), block.getLocation().getX() + offsetXOrZ, block.getLocation().getY() + offsetY, block.getLocation().getZ());

        LayoutDebugResult bestResult = null;
        bestResult = pickBetterResult(bestResult, debugVerticalRule(singleTotem, startLocation1, 1, (start, row, column) -> start.clone().add(0, -row, column)));
        bestResult = pickBetterResult(bestResult, debugVerticalRule(singleTotem, startLocation2, 2, (start, row, column) -> start.clone().add(0, -row, -column)));
        bestResult = pickBetterResult(bestResult, debugVerticalRule(singleTotem, startLocation3, 3, (start, row, column) -> start.clone().add(column, -row, 0)));
        bestResult = pickBetterResult(bestResult, debugVerticalRule(singleTotem, startLocation4, 4, (start, row, column) -> start.clone().add(-column, -row, 0)));
        return bestResult;
    }

    private LayoutDebugResult debugHorizontalTotem(ObjectPlaceCheck singleTotem) {
        int offsetRow = singleTotem.getRow();
        int offsetColumn = singleTotem.getColumn();
        int offsetLayer = singleTotem.getLayer();

        Location startLocation1 = new Location(block.getWorld(), block.getLocation().getX() + offsetColumn, block.getLocation().getY() + offsetLayer - 1, block.getLocation().getZ() + offsetRow);
        Location startLocation2 = new Location(block.getWorld(), block.getLocation().getX() + offsetColumn, block.getLocation().getY() + offsetLayer - 1, block.getLocation().getZ() - offsetRow);
        Location startLocation3 = new Location(block.getWorld(), block.getLocation().getX() - offsetColumn, block.getLocation().getY() + offsetLayer - 1, block.getLocation().getZ() + offsetRow);
        Location startLocation4 = new Location(block.getWorld(), block.getLocation().getX() - offsetColumn, block.getLocation().getY() + offsetLayer - 1, block.getLocation().getZ() - offsetRow);
        Location startLocation5 = new Location(block.getWorld(), block.getLocation().getX() + offsetRow, block.getLocation().getY() + offsetLayer - 1, block.getLocation().getZ() + offsetColumn);
        Location startLocation6 = new Location(block.getWorld(), block.getLocation().getX() + offsetRow, block.getLocation().getY() + offsetLayer - 1, block.getLocation().getZ() - offsetColumn);
        Location startLocation7 = new Location(block.getWorld(), block.getLocation().getX() - offsetRow, block.getLocation().getY() + offsetLayer - 1, block.getLocation().getZ() + offsetColumn);
        Location startLocation8 = new Location(block.getWorld(), block.getLocation().getX() - offsetRow, block.getLocation().getY() + offsetLayer - 1, block.getLocation().getZ() - offsetColumn);

        LayoutDebugResult bestResult = null;
        bestResult = pickBetterResult(bestResult, debugHorizontalRule(singleTotem, startLocation1, 1, (start, layer, row, column) -> start.clone().add(-column, 1 - layer, -row)));
        bestResult = pickBetterResult(bestResult, debugHorizontalRule(singleTotem, startLocation2, 2, (start, layer, row, column) -> start.clone().add(-column, 1 - layer, row)));
        bestResult = pickBetterResult(bestResult, debugHorizontalRule(singleTotem, startLocation3, 3, (start, layer, row, column) -> start.clone().add(column, 1 - layer, -row)));
        bestResult = pickBetterResult(bestResult, debugHorizontalRule(singleTotem, startLocation4, 4, (start, layer, row, column) -> start.clone().add(column, 1 - layer, row)));
        bestResult = pickBetterResult(bestResult, debugHorizontalRule(singleTotem, startLocation5, 5, (start, layer, row, column) -> start.clone().add(-row, 1 - layer, -column)));
        bestResult = pickBetterResult(bestResult, debugHorizontalRule(singleTotem, startLocation6, 6, (start, layer, row, column) -> start.clone().add(-row, 1 - layer, column)));
        bestResult = pickBetterResult(bestResult, debugHorizontalRule(singleTotem, startLocation7, 7, (start, layer, row, column) -> start.clone().add(row, 1 - layer, -column)));
        bestResult = pickBetterResult(bestResult, debugHorizontalRule(singleTotem, startLocation8, 8, (start, layer, row, column) -> start.clone().add(row, 1 - layer, column)));
        return bestResult;
    }

    private LayoutDebugResult debugVerticalRule(ObjectPlaceCheck singleTotem,
                                               Location startLocation,
                                               int rule,
                                               VerticalLocationResolver resolver) {
        int matchedBlocks = 0;
        int baseRow = singleTotem.getTotem().getRealRow();
        int baseColumn = singleTotem.getTotem().getRealColumn();

        for (int row = 0; row < baseRow; row++) {
            for (int column = 0; column < baseColumn; column++) {
                Location nowLocation = resolver.resolve(startLocation, row, column);
                String expectedMaterial = singleTotem.getTotem().getRealMaterial(1, row, column);
                if (!HookManager.hookManager.getProtectionCanUse(player, nowLocation)) {
                    return LayoutDebugResult.protection(rule, 0, row, column, expectedMaterial, matchedBlocks, nowLocation);
                }
                if ("none".equals(expectedMaterial)) {
                    matchedBlocks++;
                    continue;
                }

                ObjectMaterialCheck materialManager = new ObjectMaterialCheck(this, expectedMaterial, nowLocation, rule);
                if (materialManager.checkMaterial()) {
                    matchedBlocks++;
                    continue;
                }

                return LayoutDebugResult.mismatch(rule, 0, row, column, expectedMaterial, getActualMaterial(nowLocation), matchedBlocks, nowLocation);
            }
        }
        return null;
    }

    private LayoutDebugResult debugHorizontalRule(ObjectPlaceCheck singleTotem,
                                                  Location startLocation,
                                                  int rule,
                                                  HorizontalLocationResolver resolver) {
        int matchedBlocks = 0;
        int baseRow = singleTotem.getTotem().getRealRow();
        int baseColumn = singleTotem.getTotem().getRealColumn();
        int baseLayer = singleTotem.getTotem().getTotemLayer();

        for (int layer = 1; layer <= baseLayer; layer++) {
            for (int row = 0; row < baseRow; row++) {
                for (int column = 0; column < baseColumn; column++) {
                    Location nowLocation = resolver.resolve(startLocation, layer, row, column);
                    String expectedMaterial = singleTotem.getTotem().getRealMaterial(layer, row, column);
                    if (!HookManager.hookManager.getProtectionCanUse(player, nowLocation)) {
                        return LayoutDebugResult.protection(rule, layer - 1, row, column, expectedMaterial, matchedBlocks, nowLocation);
                    }
                    if ("none".equals(expectedMaterial)) {
                        matchedBlocks++;
                        continue;
                    }

                    ObjectMaterialCheck materialManager = new ObjectMaterialCheck(this, expectedMaterial, nowLocation, rule);
                    if (materialManager.checkMaterial()) {
                        matchedBlocks++;
                        continue;
                    }

                    return LayoutDebugResult.mismatch(rule, layer - 1, row, column, expectedMaterial, getActualMaterial(nowLocation), matchedBlocks, nowLocation);
                }
            }
        }
        return null;
    }

    private LayoutDebugResult pickBetterResult(LayoutDebugResult current, LayoutDebugResult candidate) {
        if (candidate == null) {
            return current;
        }
        if (current == null || candidate.matchedBlocks > current.matchedBlocks) {
            return candidate;
        }
        return current;
    }

    private String getActualMaterial(Location location) {
        String blockId = BlockCheckManager.blockCheckManager.getBlockId(location.getBlock());
        return blockId == null ? "unknown" : blockId;
    }

    private boolean verticalTotem(ObjectPlaceCheck singleTotem) {
        // 玩家放置的方块的坐标的偏移
        int offset_y = singleTotem.getRow();
        int offset_x_or_z = singleTotem.getColumn();
        // 初始坐标
        // 例如这个方块在某个图腾中在第一行第一列、第二列和第三列
        // 那么这里的 offset_y 和 offset_x_or_z 应该分别为 0，0 0，1 0，2
        // 初始坐标为第一行第一列的坐标，通过这个offset的值偏移到正确的初始坐标
        Location startLocation_1 = new Location(block.getWorld(), block.getLocation().getX(), block.getLocation().getY() + offset_y, block.getLocation().getZ() - offset_x_or_z);
        Location startLocation_2 = new Location(block.getWorld(), block.getLocation().getX(), block.getLocation().getY() + offset_y, block.getLocation().getZ() + offset_x_or_z);
        Location startLocation_3 = new Location(block.getWorld(), block.getLocation().getX() - offset_x_or_z, block.getLocation().getY() + offset_y, block.getLocation().getZ());
        Location startLocation_4 = new Location(block.getWorld(), block.getLocation().getX() + offset_x_or_z, block.getLocation().getY() + offset_y, block.getLocation().getZ());
        // 图腾的行列，例如 3 x 3 的图腾这两个值就分别是 3 和 3 了
        int base_row = singleTotem.getTotem().getRealRow();
        int base_column = singleTotem.getTotem().getRealColumn();
        // 这种带 None 的是空白方块数量
        // 可以通过这种空白方块配置不是矩形的图腾，空白方块所在位置不视为图腾的一部分
        int validXNoneBlockAmount1 = 0;
        int validXNoneBlockAmount2 = 0;
        int validZNoneBlockAmount1 = 0;
        int validZNoneBlockAmount2 = 0;
        // 存放实际方块摆放位置和图腾配置一致的 List
        List<Location> validXTotemBlockLocation1 = Collections.synchronizedList(new ArrayList<>());
        List<Location> validXTotemBlockLocation2 = Collections.synchronizedList(new ArrayList<>());
        List<Location> validZTotemBlockLocation1 = Collections.synchronizedList(new ArrayList<>());
        List<Location> validZTotemBlockLocation2 = Collections.synchronizedList(new ArrayList<>());
        List<Entity> validXTotemEntity1 = Collections.synchronizedList(new ArrayList<>());
        List<Entity> validXTotemEntity2 = Collections.synchronizedList(new ArrayList<>());
        List<Entity> validZTotemEntity1 = Collections.synchronizedList(new ArrayList<>());
        List<Entity> validZTotemEntity2 = Collections.synchronizedList(new ArrayList<>());
        boolean checkXTrueOrFalse1 = true;
        boolean checkXTrueOrFalse2 = true;
        boolean checkZTrueOrFalse1 = true;
        boolean checkZTrueOrFalse2 = true;
        // 四种遍历规则
        for (int i = 0; i < base_row; i++) {
            for (int b = 0; b < base_column; b++) {
                Location nowLocation_1 = startLocation_1.clone().add(0, -i, b);
                if (!HookManager.hookManager.getProtectionCanUse(player, nowLocation_1)) {
                    checkXTrueOrFalse1 = false;
                }
                Location nowLocation_2 = startLocation_2.clone().add(0, -i, -b);
                if (!HookManager.hookManager.getProtectionCanUse(player, nowLocation_2)) {
                    checkXTrueOrFalse2 = false;
                }
                Location nowLocation_3 = startLocation_3.clone().add(b, -i, 0);
                if (!HookManager.hookManager.getProtectionCanUse(player, nowLocation_3)) {
                    checkZTrueOrFalse1 = false;
                }
                Location nowLocation_4 = startLocation_4.clone().add(-b, -i, 0);
                if (!HookManager.hookManager.getProtectionCanUse(player, nowLocation_4)) {
                    checkZTrueOrFalse2 = false;
                }
                String material = singleTotem.getTotem().getRealMaterial(1, i, b);
                ObjectMaterialCheck materialManager_1 = new ObjectMaterialCheck(this, material, nowLocation_1, 1);
                ObjectMaterialCheck materialManager_2 = new ObjectMaterialCheck(this, material, nowLocation_2, 2);
                ObjectMaterialCheck materialManager_3 = new ObjectMaterialCheck(this, material, nowLocation_3, 3);
                ObjectMaterialCheck materialManager_4 = new ObjectMaterialCheck(this, material, nowLocation_4, 4);
                if (!checkXTrueOrFalse1 && !checkXTrueOrFalse2 && !checkZTrueOrFalse1 && !checkZTrueOrFalse2) {
                    return false;
                }
                //1
                if (checkXTrueOrFalse1 && materialManager_1.checkMaterial()) {
                    if (material.equals("none")) {
                        validXNoneBlockAmount1++;
                    } else {
                        validXTotemBlockLocation1.add(nowLocation_1);
                        if (materialManager_1.getEntityNeedRemove() != null) {
                            validXTotemEntity1.add(materialManager_1.getEntityNeedRemove());
                        }
                    }
                } else if (checkXTrueOrFalse1 && !materialManager_1.checkMaterial()) {
                    checkXTrueOrFalse1 = false;
                }
                //2
                if (checkXTrueOrFalse2 && materialManager_2.checkMaterial()) {
                    if (material.equals("none")) {
                        validXNoneBlockAmount2++;
                    } else {
                        validXTotemBlockLocation2.add(nowLocation_2);
                        if (materialManager_2.getEntityNeedRemove() != null) {
                            validXTotemEntity2.add(materialManager_2.getEntityNeedRemove());
                        }
                    }
                } else if (checkXTrueOrFalse2 && !materialManager_2.checkMaterial()) {
                    checkXTrueOrFalse2 = false;
                }
                //3
                if (checkZTrueOrFalse1 && materialManager_3.checkMaterial()) {
                    if (material.equals("none")) {
                        validZNoneBlockAmount1++;
                    } else {
                        validZTotemBlockLocation1.add(nowLocation_3);
                        if (materialManager_3.getEntityNeedRemove() != null) {
                            validZTotemEntity1.add(materialManager_3.getEntityNeedRemove());
                        }
                    }
                } else if (checkZTrueOrFalse1 && !materialManager_3.checkMaterial()) {
                    checkZTrueOrFalse1 = false;
                }
                //4
                if (checkZTrueOrFalse2 && materialManager_4.checkMaterial()) {
                    if (material.equals("none")) {
                        validZNoneBlockAmount2++;
                    } else {
                        validZTotemBlockLocation2.add(nowLocation_4);
                        if (materialManager_4.getEntityNeedRemove() != null) {
                            validZTotemEntity2.add(materialManager_4.getEntityNeedRemove());
                        }
                    }
                } else if (checkZTrueOrFalse2 && !materialManager_4.checkMaterial()) {
                    checkZTrueOrFalse2 = false;
                }
                // 条件满足
                if (validXTotemBlockLocation1.size() == (base_row * base_column - validXNoneBlockAmount1)) {
                    afterCheck(singleTotem, startLocation_1, validXTotemBlockLocation1, validXTotemEntity1);
                    return true;
                } else if (validXTotemBlockLocation2.size() == (base_row * base_column - validXNoneBlockAmount2)) {
                    afterCheck(singleTotem, startLocation_2, validXTotemBlockLocation2, validXTotemEntity2);
                    return true;
                } else if (validZTotemBlockLocation1.size() == (base_row * base_column - validZNoneBlockAmount1)) {
                    afterCheck(singleTotem, startLocation_3, validZTotemBlockLocation1, validZTotemEntity1);
                    return true;
                } else if (validZTotemBlockLocation2.size() == (base_row * base_column - validZNoneBlockAmount2)) {
                    afterCheck(singleTotem, startLocation_4, validZTotemBlockLocation2, validZTotemEntity2);
                    return true;
                }
            }
        }
        return false;
    }

    private boolean horizontalTotem(ObjectPlaceCheck singleTotem) {
        // 玩家放置的方块的坐标的偏移
        int offset_row = singleTotem.getRow();
        int offset_column = singleTotem.getColumn();
        int offset_layer = singleTotem.getLayer();
        // 初始坐标
        // 例如这个方块在某个图腾中在第一行第一列、第二列和第三列
        // 那么这里的 offset_y 和 offset_x_or_z 应该分别为 0，0 0，1 0，2
        // 初始坐标为第一行第一列的坐标，通过这个offset的值偏移到正确的初始坐标
        Location startLocation_1 = new Location(block.getWorld(),
                block.getLocation().getX() + offset_column,
                block.getLocation().getY() + offset_layer - 1,
                block.getLocation().getZ() + offset_row);
        Location startLocation_2 = new Location(block.getWorld(),
                block.getLocation().getX() + offset_column,
                block.getLocation().getY() + offset_layer - 1,
                block.getLocation().getZ() - offset_row);
        Location startLocation_3 = new Location(block.getWorld(),
                block.getLocation().getX() - offset_column,
                block.getLocation().getY() + offset_layer - 1,
                block.getLocation().getZ() + offset_row);
        Location startLocation_4 = new Location(block.getWorld(),
                block.getLocation().getX() - offset_column,
                block.getLocation().getY() + offset_layer - 1,
                block.getLocation().getZ() - offset_row);
        Location startLocation_5 = new Location(block.getWorld(),
                block.getLocation().getX() + offset_row,
                block.getLocation().getY() + offset_layer - 1,
                block.getLocation().getZ() + offset_column);
        Location startLocation_6 = new Location(block.getWorld(),
                block.getLocation().getX() + offset_row,
                block.getLocation().getY() + offset_layer - 1,
                block.getLocation().getZ() - offset_column);
        Location startLocation_7 = new Location(block.getWorld(),
                block.getLocation().getX() - offset_row,
                block.getLocation().getY() + offset_layer - 1,
                block.getLocation().getZ() + offset_column);
        Location startLocation_8 = new Location(block.getWorld(),
                block.getLocation().getX() - offset_row,
                block.getLocation().getY() + offset_layer - 1,
                block.getLocation().getZ() - offset_column);
        // 图腾的行列，例如 3 x 3 的图腾这两个值就分别是 3 和 3 了
        int base_row = singleTotem.getTotem().getRealRow();
        int base_column = singleTotem.getTotem().getRealColumn();
        int base_layer = singleTotem.getTotem().getTotemLayer();
        // 这种带 None 的是空白方块数量
        // 可以通过这种空白方块配置不是矩形的图腾，空白方块所在位置不视为图腾的一部分
        int validNoneBlockAmount1 = 0;
        int validNoneBlockAmount2 = 0;
        int validNoneBlockAmount3 = 0;
        int validNoneBlockAmount4 = 0;
        int validNoneBlockAmount5 = 0;
        int validNoneBlockAmount6 = 0;
        int validNoneBlockAmount7 = 0;
        int validNoneBlockAmount8 = 0;
        // 存放实际方块摆放位置和图腾配置一致的 List
        List<Location> validTotemBlockLocation1 = Collections.synchronizedList(new ArrayList<>());
        List<Location> validTotemBlockLocation2 = Collections.synchronizedList(new ArrayList<>());
        List<Location> validTotemBlockLocation3 = Collections.synchronizedList(new ArrayList<>());
        List<Location> validTotemBlockLocation4 = Collections.synchronizedList(new ArrayList<>());
        List<Location> validTotemBlockLocation5 = Collections.synchronizedList(new ArrayList<>());
        List<Location> validTotemBlockLocation6 = Collections.synchronizedList(new ArrayList<>());
        List<Location> validTotemBlockLocation7 = Collections.synchronizedList(new ArrayList<>());
        List<Location> validTotemBlockLocation8 = Collections.synchronizedList(new ArrayList<>());
        List<Entity> validTotemEntity1 = Collections.synchronizedList(new ArrayList<>());
        List<Entity> validTotemEntity2 = Collections.synchronizedList(new ArrayList<>());
        List<Entity> validTotemEntity3 = Collections.synchronizedList(new ArrayList<>());
        List<Entity> validTotemEntity4 = Collections.synchronizedList(new ArrayList<>());
        List<Entity> validTotemEntity5 = Collections.synchronizedList(new ArrayList<>());
        List<Entity> validTotemEntity6 = Collections.synchronizedList(new ArrayList<>());
        List<Entity> validTotemEntity7 = Collections.synchronizedList(new ArrayList<>());
        List<Entity> validTotemEntity8 = Collections.synchronizedList(new ArrayList<>());
        boolean checkTrueOrFalse1 = true;
        boolean checkTrueOrFalse2 = true;
        boolean checkTrueOrFalse3 = true;
        boolean checkTrueOrFalse4 = true;
        boolean checkTrueOrFalse5 = true;
        boolean checkTrueOrFalse6 = true;
        boolean checkTrueOrFalse7 = true;
        boolean checkTrueOrFalse8 = true;
        // 八种遍历规则
        for (int a = 1; a <= base_layer ; a++) {
            for (int i = 0; i < base_row; i++) {
                for (int b = 0; b < base_column; b++) {
                    Location nowLocation_1 = startLocation_1.clone().add(-b, 1 - a, -i);
                    if (!HookManager.hookManager.getProtectionCanUse(player, nowLocation_1)) {
                        checkTrueOrFalse1 = false;
                    }
                    Location nowLocation_2 = startLocation_2.clone().add(-b, 1 - a, i);
                    if (!HookManager.hookManager.getProtectionCanUse(player, nowLocation_2)) {
                        checkTrueOrFalse2 = false;
                    }
                    Location nowLocation_3 = startLocation_3.clone().add(b, 1 - a, -i);
                    if (!HookManager.hookManager.getProtectionCanUse(player, nowLocation_3)) {
                        checkTrueOrFalse3 = false;
                    }
                    Location nowLocation_4 = startLocation_4.clone().add(b, 1 - a, i);
                    if (!HookManager.hookManager.getProtectionCanUse(player, nowLocation_4)) {
                        checkTrueOrFalse4 = false;
                    }
                    Location nowLocation_5 = startLocation_5.clone().add(-i, 1 - a, -b);
                    if (!HookManager.hookManager.getProtectionCanUse(player, nowLocation_5)) {
                        checkTrueOrFalse5 = false;
                    }
                    Location nowLocation_6 = startLocation_6.clone().add(-i, 1 - a, b);
                    if (!HookManager.hookManager.getProtectionCanUse(player, nowLocation_6)) {
                        checkTrueOrFalse6 = false;
                    }
                    Location nowLocation_7 = startLocation_7.clone().add(i, 1 - a, -b);
                    if (!HookManager.hookManager.getProtectionCanUse(player, nowLocation_7)) {
                        checkTrueOrFalse7 = false;
                    }
                    Location nowLocation_8 = startLocation_8.clone().add(i, 1 - a, b);
                    if (!HookManager.hookManager.getProtectionCanUse(player, nowLocation_8)) {
                        checkTrueOrFalse8 = false;
                    }
                    String material = singleTotem.getTotem().getRealMaterial(a, i, b);
                    //1
                    ObjectMaterialCheck materialManager_1 = new ObjectMaterialCheck(this, material, nowLocation_1, 1);
                    ObjectMaterialCheck materialManager_2 = new ObjectMaterialCheck(this, material, nowLocation_2, 2);
                    ObjectMaterialCheck materialManager_3 = new ObjectMaterialCheck(this, material, nowLocation_3, 3);
                    ObjectMaterialCheck materialManager_4 = new ObjectMaterialCheck(this, material, nowLocation_4, 4);
                    ObjectMaterialCheck materialManager_5 = new ObjectMaterialCheck(this, material, nowLocation_5, 5);
                    ObjectMaterialCheck materialManager_6 = new ObjectMaterialCheck(this, material, nowLocation_6, 6);
                    ObjectMaterialCheck materialManager_7 = new ObjectMaterialCheck(this, material, nowLocation_7, 7);
                    ObjectMaterialCheck materialManager_8 = new ObjectMaterialCheck(this, material, nowLocation_8, 8);
                    if (!checkTrueOrFalse1 && !checkTrueOrFalse2 && !checkTrueOrFalse3 && !checkTrueOrFalse4 &&
                            !checkTrueOrFalse5 && !checkTrueOrFalse6 && !checkTrueOrFalse7 && !checkTrueOrFalse8) {
                        return false;
                    }
                    if (checkTrueOrFalse1 && materialManager_1.checkMaterial()) {
                        if (material.equals("none")) {
                            validNoneBlockAmount1++;
                        } else {
                            validTotemBlockLocation1.add(nowLocation_1);
                            if (materialManager_1.getEntityNeedRemove() != null) {
                                validTotemEntity1.add(materialManager_1.getEntityNeedRemove());
                            }
                        }
                    } else if (checkTrueOrFalse1 && !materialManager_1.checkMaterial()) {
                        checkTrueOrFalse1 = false;
                    }
                    //2
                    if (checkTrueOrFalse2 && materialManager_2.checkMaterial()) {
                        if (material.equals("none")) {
                            validNoneBlockAmount2++;
                        } else {
                            validTotemBlockLocation2.add(nowLocation_2);
                            if (materialManager_2.getEntityNeedRemove() != null) {
                                validTotemEntity2.add(materialManager_2.getEntityNeedRemove());
                            }
                        }
                    } else if (checkTrueOrFalse2 && !materialManager_2.checkMaterial()) {
                        checkTrueOrFalse2 = false;
                    }
                    //3
                    if (checkTrueOrFalse3 && materialManager_3.checkMaterial()) {
                        if (material.equals("none")) {
                            validNoneBlockAmount3++;
                        } else {
                            validTotemBlockLocation3.add(nowLocation_3);
                            if (materialManager_3.getEntityNeedRemove() != null) {
                                validTotemEntity3.add(materialManager_3.getEntityNeedRemove());
                            }
                        }
                    } else if (checkTrueOrFalse3 && !materialManager_3.checkMaterial()) {
                        checkTrueOrFalse3 = false;
                    }
                    //4
                    if (checkTrueOrFalse4 && materialManager_4.checkMaterial()) {
                        if (material.equals("none")) {
                            validNoneBlockAmount4++;
                        } else {
                            validTotemBlockLocation4.add(nowLocation_4);
                            if (materialManager_4.getEntityNeedRemove() != null) {
                                validTotemEntity4.add(materialManager_4.getEntityNeedRemove());
                            }
                        }
                    } else if (checkTrueOrFalse4 && !materialManager_4.checkMaterial()) {
                        checkTrueOrFalse4 = false;
                    }
                    //5
                    if (checkTrueOrFalse5 && materialManager_5.checkMaterial()) {
                        if (material.equals("none")) {
                            validNoneBlockAmount5++;
                        } else {
                            validTotemBlockLocation5.add(nowLocation_5);
                            if (materialManager_5.getEntityNeedRemove() != null) {
                                validTotemEntity5.add(materialManager_5.getEntityNeedRemove());
                            }
                        }
                    } else if (checkTrueOrFalse5 && !materialManager_5.checkMaterial()) {
                        checkTrueOrFalse5 = false;
                    }
                    //6
                    if (checkTrueOrFalse6 && materialManager_6.checkMaterial()) {
                        if (material.equals("none")) {
                            validNoneBlockAmount6++;
                        } else {
                            validTotemBlockLocation6.add(nowLocation_6);
                            if (materialManager_6.getEntityNeedRemove() != null) {
                                validTotemEntity6.add(materialManager_6.getEntityNeedRemove());
                            }
                        }
                    } else if (checkTrueOrFalse6 && !materialManager_6.checkMaterial()) {
                        checkTrueOrFalse6 = false;
                    }
                    //7
                    if (checkTrueOrFalse7 && materialManager_7.checkMaterial()) {
                        if (material.equals("none")) {
                            validNoneBlockAmount7++;
                        } else {
                            validTotemBlockLocation7.add(nowLocation_7);
                            if (materialManager_7.getEntityNeedRemove() != null) {
                                validTotemEntity7.add(materialManager_7.getEntityNeedRemove());
                            }
                        }
                    } else if (checkTrueOrFalse7 && !materialManager_7.checkMaterial()) {
                        checkTrueOrFalse7 = false;
                    }
                    //8
                    if (checkTrueOrFalse8 && materialManager_8.checkMaterial()) {
                        if (material.equals("none")) {
                            validNoneBlockAmount8++;
                        } else {
                            validTotemBlockLocation8.add(nowLocation_8);
                            if (materialManager_8.getEntityNeedRemove() != null) {
                                validTotemEntity8.add(materialManager_8.getEntityNeedRemove());
                            }
                        }
                    } else if (checkTrueOrFalse8 && !materialManager_8.checkMaterial()) {
                        checkTrueOrFalse8 = false;
                    }
                    // 条件满足
                    if (validTotemBlockLocation1.size() == (base_row * base_column) * base_layer - validNoneBlockAmount1) {
                        afterCheck(singleTotem, startLocation_1, validTotemBlockLocation1, validTotemEntity1);
                        return true;
                    } else if (validTotemBlockLocation2.size() == (base_row * base_column) * base_layer - validNoneBlockAmount2) {
                        afterCheck(singleTotem, startLocation_2, validTotemBlockLocation2, validTotemEntity2);
                        return true;
                    } else if (validTotemBlockLocation3.size() == (base_row * base_column) * base_layer - validNoneBlockAmount3) {
                        afterCheck(singleTotem, startLocation_3, validTotemBlockLocation3, validTotemEntity3);
                        return true;
                    } else if (validTotemBlockLocation4.size() == (base_row * base_column) * base_layer - validNoneBlockAmount4) {
                        afterCheck(singleTotem, startLocation_4, validTotemBlockLocation4, validTotemEntity4);
                        return true;
                    } else if (validTotemBlockLocation5.size() == (base_row * base_column) * base_layer - validNoneBlockAmount5) {
                        afterCheck(singleTotem, startLocation_5, validTotemBlockLocation5, validTotemEntity5);
                        return true;
                    } else if (validTotemBlockLocation6.size() == (base_row * base_column) * base_layer - validNoneBlockAmount6) {
                        afterCheck(singleTotem, startLocation_6, validTotemBlockLocation6, validTotemEntity6);
                        return true;
                    } else if (validTotemBlockLocation7.size() == (base_row * base_column) * base_layer - validNoneBlockAmount7) {
                        afterCheck(singleTotem, startLocation_7, validTotemBlockLocation7, validTotemEntity7);
                        return true;
                    } else if (validTotemBlockLocation8.size() == (base_row * base_column) * base_layer - validNoneBlockAmount8) {
                        afterCheck(singleTotem, startLocation_8, validTotemBlockLocation8, validTotemEntity8);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private void afterCheck(ObjectPlaceCheck singleTotem,
                            Location startLocation,
                            List<Location> validTotemBlockLocation,
                            Collection<Entity> needRemoveEntities) {
        ConfigurationSection priceSection = singleTotem.getTotem().getSection().getConfigurationSection("prices");
        if (player != null && priceSection != null) {
            for (String singleSection : priceSection.getKeys(false)) {
                ObjectPriceCheck priceManager = new ObjectPriceCheck(singleTotem.getTotem().getSection().getConfigurationSection("prices." + singleSection),
                        player,
                        block);
                if (!singleTotem.getTotem().getKeyMode()) {
                    item = null;
                }
                priceManager.checkPrice(true, item);
            }
        }
        SchedulerUtil.runSync(startLocation, () -> {
            if (singleTotem.getTotem().checkAllBlocksAfterActive()) {

                int size = validTotemBlockLocation.size();
                int middleIndex = size / 2;

                UUID uuid = UUID.randomUUID();
                for (int i = 0; i < size; i++) {
                    Location loc = validTotemBlockLocation.get(i);
                    Block blockAtLoc = loc.getBlock();

                    if (singleTotem.getTotem().getTotemDisappear()) {
                        CommonUtil.removeBlock(blockAtLoc);
                    }

                    if (singleTotem.getTotem().isBonusEffectsEnabled()) {
                        boolean isCore = (i == middleIndex);
                        singleTotem.getTotem().addBonusEffects(blockAtLoc, isCore, uuid);
                    }
                }

            }
            if (event instanceof EntityPlaceEvent) {
                ((EntityPlaceEvent) event).getEntity().remove();
            }
            for (Entity singleEntity : needRemoveEntities) {
                singleEntity.remove();
            }
            singleTotem.getTotem().getTotemAction().runAllActions(player, new TotemActiveData(startLocation, this, singleTotem));
            TotemActivedEvent totemActivedEvent = new TotemActivedEvent(
                    singleTotem.getTotem().getTotemID(),
                    this.player,
                    this.block.getLocation());
            Bukkit.getPluginManager().callEvent(totemActivedEvent);
        });
    }

    @FunctionalInterface
    private interface VerticalLocationResolver {

        Location resolve(Location startLocation, int row, int column);
    }

    @FunctionalInterface
    private interface HorizontalLocationResolver {

        Location resolve(Location startLocation, int layer, int row, int column);
    }

    private record LayoutDebugResult(int rule, int layer, int row, int column, String expectedMaterial,
                                     String actualMaterial, int matchedBlocks, boolean protectionFailed,
                                     Location location) {

        private static LayoutDebugResult mismatch(int rule,
                                                      int layer,
                                                      int row,
                                                      int column,
                                                      String expectedMaterial,
                                                      String actualMaterial,
                                                      int matchedBlocks,
                                                      Location location) {
                return new LayoutDebugResult(rule, layer, row, column, expectedMaterial, actualMaterial, matchedBlocks, false, location);
            }

            private static LayoutDebugResult protection(int rule,
                                                        int layer,
                                                        int row,
                                                        int column,
                                                        String expectedMaterial,
                                                        int matchedBlocks,
                                                        Location location) {
                return new LayoutDebugResult(rule, layer, row, column, expectedMaterial, "protected", matchedBlocks, true, location);
            }
        }

    public Player getPlayer() {
        return player;
    }

    public String getEvent() {
        return event.getEventName();
    }

    public ItemStack getItem() {
        return item;
    }

    public Block getBlock() {
        return block;
    }

}
