package cn.superiormc.mythictotem.managers;

import cn.superiormc.mythictotem.MythicTotem;
import cn.superiormc.mythictotem.configs.Messages;
import cn.superiormc.mythictotem.hook.ItemsAdderHook;
import cn.superiormc.mythictotem.hook.PlaceholerAPIHook;
import cn.superiormc.mythictotem.utils.DispatchCommand;
import cn.superiormc.mythictotem.utils.RemoveBlock;
import dev.lone.itemsadder.api.CustomBlock;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.ArrayList;
import java.util.List;

import static cn.superiormc.mythictotem.MythicTotem.SetErrorValue;

public class ValidManager {

    public ValidManager(BlockPlaceEvent event){
        CheckTotem(event.getPlayer(), event.getBlockPlaced());
    }

    public ValidManager(PlayerInteractEvent event){
        CheckTotem(event.getPlayer(), event.getClickedBlock());
    }

    public void CheckTotem(Player player, Block block) {
        // 处理 ItemsAdder 方块
        List<PlacedBlockCheckManager> placedBlockCheckManagers = new ArrayList<>();
        if (ItemsAdderHook.CheckLoad() && CustomBlock.byAlreadyPlaced(block) != null &&
                MythicTotem.getTotemMaterial.containsKey("itemsadder:" + CustomBlock.byAlreadyPlaced(block).getNamespacedID())) {
            placedBlockCheckManagers = MythicTotem.getTotemMaterial.get("itemsadder:" + CustomBlock.byAlreadyPlaced(block).getNamespacedID());
        }
        // 处理 Oraxen 方块
        // TODO
        // 处理原版方块
        else if (MythicTotem.getTotemMaterial.containsKey("minecraft:" + block.getType().toString().toLowerCase())) {
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
            Location startLocation_2 = new Location(block.getWorld(), block.getLocation().getX() - offset_x_or_z, block.getLocation().getY() + offset_y, block.getLocation().getZ());
            // 图腾的行列
            int base_row = singleTotem.GetTotemManager().GetRealRow();
            int base_column = singleTotem.GetTotemManager().GetRealColumn();
            // 遍历周围的方块
            boolean validTrueOrFalse = false;
            int validXNoneBlockAmount1 = 0;
            List<Location> validXTotemBlockLocation1 = new ArrayList<>();
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
                        Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[MythicTotem] §9Now Location: " + nowLocation + " " + nowLocation.getBlock().toString());
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
                        if (singleTotem.GetTotemManager().GetTotemDisappear()) {
                            for (Location loc : validXTotemBlockLocation1) {
                                Bukkit.getScheduler().callSyncMethod(MythicTotem.instance, () -> {
                                    RemoveBlock.DoIt(loc);
                                    return null;
                                });
                            }
                        }
                        validTrueOrFalse = true;
                        Bukkit.getScheduler().callSyncMethod(MythicTotem.instance, () -> {
                            CheckAction(singleTotem.GetTotemManager().GetTotemAction(), player, block);
                            return null;
                        });
                        break;
                    }
                }
            }
            int validXNoneBlockAmount2 = 0;
            List<Location> validXTotemBlockLocation2 = new ArrayList<>();
            xbianli2:for (int i = 0; i < base_row ; i ++){
                for(int b = 0 ; b < base_column ; b ++) {
                    Location nowLocation = startLocation_1.clone().add(0, -i, b);
                    String material = singleTotem.GetTotemManager().GetRealMaterial(i, base_column - b - 1);
                    if (MythicTotem.instance.getConfig().getBoolean("settings.debug")) {
                        Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[MythicTotem] §2Rule: X2 §eSize: " +
                                validXTotemBlockLocation2.size());
                        Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[MythicTotem] §bMaterial: " + material + " §dR. C.:" + i + " " + b);
                        Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[MythicTotem] §6Base R. C.: " + base_row + " " + base_column);
                        Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[MythicTotem] §4Start Location: " + startLocation_1);
                        Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[MythicTotem] §9Now Location: " + nowLocation + " " + nowLocation.getBlock().toString());
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
                        if (singleTotem.GetTotemManager().GetTotemDisappear()) {
                            for (Location loc : validXTotemBlockLocation2) {
                                Bukkit.getScheduler().callSyncMethod(MythicTotem.instance, () -> {
                                    RemoveBlock.DoIt(loc);
                                    return null;
                                });
                            }
                        }
                        validTrueOrFalse = true;
                        Bukkit.getScheduler().callSyncMethod(MythicTotem.instance, () -> {
                            CheckAction(singleTotem.GetTotemManager().GetTotemAction(), player, block);
                            return null;
                        });
                        break;
                    }
                }
            }
            int validZNoneBlockAmount1 = 0;
            List<Location> validZTotemBlockLocation1 = new ArrayList<>();
            zbianli1:for (int i = 0 ; i < base_row ; i ++) {
                for (int b = 0; b < base_column; b ++) {
                    if (validTrueOrFalse) {
                        break zbianli1;
                    }
                    Location nowLocation = startLocation_2.clone().add(b, -i, 0);
                    String material = singleTotem.GetTotemManager().GetRealMaterial(i, base_column - b - 1);
                    if (MythicTotem.instance.getConfig().getBoolean("settings.debug")) {
                        Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[MythicTotem] §3Rule: Z1 §eSize: " +
                                validZTotemBlockLocation1.size());
                        Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[MythicTotem] §bMaterial: " + material + " §dR. C.:" + i + " " + b);
                        Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[MythicTotem] §6Base R. C.: " + base_row + " " + base_column);
                        Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[MythicTotem] §4Start Location: " + startLocation_1);
                        Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[MythicTotem] §9Now Location: " + nowLocation + " " + nowLocation.getBlock().toString());
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
                        if (singleTotem.GetTotemManager().GetTotemDisappear()) {
                            for (Location loc : validZTotemBlockLocation1) {
                                Bukkit.getScheduler().callSyncMethod(MythicTotem.instance, () -> {
                                    RemoveBlock.DoIt(loc);
                                    return null;
                                });
                            }
                        }
                        validTrueOrFalse = true;
                        Bukkit.getScheduler().callSyncMethod(MythicTotem.instance, () -> {
                            CheckAction(singleTotem.GetTotemManager().GetTotemAction(), player, block);
                            return null;
                        });
                        break;
                    }
                }
            }
            int validZNoneBlockAmount2 = 0;
            List<Location> validZTotemBlockLocation2 = new ArrayList<>();
            zbianli2:for (int i = 0 ; i < base_row ; i ++) {
                for (int b = 0; b < base_column; b ++) {
                    if (validTrueOrFalse) {
                        break zbianli2;
                    }
                    Location nowLocation = startLocation_2.clone().add(b, -i, 0);
                    String material = singleTotem.GetTotemManager().GetRealMaterial(i, b);
                    if (MythicTotem.instance.getConfig().getBoolean("settings.debug")) {
                        Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[MythicTotem] §5Rule: Z2 §eSize: " +
                                validZTotemBlockLocation2.size());
                        Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[MythicTotem] §bMaterial: " + material + " §dR. C.:" + i + " " + b);
                        Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[MythicTotem] §6Base R. C.: " + base_row + " " + base_column);
                        Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[MythicTotem] §4Start Location: " + startLocation_1);
                        Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[MythicTotem] §9Now Location: " + nowLocation + " " + nowLocation.getBlock().toString());
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
                        if (singleTotem.GetTotemManager().GetTotemDisappear()) {
                            for (Location loc : validZTotemBlockLocation2) {
                                Bukkit.getScheduler().callSyncMethod(MythicTotem.instance, () -> {
                                    RemoveBlock.DoIt(loc);
                                    return null;
                                });
                            }
                        }
                        validTrueOrFalse = true;
                        Bukkit.getScheduler().callSyncMethod(MythicTotem.instance, () -> {
                            CheckAction(singleTotem.GetTotemManager().GetTotemAction(), player, block);
                            return null;
                        });
                        break;
                    }
                }
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
            } catch (IllegalArgumentException | NullPointerException exception) {
                SetErrorValue();
                Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[MythicTotem] §cError: There is illegal material in your totem config layout explain.");
            }
        }
        else if (material.startsWith("itemsadder:")) {
            CustomBlock iaMaterial = CustomBlock.getInstance(material.split(":")[1] + material.split(":")[2]);
            if (iaMaterial == null) {
                Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[MythicTotem] §cError: There is illegal material in your totem config layout explain.");
            } else {
                return (iaMaterial == CustomBlock.byAlreadyPlaced(block));
            }
        }
        else if (material.startsWith("oraxen:")) {
            //TODO
            return false;
        } else {
            try {
                return (Material.valueOf(material.split(":")[1].toUpperCase()) == block.getType());
            } catch (IllegalArgumentException | NullPointerException exception) {
                SetErrorValue();
                Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[MythicTotem] §cError: There is illegal material in your totem config layout explain.");
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
            } else if (singleAction.startsWith("console_command: ")) {
                ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();
                Bukkit.getScheduler().callSyncMethod(MythicTotem.instance, () -> {
                    DispatchCommand.DoIt(ReplacePlaceholder(singleAction.substring(17), player, block));
                    return null;
                });
            } else if (singleAction.startsWith("player_command: ")) {
                Bukkit.getScheduler().callSyncMethod(MythicTotem.instance, () -> {
                    DispatchCommand.DoIt(player, ReplacePlaceholder(singleAction.substring(16), player, block));
                    return null;
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
            } else if (PlaceholerAPIHook.CheckLoad() && singleCondition.startsWith("placeholder: "))
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
                .replace("%player%", player.getName());
    }

}
