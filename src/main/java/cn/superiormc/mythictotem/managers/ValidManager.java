package cn.superiormc.mythictotem.managers;

import cn.superiormc.mythictotem.MythicTotem;
import cn.superiormc.mythictotem.utils.CheckPluginLoad;
import cn.superiormc.mythictotem.utils.RemoveBlock;
import dev.lone.itemsadder.api.CustomBlock;
import io.th0rgal.oraxen.api.OraxenBlocks;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ValidManager {

    private Block block;

    private Player player;

    public ValidManager(BlockPlaceEvent event){
        this.block = event.getBlockPlaced();
        this.player = event.getPlayer();
        CheckTotem();
    }

    public ValidManager(PlayerInteractEvent event){
        if (event.getClickedBlock() == null) {
            return;
        }
        this.block = event.getClickedBlock();
        this.player = event.getPlayer();
        CheckTotem();
    }

    public void CheckTotem() {
        if (MythicTotem.getCheckingBlock.contains(block)) {
            return;
        }
        List<PlacedBlockCheckManager> placedBlockCheckManagers = new ArrayList<>();
        MythicTotem.getCheckingBlock.add(block);
        // 处理 ItemsAdder 方块
        if (CheckPluginLoad.DoIt("ItemsAdder")) {
            if (CustomBlock.byAlreadyPlaced(block) != null &&
                    MythicTotem.getTotemMaterial.containsKey("itemsadder:" + CustomBlock.byAlreadyPlaced(block).getNamespacedID())) {
                placedBlockCheckManagers = MythicTotem.getTotemMaterial.get("itemsadder:" + CustomBlock.byAlreadyPlaced(block).getNamespacedID());
            }
        }
        // 处理 Oraxen 方块
        if (placedBlockCheckManagers.size() == 0 && CheckPluginLoad.DoIt("Oraxen")) {
            if ((OraxenBlocks.isOraxenBlock(block)) && OraxenBlocks.getNoteBlockMechanic(block).getItemID() != null &&
                (MythicTotem.getTotemMaterial.containsKey("oraxen:" + OraxenBlocks.getNoteBlockMechanic(block).getItemID()))){
                placedBlockCheckManagers = MythicTotem.getTotemMaterial.get("oraxen:" + OraxenBlocks.getNoteBlockMechanic(block).getItemID());
            }
            else if((OraxenBlocks.isOraxenBlock(block)) && OraxenBlocks.getStringMechanic(block).getItemID() != null &&
                (MythicTotem.getTotemMaterial.containsKey("oraxen:" + OraxenBlocks.getStringMechanic(block).getItemID()))) {
                placedBlockCheckManagers = MythicTotem.getTotemMaterial.get("oraxen:" + OraxenBlocks.getStringMechanic(block).getItemID());
            }
        }
        // 处理原版方块
        if (placedBlockCheckManagers.size() == 0 && MythicTotem.getTotemMaterial.containsKey("minecraft:" + block.getType().toString().toLowerCase())) {
            placedBlockCheckManagers = MythicTotem.getTotemMaterial.get("minecraft:" + block.getType().toString().toLowerCase());
        }
        for (PlacedBlockCheckManager singleTotem : placedBlockCheckManagers) {
            ConditionManager conditionManager = new ConditionManager(singleTotem.GetTotemManager().GetTotemCondition(), player, block);
            if (!conditionManager.CheckCondition()) {
                continue;
            }
            if (singleTotem.GetTotemManager().GetCheckMode().equals("VERTICAL")) {
                VerticalTotem(singleTotem);
            }
            else {;
                for (int i = 1 ; i <= singleTotem.GetTotemManager().GetTotemLayer() ; i++) {
                    if (HorizontalTotem(i, singleTotem)) {
                        break;
                    }
                }
            }
            MythicTotem.getCheckingBlock.remove(block);
        }
    }
    private void VerticalTotem(PlacedBlockCheckManager singleTotem) {
        // 玩家放置的方块的坐标的偏移
        int offset_y = singleTotem.GetRow();
        int offset_x_or_z = singleTotem.GetColumn();
        // 初始坐标
        // 例如这个方块在某个图腾中在第一行第一列、第二列和第三列
        // 那么这里的 offset_y 和 offset_x_or_z 应该分别为 0，0 0，1 0，2
        // 初始坐标为第一行第一列的坐标，通过这个offset的值偏移到正确的初始坐标
        Location startLocation_1 = new Location(block.getWorld(), block.getLocation().getX(), block.getLocation().getY() + offset_y, block.getLocation().getZ() - offset_x_or_z);
        Location startLocation_2 = new Location(block.getWorld(), block.getLocation().getX(), block.getLocation().getY() + offset_y, block.getLocation().getZ() + offset_x_or_z);
        Location startLocation_3 = new Location(block.getWorld(), block.getLocation().getX() - offset_x_or_z, block.getLocation().getY() + offset_y, block.getLocation().getZ());
        Location startLocation_4 = new Location(block.getWorld(), block.getLocation().getX() + offset_x_or_z, block.getLocation().getY() + offset_y, block.getLocation().getZ());
        // 图腾的行列，例如 3 x 3 的图腾这两个值就分别是 3 和 3 了
        int base_row = singleTotem.GetTotemManager().GetRealRow();
        int base_column = singleTotem.GetTotemManager().GetRealColumn();
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
        boolean checkXTrueOrFalse1 = true;
        boolean checkXTrueOrFalse2 = true;
        boolean checkZTrueOrFalse1 = true;
        boolean checkZTrueOrFalse2 = true;
        // 四种遍历规则
        for (int i = 0; i < base_row; i++) {
            for (int b = 0; b < base_column; b++) {
                Location nowLocation_1 = startLocation_1.clone().add(0, -i, b);
                Location nowLocation_2 = startLocation_2.clone().add(0, -i, -b);
                Location nowLocation_3 = startLocation_3.clone().add(b, -i, 0);
                Location nowLocation_4 = startLocation_4.clone().add(-b, -i, 0);
                String material = singleTotem.GetTotemManager().GetRealMaterial(1, i, b);
                MaterialManager materialManager_1 = new MaterialManager(material, nowLocation_1.getBlock());
                MaterialManager materialManager_2 = new MaterialManager(material, nowLocation_2.getBlock());
                MaterialManager materialManager_3 = new MaterialManager(material, nowLocation_3.getBlock());
                MaterialManager materialManager_4 = new MaterialManager(material, nowLocation_4.getBlock());
                if (!checkXTrueOrFalse1 && !checkXTrueOrFalse2 && !checkZTrueOrFalse1 && !checkZTrueOrFalse2) {
                    return;
                }
                //1
                if (checkXTrueOrFalse1 && materialManager_1.CheckMaterial()) {
                    if (material.equals("none")) {
                        validXNoneBlockAmount1++;
                    } else {
                        validXTotemBlockLocation1.add(nowLocation_1);
                    }
                } else if (checkXTrueOrFalse1 && !materialManager_1.CheckMaterial()) {
                    checkXTrueOrFalse1 = false;
                }
                //2
                if (checkXTrueOrFalse2 && materialManager_2.CheckMaterial()) {
                    if (material.equals("none")) {
                        validXNoneBlockAmount2++;
                    } else {
                        validXTotemBlockLocation2.add(nowLocation_2);
                    }
                } else if (checkXTrueOrFalse2 && !materialManager_2.CheckMaterial()) {
                    checkXTrueOrFalse2 = false;
                }
                //3
                if (checkZTrueOrFalse1 && materialManager_3.CheckMaterial()) {
                    if (material.equals("none")) {
                        validZNoneBlockAmount1++;
                    } else {
                        validZTotemBlockLocation1.add(nowLocation_3);
                    }
                } else if (checkZTrueOrFalse1 && !materialManager_3.CheckMaterial()) {
                    checkZTrueOrFalse1 = false;
                }
                //4
                if (checkZTrueOrFalse2 && materialManager_4.CheckMaterial()) {
                    if (material.equals("none")) {
                        validZNoneBlockAmount2++;
                    } else {
                        validZTotemBlockLocation2.add(nowLocation_4);
                    }
                } else if (checkZTrueOrFalse2 && !materialManager_4.CheckMaterial()) {
                    checkZTrueOrFalse2 = false;
                }
                if (MythicTotem.instance.getConfig().getBoolean("settings.debug")) {
                    Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[MythicTotem] §1Rule: X1 §eSize: " +
                            validXTotemBlockLocation1.size());
                    Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[MythicTotem] §bMaterial: " + material + " §dR. C.:" + i + " " + b);
                    Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[MythicTotem] §6Base R. C.: " + base_row + " " + base_column);
                    Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[MythicTotem] §4Start Location: " + startLocation_1);
                    Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[MythicTotem] §9Now Location: " + nowLocation_1 + " " + nowLocation_1.getBlock());
                    Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[MythicTotem] §2Rule: X2 §eSize: " +
                            validXTotemBlockLocation2.size());
                    Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[MythicTotem] §bMaterial: " + material + " §dR. C.:" + i + " " + b);
                    Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[MythicTotem] §6Base R. C.: " + base_row + " " + base_column);
                    Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[MythicTotem] §4Start Location: " + startLocation_2);
                    Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[MythicTotem] §9Now Location: " + nowLocation_2 + " " + nowLocation_2.getBlock());
                    Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[MythicTotem] §3Rule: Z1 §eSize: " +
                            validZTotemBlockLocation1.size());
                    Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[MythicTotem] §bMaterial: " + material + " §dR. C.:" + i + " " + b);
                    Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[MythicTotem] §6Base R. C.: " + base_row + " " + base_column);
                    Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[MythicTotem] §4Start Location: " + startLocation_3);
                    Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[MythicTotem] §9Now Location: " + nowLocation_3 + " " + nowLocation_3.getBlock());
                    Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[MythicTotem] §5Rule: Z2 §eSize: " +
                            validZTotemBlockLocation2.size());
                    Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[MythicTotem] §bMaterial: " + material + " §dR. C.:" + i + " " + b);
                    Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[MythicTotem] §6Base R. C.: " + base_row + " " + base_column);
                    Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[MythicTotem] §4Start Location: " + startLocation_4);
                    Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[MythicTotem] §9Now Location: " + nowLocation_4 + " " + nowLocation_4.getBlock());
                }
                // 条件满足
                if (validXTotemBlockLocation1.size() == (base_row * base_column - validXNoneBlockAmount1)) {
                    AfterCheck(singleTotem, validXTotemBlockLocation1, player, block);
                    return;
                } else if (validXTotemBlockLocation2.size() == (base_row * base_column - validXNoneBlockAmount2)) {
                    AfterCheck(singleTotem, validXTotemBlockLocation2, player, block);
                    return;
                } else if (validZTotemBlockLocation1.size() == (base_row * base_column - validZNoneBlockAmount1)) {
                    AfterCheck(singleTotem, validZTotemBlockLocation1, player, block);
                    return;
                } else if (validZTotemBlockLocation2.size() == (base_row * base_column - validZNoneBlockAmount2)) {
                    AfterCheck(singleTotem, validZTotemBlockLocation2, player, block);
                    return;
                }
            }
        }
    }

    private boolean HorizontalTotem(int offset_layer, PlacedBlockCheckManager singleTotem) {
        // 玩家放置的方块的坐标的偏移
        int offset_row = singleTotem.GetRow();
        int offset_column = singleTotem.GetColumn();
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
        int base_row = singleTotem.GetTotemManager().GetRealRow();
        int base_column = singleTotem.GetTotemManager().GetRealColumn();
        int base_layer = singleTotem.GetTotemManager().GetTotemLayer();
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
                    Location nowLocation_1 = startLocation_1.clone().add(-i, 1 - a, -b);
                    Location nowLocation_2 = startLocation_2.clone().add(-i, 1 - a, b);
                    Location nowLocation_3 = startLocation_3.clone().add(i, 1 - a, -b);
                    Location nowLocation_4 = startLocation_4.clone().add(i, 1 - a, b);
                    Location nowLocation_5 = startLocation_5.clone().add(-b, 1 - a, -i);
                    Location nowLocation_6 = startLocation_6.clone().add(-b, 1 - a, i);
                    Location nowLocation_7 = startLocation_7.clone().add(b, 1 - a, -i);
                    Location nowLocation_8 = startLocation_8.clone().add(b, 1 - a, i);
                    String material = singleTotem.GetTotemManager().GetRealMaterial(a, i, b);
                    //1
                    MaterialManager materialManager_1 = new MaterialManager(material, nowLocation_1.getBlock());
                    MaterialManager materialManager_2 = new MaterialManager(material, nowLocation_2.getBlock());
                    MaterialManager materialManager_3 = new MaterialManager(material, nowLocation_3.getBlock());
                    MaterialManager materialManager_4 = new MaterialManager(material, nowLocation_4.getBlock());
                    MaterialManager materialManager_5 = new MaterialManager(material, nowLocation_5.getBlock());
                    MaterialManager materialManager_6 = new MaterialManager(material, nowLocation_6.getBlock());
                    MaterialManager materialManager_7 = new MaterialManager(material, nowLocation_7.getBlock());
                    MaterialManager materialManager_8 = new MaterialManager(material, nowLocation_8.getBlock());
                    if (!checkTrueOrFalse1 && !checkTrueOrFalse2 && !checkTrueOrFalse3 && !checkTrueOrFalse4 &&
                            !checkTrueOrFalse5 && !checkTrueOrFalse6 && !checkTrueOrFalse7 && !checkTrueOrFalse8) {
                        break;
                    }
                    if (checkTrueOrFalse1 && materialManager_1.CheckMaterial()) {
                        if (material.equals("none")) {
                            validNoneBlockAmount1++;
                        } else {
                            validTotemBlockLocation1.add(nowLocation_1);
                        }
                    } else if (checkTrueOrFalse1 && !materialManager_1.CheckMaterial()) {
                        checkTrueOrFalse1 = false;
                    }
                    //2
                    if (checkTrueOrFalse2 && materialManager_2.CheckMaterial()) {
                        if (material.equals("none")) {
                            validNoneBlockAmount2++;
                        } else {
                            validTotemBlockLocation2.add(nowLocation_2);
                        }
                    } else if (checkTrueOrFalse2 && !materialManager_2.CheckMaterial()) {
                        checkTrueOrFalse2 = false;
                    }
                    //3
                    if (checkTrueOrFalse3 && materialManager_3.CheckMaterial()) {
                        if (material.equals("none")) {
                            validNoneBlockAmount3++;
                        } else {
                            validTotemBlockLocation3.add(nowLocation_3);
                        }
                    } else if (checkTrueOrFalse3 && !materialManager_3.CheckMaterial()) {
                        checkTrueOrFalse3 = false;
                    }
                    //4
                    if (checkTrueOrFalse4 && materialManager_4.CheckMaterial()) {
                        if (material.equals("none")) {
                            validNoneBlockAmount4++;
                        } else {
                            validTotemBlockLocation4.add(nowLocation_4);
                        }
                    } else if (checkTrueOrFalse4 && !materialManager_4.CheckMaterial()) {
                        checkTrueOrFalse4 = false;
                    }
                    //5
                    if (checkTrueOrFalse5 && materialManager_5.CheckMaterial()) {
                        if (material.equals("none")) {
                            validNoneBlockAmount5++;
                        } else {
                            validTotemBlockLocation5.add(nowLocation_5);
                        }
                    } else if (checkTrueOrFalse5 && !materialManager_5.CheckMaterial()) {
                        checkTrueOrFalse5 = false;
                    }
                    //6
                    if (checkTrueOrFalse6 && materialManager_6.CheckMaterial()) {
                        if (material.equals("none")) {
                            validNoneBlockAmount6++;
                        } else {
                            validTotemBlockLocation6.add(nowLocation_6);
                        }
                    } else if (checkTrueOrFalse6 && !materialManager_6.CheckMaterial()) {
                        checkTrueOrFalse6 = false;
                    }
                    //7
                    if (checkTrueOrFalse7 && materialManager_7.CheckMaterial()) {
                        if (material.equals("none")) {
                            validNoneBlockAmount7++;
                        } else {
                            validTotemBlockLocation7.add(nowLocation_7);
                        }
                    } else if (checkTrueOrFalse7 && !materialManager_7.CheckMaterial()) {
                        checkTrueOrFalse7 = false;
                    }
                    //8
                    if (checkTrueOrFalse8 && materialManager_8.CheckMaterial()) {
                        if (material.equals("none")) {
                            validNoneBlockAmount8++;
                        } else {
                            validTotemBlockLocation8.add(nowLocation_8);
                        }
                    } else if (checkTrueOrFalse8 && !materialManager_8.CheckMaterial()) {
                        checkTrueOrFalse8 = false;
                    }
                    // 条件满足
                    if (validTotemBlockLocation1.size() == (base_row * base_column - validNoneBlockAmount1) * base_layer) {
                        AfterCheck(singleTotem, validTotemBlockLocation1, player, block);
                        return true;
                    } else if (validTotemBlockLocation2.size() == (base_row * base_column - validNoneBlockAmount2) * base_layer) {
                        AfterCheck(singleTotem, validTotemBlockLocation2, player, block);
                        return true;
                    } else if (validTotemBlockLocation3.size() == (base_row * base_column - validNoneBlockAmount3) * base_layer) {
                        AfterCheck(singleTotem, validTotemBlockLocation3, player, block);
                        return true;
                    } else if (validTotemBlockLocation4.size() == (base_row * base_column - validNoneBlockAmount4) * base_layer) {
                        AfterCheck(singleTotem, validTotemBlockLocation3, player, block);
                        return true;
                    } else if (validTotemBlockLocation5.size() == (base_row * base_column - validNoneBlockAmount5) * base_layer) {
                        AfterCheck(singleTotem, validTotemBlockLocation3, player, block);
                        return true;
                    } else if (validTotemBlockLocation6.size() == (base_row * base_column - validNoneBlockAmount6) * base_layer) {
                        AfterCheck(singleTotem, validTotemBlockLocation3, player, block);
                        return true;
                    } else if (validTotemBlockLocation7.size() == (base_row * base_column - validNoneBlockAmount7) * base_layer) {
                        AfterCheck(singleTotem, validTotemBlockLocation3, player, block);
                        return true;
                    } else if (validTotemBlockLocation8.size() == (base_row * base_column - validNoneBlockAmount8) * base_layer) {
                        AfterCheck(singleTotem, validTotemBlockLocation3, player, block);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private void AfterCheck(PlacedBlockCheckManager singleTotem, List<Location> validTotemBlockLocation, Player player, Block block) {
        MythicTotem.getCheckingBlock.remove(block);
        if (singleTotem.GetTotemManager().GetTotemDisappear()) {
            for (Location loc : validTotemBlockLocation) {
                Bukkit.getScheduler().callSyncMethod(MythicTotem.instance, () -> {
                    RemoveBlock.DoIt(player, loc);
                    return null;
                });
            }
        }
        Bukkit.getScheduler().callSyncMethod(MythicTotem.instance, () -> {
            ActionManager actionManager = new ActionManager(singleTotem.GetTotemManager().GetTotemAction(), player, block);
            actionManager.CheckAction();
            return null;
        });
    }

}
