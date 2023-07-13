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
import java.util.Collection;
import java.util.List;

public class ValidManager {

    public ValidManager(BlockPlaceEvent event){
        CheckTotem(event.getPlayer(), event.getBlockPlaced());
    }

    public ValidManager(PlayerInteractEvent event){
        if (event.getClickedBlock() == null) {
            return;
        }
        CheckTotem(event.getPlayer(), event.getClickedBlock());
    }

    public void CheckTotem(Player player, Block block) {
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
                VerticalTotem(singleTotem, player, block);
            }
            else {
                HorizontalTotem(singleTotem, player, block);
            }
            MythicTotem.getCheckingBlock.remove(block);
        }
    }

    private void VerticalTotem(PlacedBlockCheckManager singleTotem, Player player, Block block) {
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
        List<Location> validXTotemBlockLocation1 = new ArrayList<>();
        List<Location> validXTotemBlockLocation2 = new ArrayList<>();
        List<Location> validZTotemBlockLocation1 = new ArrayList<>();
        List<Location> validZTotemBlockLocation2 = new ArrayList<>();
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
                String material = singleTotem.GetTotemManager().GetRealMaterial(i, b);
                //1
                MaterialManager materialManager_1 = new MaterialManager(material, nowLocation_1.getBlock());
                MaterialManager materialManager_2 = new MaterialManager(material, nowLocation_2.getBlock());
                MaterialManager materialManager_3 = new MaterialManager(material, nowLocation_3.getBlock());
                MaterialManager materialManager_4 = new MaterialManager(material, nowLocation_4.getBlock());
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
                else if (checkXTrueOrFalse2 && materialManager_2.CheckMaterial()) {
                    if (material.equals("none")) {
                        validXNoneBlockAmount2++;
                    } else {
                        validXTotemBlockLocation2.add(nowLocation_2);
                    }
                } else if (checkXTrueOrFalse2 && !materialManager_2.CheckMaterial()) {
                    checkXTrueOrFalse2 = false;
                }
                //3
                else if (checkZTrueOrFalse1 && materialManager_3.CheckMaterial()) {
                    if (material.equals("none")) {
                        validZNoneBlockAmount1++;
                    } else {
                        validZTotemBlockLocation1.add(nowLocation_3);
                    }
                } else if (checkZTrueOrFalse1 && !materialManager_3.CheckMaterial()) {
                    checkZTrueOrFalse1 = false;
                }
                //4
                else if (checkZTrueOrFalse2 && materialManager_4.CheckMaterial()) {
                    if (material.equals("none")) {
                        validZNoneBlockAmount2++;
                    } else {
                        validZTotemBlockLocation2.add(nowLocation_4);
                    }
                } else if (checkZTrueOrFalse2 && !materialManager_4.CheckMaterial()) {
                    checkZTrueOrFalse2 = false;
                } else {
                    break;
                }
                // 条件满足
                if (validXTotemBlockLocation1.size() == (base_row * base_column - validXNoneBlockAmount1)) {
                    MythicTotem.getCheckingBlock.remove(block);
                    if (singleTotem.GetTotemManager().GetTotemDisappear()) {
                        for (Location loc : validXTotemBlockLocation1) {
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
                    break;
                } else if (validXTotemBlockLocation2.size() == (base_row * base_column - validXNoneBlockAmount2)) {
                    MythicTotem.getCheckingBlock.remove(block);
                    if (singleTotem.GetTotemManager().GetTotemDisappear()) {
                        for (Location loc : validXTotemBlockLocation2) {
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
                    break;
                } else if (validZTotemBlockLocation1.size() == (base_row * base_column - validZNoneBlockAmount1)) {
                    MythicTotem.getCheckingBlock.remove(block);
                    if (singleTotem.GetTotemManager().GetTotemDisappear()) {
                        for (Location loc : validZTotemBlockLocation2) {
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
                    break;
                } else if (validZTotemBlockLocation2.size() == (base_row * base_column - validZNoneBlockAmount2)) {
                    MythicTotem.getCheckingBlock.remove(block);
                    if (singleTotem.GetTotemManager().GetTotemDisappear()) {
                        for (Location loc : validZTotemBlockLocation2) {
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
                    break;
                }
            }
        }
    }

    private void HorizontalTotem(PlacedBlockCheckManager singleTotem, Player player, Block block) {
        // 玩家放置的方块的坐标的偏移
        int offset_row = singleTotem.GetRow();
        int offset_column = singleTotem.GetColumn();
        // 初始坐标
        // 例如这个方块在某个图腾中在第一行第一列、第二列和第三列
        // 那么这里的 offset_y 和 offset_x_or_z 应该分别为 0，0 0，1 0，2
        // 初始坐标为第一行第一列的坐标，通过这个offset的值偏移到正确的初始坐标
        Location startLocation_1 = new Location(block.getWorld(),
                block.getLocation().getX() + offset_column,
                block.getLocation().getY(),
                block.getLocation().getZ() + offset_row);
        Location startLocation_2 = new Location(block.getWorld(),
                block.getLocation().getX() + offset_column,
                block.getLocation().getY(),
                block.getLocation().getZ() - offset_row);
        Location startLocation_3 = new Location(block.getWorld(),
                block.getLocation().getX() - offset_column,
                block.getLocation().getY(),
                block.getLocation().getZ() + offset_row);
        Location startLocation_4 = new Location(block.getWorld(),
                block.getLocation().getX() - offset_column,
                block.getLocation().getY(),
                block.getLocation().getZ() - offset_row);
        Location startLocation_5 = new Location(block.getWorld(),
                block.getLocation().getX() + offset_row,
                block.getLocation().getY(),
                block.getLocation().getZ() + offset_column);
        Location startLocation_6 = new Location(block.getWorld(),
                block.getLocation().getX() + offset_row,
                block.getLocation().getY(),
                block.getLocation().getZ() - offset_column);
        Location startLocation_7 = new Location(block.getWorld(),
                block.getLocation().getX() - offset_row,
                block.getLocation().getY(),
                block.getLocation().getZ() + offset_column);
        Location startLocation_8 = new Location(block.getWorld(),
                block.getLocation().getX() - offset_row,
                block.getLocation().getY(),
                block.getLocation().getZ() - offset_column);
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
        List<Location> validXTotemBlockLocation1 = new ArrayList<>();
        List<Location> validXTotemBlockLocation2 = new ArrayList<>();
        List<Location> validZTotemBlockLocation1 = new ArrayList<>();
        List<Location> validZTotemBlockLocation2 = new ArrayList<>();
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
                String material = singleTotem.GetTotemManager().GetRealMaterial(i, b);
                //1
                MaterialManager materialManager_1 = new MaterialManager(material, nowLocation_1.getBlock());
                MaterialManager materialManager_2 = new MaterialManager(material, nowLocation_2.getBlock());
                MaterialManager materialManager_3 = new MaterialManager(material, nowLocation_3.getBlock());
                MaterialManager materialManager_4 = new MaterialManager(material, nowLocation_4.getBlock());
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
                else if (checkXTrueOrFalse2 && materialManager_2.CheckMaterial()) {
                    if (material.equals("none")) {
                        validXNoneBlockAmount2++;
                    } else {
                        validXTotemBlockLocation2.add(nowLocation_2);
                    }
                } else if (checkXTrueOrFalse2 && !materialManager_2.CheckMaterial()) {
                    checkXTrueOrFalse2 = false;
                }
                //3
                else if (checkZTrueOrFalse1 && materialManager_3.CheckMaterial()) {
                    if (material.equals("none")) {
                        validZNoneBlockAmount1++;
                    } else {
                        validZTotemBlockLocation1.add(nowLocation_3);
                    }
                } else if (checkZTrueOrFalse1 && !materialManager_3.CheckMaterial()) {
                    checkZTrueOrFalse1 = false;
                }
                //4
                else if (checkZTrueOrFalse2 && materialManager_4.CheckMaterial()) {
                    if (material.equals("none")) {
                        validZNoneBlockAmount2++;
                    } else {
                        validZTotemBlockLocation2.add(nowLocation_4);
                    }
                } else if (checkZTrueOrFalse2 && !materialManager_4.CheckMaterial()) {
                    checkZTrueOrFalse2 = false;
                } else {
                    break;
                }
                // 条件满足
                if (validXTotemBlockLocation1.size() == (base_row * base_column - validXNoneBlockAmount1)) {
                    MythicTotem.getCheckingBlock.remove(block);
                    if (singleTotem.GetTotemManager().GetTotemDisappear()) {
                        for (Location loc : validXTotemBlockLocation1) {
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
                    break;
                } else if (validXTotemBlockLocation2.size() == (base_row * base_column - validXNoneBlockAmount2)) {
                    MythicTotem.getCheckingBlock.remove(block);
                    if (singleTotem.GetTotemManager().GetTotemDisappear()) {
                        for (Location loc : validXTotemBlockLocation2) {
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
                    break;
                } else if (validZTotemBlockLocation1.size() == (base_row * base_column - validZNoneBlockAmount1)) {
                    MythicTotem.getCheckingBlock.remove(block);
                    if (singleTotem.GetTotemManager().GetTotemDisappear()) {
                        for (Location loc : validZTotemBlockLocation2) {
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
                    break;
                } else if (validZTotemBlockLocation2.size() == (base_row * base_column - validZNoneBlockAmount2)) {
                    MythicTotem.getCheckingBlock.remove(block);
                    if (singleTotem.GetTotemManager().GetTotemDisappear()) {
                        for (Location loc : validZTotemBlockLocation2) {
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
                    break;
                }
            }
        }
    }

}
