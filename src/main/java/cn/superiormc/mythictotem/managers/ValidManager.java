package cn.superiormc.mythictotem.managers;

import cn.superiormc.mythictotem.MythicTotem;
import cn.superiormc.mythictotem.configs.Messages;
import cn.superiormc.mythictotem.utils.CheckPluginLoad;
import cn.superiormc.mythictotem.utils.DispatchCommand;
import cn.superiormc.mythictotem.utils.MythicMobsSpawn;
import cn.superiormc.mythictotem.utils.RemoveBlock;
import dev.lone.itemsadder.api.CustomBlock;
import io.th0rgal.oraxen.api.OraxenBlocks;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.ArrayList;
import java.util.List;

public class ValidManager {

    public ValidManager(BlockPlaceEvent event){
        CheckTotem(event.getPlayer(), event.getBlockPlaced());
    }

    public ValidManager(PlayerInteractEvent event){
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
            if (!CheckCondition(singleTotem.GetTotemManager().GetTotemCondition(), player, block)) {
                break;
            }
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
            // 存放实际方块摆放位置和图腾配置一致的 List
            List<Location> validXTotemBlockLocation1 = new ArrayList<>();
            // 四种遍历规则
            xbianli1:
            for (int i = 0; i < base_row; i++) {
                for (int b = 0; b < base_column; b++) {
                    Location nowLocation = startLocation_1.clone().add(0, -i, b);
                    String material = singleTotem.GetTotemManager().GetRealMaterial(i, b);
                    if (MythicTotem.instance.getConfig().getBoolean("settings.debug")) {
                        Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[MythicTotem] §1Rule: X1 §eSize: " +
                                validXTotemBlockLocation1.size());
                        Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[MythicTotem] §bMaterial: " + material + " §dR. C.:" + i + " " + b);
                        Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[MythicTotem] §6Base R. C.: " + base_row + " " + base_column);
                        Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[MythicTotem] §4Start Location: " + startLocation_1);
                        Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[MythicTotem] §9Now Location: " + nowLocation + " " + nowLocation.getBlock());
                    }
                    if (!CheckMaterial(material, nowLocation.getBlock())) {
                        break xbianli1;
                    } else {
                        if (!material.equals("none")) {
                            validXTotemBlockLocation1.add(nowLocation);
                        } else {
                            validXNoneBlockAmount1++;
                        }
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
                            CheckAction(singleTotem.GetTotemManager().GetTotemAction(), player, block);
                            return null;
                        });
                        Bukkit.getConsoleSender().sendMessage("X1");
                        break;
                    }
                }
            }
            int validXNoneBlockAmount2 = 0;
            List<Location> validXTotemBlockLocation2 = new ArrayList<>();
            xbianli2:for (int i = 0; i < base_row ; i ++){
                for (int b = 0 ; b < base_column ; b ++) {
                    Location nowLocation = startLocation_2.clone().add(0, -i, -b);
                    String material = singleTotem.GetTotemManager().GetRealMaterial(i, b);
                    if (MythicTotem.instance.getConfig().getBoolean("settings.debug")) {
                        Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[MythicTotem] §2Rule: X2 §eSize: " +
                                validXTotemBlockLocation2.size());
                        Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[MythicTotem] §bMaterial: " + material + " §dR. C.:" + i + " " + b);
                        Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[MythicTotem] §6Base R. C.: " + base_row + " " + base_column);
                        Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[MythicTotem] §4Start Location: " + startLocation_2);
                        Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[MythicTotem] §9Now Location: " + nowLocation + " " + nowLocation.getBlock());
                    }
                    if (!CheckMaterial(material, nowLocation.getBlock())) {
                        break xbianli2;
                    } else {
                        if (!material.equals("none")) {
                            validXTotemBlockLocation2.add(nowLocation);
                        }
                        else{
                            validXNoneBlockAmount2 ++;
                        }
                    }
                    if (validXTotemBlockLocation2.size() == (base_row * base_column - validXNoneBlockAmount2)) {
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
                            CheckAction(singleTotem.GetTotemManager().GetTotemAction(), player, block);
                            return null;
                        });
                        Bukkit.getConsoleSender().sendMessage("X2");
                        break;
                    }
                }
            }
            int validZNoneBlockAmount1 = 0;
            List<Location> validZTotemBlockLocation1 = new ArrayList<>();
            zbianli1:for (int i = 0 ; i < base_row ; i ++) {
                for (int b = 0; b < base_column; b ++) {
                    Location nowLocation = startLocation_3.clone().add(b, -i, 0);
                    String material = singleTotem.GetTotemManager().GetRealMaterial(i, b);
                    if (MythicTotem.instance.getConfig().getBoolean("settings.debug")) {
                        Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[MythicTotem] §3Rule: Z1 §eSize: " +
                                validZTotemBlockLocation1.size());
                        Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[MythicTotem] §bMaterial: " + material + " §dR. C.:" + i + " " + b);
                        Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[MythicTotem] §6Base R. C.: " + base_row + " " + base_column);
                        Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[MythicTotem] §4Start Location: " + startLocation_3);
                        Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[MythicTotem] §9Now Location: " + nowLocation + " " + nowLocation.getBlock());
                    }
                    if (!CheckMaterial(material, nowLocation.getBlock())) {
                        break zbianli1;
                    } else {
                        if (!material.equals("none")) {
                            validZTotemBlockLocation1.add(nowLocation);
                        }
                        else{
                            validZNoneBlockAmount1 ++;
                        }
                    }
                    if (validZTotemBlockLocation1.size() == (base_row * base_column - validZNoneBlockAmount1)) {
                        MythicTotem.getCheckingBlock.remove(block);
                        if (singleTotem.GetTotemManager().GetTotemDisappear()) {
                            for (Location loc : validZTotemBlockLocation1) {
                                Bukkit.getScheduler().callSyncMethod(MythicTotem.instance, () -> {
                                    RemoveBlock.DoIt(player, loc);
                                    return null;
                                });
                            }
                        }
                        Bukkit.getScheduler().callSyncMethod(MythicTotem.instance, () -> {
                            CheckAction(singleTotem.GetTotemManager().GetTotemAction(), player, block);
                            return null;
                        });
                        Bukkit.getConsoleSender().sendMessage("Z1");
                        break;
                    }
                }
            }
            int validZNoneBlockAmount2 = 0;
            List<Location> validZTotemBlockLocation2 = new ArrayList<>();
            zbianli2:for (int i = 0 ; i < base_row ; i ++) {
                for (int b = 0; b < base_column; b ++) {
                    Location nowLocation = startLocation_4.clone().add(-b, -i, 0);
                    String material = singleTotem.GetTotemManager().GetRealMaterial(i, b);
                    if (MythicTotem.instance.getConfig().getBoolean("settings.debug")) {
                        Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[MythicTotem] §5Rule: Z2 §eSize: " +
                                validZTotemBlockLocation2.size());
                        Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[MythicTotem] §bMaterial: " + material + " §dR. C.:" + i + " " + b);
                        Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[MythicTotem] §6Base R. C.: " + base_row + " " + base_column);
                        Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[MythicTotem] §4Start Location: " + startLocation_4);
                        Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[MythicTotem] §9Now Location: " + nowLocation + " " + nowLocation.getBlock());
                    }
                    if (!CheckMaterial(material, nowLocation.getBlock())) {
                        break zbianli2;
                    } else {
                        if (!material.equals("none")) {
                            validZTotemBlockLocation2.add(nowLocation);
                        }
                        else{
                            validZNoneBlockAmount2 ++;
                        }
                    }
                    if (validZTotemBlockLocation2.size() == (base_row * base_column - validZNoneBlockAmount2)) {
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
                            CheckAction(singleTotem.GetTotemManager().GetTotemAction(), player, block);
                            return null;
                        });
                        Bukkit.getConsoleSender().sendMessage("Z2");
                        break;
                    }
                }
            }
            if (MythicTotem.getCheckingBlock.contains(block)) {
                MythicTotem.getCheckingBlock.remove(block);
            }
        }
    }

    public boolean CheckMaterial(String material, Block block){
        if (material.equals("none")) {
            return true;
        }
        else if (material.startsWith("minecraft:")) {
            try {
                return (Material.valueOf(material.split(":")[1].toUpperCase()) == block.getType());
            } catch (IllegalArgumentException | NullPointerException ignored) {
            }
        }
        else if (material.startsWith("itemsadder:")) {
            try {
                return (material.split(":")[1] + ":" + material.split(":")[2]).equals(CustomBlock.byAlreadyPlaced(block).getNamespacedID());
            } catch (NullPointerException ignored) {
            }
        }
        else if (material.startsWith("oraxen:")) {
            try {
                if (OraxenBlocks.getNoteBlockMechanic(block).getItemID() == null){
                    return (material.split(":")[1]).equals(OraxenBlocks.getStringMechanic(block).getItemID());
                }
                else if (OraxenBlocks.getStringMechanic(block).getItemID() == null) {
                    return (material.split(":")[1]).equals(OraxenBlocks.getNoteBlockMechanic(block).getItemID());
                }
            } catch (NullPointerException ignored) {
            }
        } else {
            try {
                return (Material.valueOf(material.split(":")[1].toUpperCase()) == block.getType());
            } catch (IllegalArgumentException | NullPointerException ignored) {
            }
        }
        return false;
    }

    public void CheckAction(List<String> action, Player player, Block block){
        for(String singleAction : action) {
            if (singleAction.startsWith("none")) {
                return;
            } else if (singleAction.startsWith("message: ")) {
                player.sendMessage(Messages.GetActionMessages(singleAction.substring(9)));
            } else if (CheckPluginLoad.DoIt("MythicMobs") && singleAction.startsWith("mythicmobs_spawn: ")) {
                Bukkit.getScheduler().runTask(MythicTotem.instance, () -> {
                    try {
                        MythicMobsSpawn.DoIt(block, singleAction.substring(18).split(";;")[0], Integer.parseInt(singleAction.substring(18).split(";;")[1]));
                    }
                    catch (ArrayIndexOutOfBoundsException e) {
                        MythicMobsSpawn.DoIt(block, singleAction.substring(18).split(";;")[0], 1);
                    }
                });
            } else if (singleAction.startsWith("console_command: ")) {
                Bukkit.getScheduler().runTask(MythicTotem.instance, () -> {
                    DispatchCommand.DoIt(ReplacePlaceholder(singleAction.substring(17), player, block));
                });
            } else if (singleAction.startsWith("player_command: ")) {
                Bukkit.getScheduler().runTask(MythicTotem.instance, () -> {
                    DispatchCommand.DoIt(player, ReplacePlaceholder(singleAction.substring(16), player, block));
                });
            }
        }
    }

    public boolean CheckCondition(List<String> condition, Player player, Block block){
        boolean conditionTrueOrFasle = true;
        for(String singleCondition : condition){
            if (singleCondition.startsWith("none")){
                return true;
            } else if (singleCondition.startsWith("world: "))
            {
                int i = 0;
                for (String str : singleCondition.substring(7).split(";;")){
                    if (str.equals(block.getWorld().getName())){
                        break;
                    }
                    i ++;
                }
                if (i == singleCondition.substring(7).split(";;").length){
                    conditionTrueOrFasle = false;
                    break;
                }
            } else if (singleCondition.startsWith("biome: "))
            {
                int i = 0;
                for (String str : singleCondition.substring(7).toUpperCase().split(";;")){
                    if (str.equals(block.getBiome().getKey().toString())){
                        break;
                    }
                    i ++;
                }
                if (i == singleCondition.substring(7).split(";;").length){
                    conditionTrueOrFasle = false;
                    break;
                }
            } else if (singleCondition.startsWith("permission: "))
            {
                for(String str : singleCondition.substring(12).split(";;")){
                    if(!player.hasPermission(str)){
                        conditionTrueOrFasle = false;
                        break;
                    }
                }
            } else if (CheckPluginLoad.DoIt("PlaceholderAPI") && singleCondition.startsWith("placeholder: "))
            {
                String[] conditionString = singleCondition.substring(13).split(";;");
                String placeholder = conditionString[0];
                String conditionValue = conditionString[1];
                String value = conditionString[2];
                if(conditionValue.equals("==")){
                    placeholder = PlaceholderAPI.setPlaceholders(player, placeholder);
                    if(!placeholder.equals(value)){
                        conditionTrueOrFasle = false;
                        break;
                    }
                }
                if(conditionValue.equals("*=")){
                    placeholder = PlaceholderAPI.setPlaceholders(player, placeholder);
                    if(!placeholder.contains(value)){
                        conditionTrueOrFasle = false;
                        break;
                    }
                }
                if(conditionValue.equals(">=")){
                    placeholder = PlaceholderAPI.setPlaceholders(player, placeholder);
                    if(!(Integer.parseInt(placeholder) >= Integer.parseInt(value))){
                        conditionTrueOrFasle = false;
                        break;
                    }
                }
                if(conditionValue.equals(">")){
                    placeholder = PlaceholderAPI.setPlaceholders(player, placeholder);
                    if(!(Integer.parseInt(placeholder) > Integer.parseInt(value))){
                        conditionTrueOrFasle = false;
                        break;
                    }
                }
                if(conditionValue.equals("<=")){
                    placeholder = PlaceholderAPI.setPlaceholders(player, placeholder);
                    if(!(Integer.parseInt(placeholder) <= Integer.parseInt(value))){
                        conditionTrueOrFasle = false;
                        break;
                    }
                }
                if(conditionValue.equals("<")){
                    placeholder = PlaceholderAPI.setPlaceholders(player, placeholder);
                    if(!(Integer.parseInt(placeholder) < Integer.parseInt(value))){
                        conditionTrueOrFasle = false;
                        break;
                    }
                }
                if(conditionValue.equals("=")){
                    placeholder = PlaceholderAPI.setPlaceholders(player, placeholder);
                    if(!(Integer.parseInt(placeholder) == Integer.parseInt(value))){
                        conditionTrueOrFasle = false;
                        break;
                    }
                }
            }
        }
        return conditionTrueOrFasle;
    }

    private String ReplacePlaceholder(String str, Player player, Block block){
        return str.replace("%world%", block.getWorld().getName())
                .replace("%block_x%", String.valueOf(block.getX()))
                .replace("%block_y%", String.valueOf(block.getY()))
                .replace("%block_z%", String.valueOf(block.getZ()))
                .replace("%player_x%", String.valueOf(player.getLocation().getX()))
                .replace("%player_y%", String.valueOf(player.getLocation().getY()))
                .replace("%player_z%", String.valueOf(player.getLocation().getZ()))
                .replace("%player_pitch%", String.valueOf(player.getLocation().getPitch()))
                .replace("%player_yaw%", String.valueOf(player.getLocation().getYaw()))
                .replace("%player%", player.getName());
    }

}
