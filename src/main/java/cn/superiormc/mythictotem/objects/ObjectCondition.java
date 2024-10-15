package cn.superiormc.mythictotem.objects;

import cn.superiormc.mythictotem.managers.ErrorManager;
import cn.superiormc.mythictotem.MythicTotem;
import cn.superiormc.mythictotem.hooks.CheckValidHook;
import cn.superiormc.mythictotem.objects.checks.ObjectCheck;
import cn.superiormc.mythictotem.utils.CommonUtil;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.mobs.ActiveMob;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class ObjectCondition {

    private final List<String> condition;

    public ObjectCondition(List<String> condition) {
        this.condition = condition;
    }

    public boolean checkCondition(ObjectCheck manager) {
        Player player = manager.getPlayer();
        Block block = manager.getBlock();
        String event = manager.getEvent();
        ItemStack item= manager.getItem();
        boolean conditionTrueOrFasle = true;
        for (String singleCondition : condition) {
            if (singleCondition.startsWith("none")){
                return true;
            } else if (singleCondition.startsWith("trigger: "))
            {
                return singleCondition.substring(9).equals(event);
            } else if (singleCondition.startsWith("trigger_item: ") || singleCondition.startsWith("trigger-item: "))
            {
                return item != null && singleCondition.substring(14).equals(CheckValidHook.checkValid(item)[1]);
            } else if (singleCondition.startsWith("world: ") && block != null)
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
            } else if (singleCondition.startsWith("biome: ") && block != null)
            {
                int i = 0;
                for (String str : singleCondition.substring(7).toUpperCase().split(";;")){
                    if (str.toUpperCase().equals(block.getBiome().name())){
                        break;
                    }
                    i ++;
                }
                if (i == singleCondition.substring(7).split(";;").length){
                    conditionTrueOrFasle = false;
                    break;
                }
            } else if (singleCondition.startsWith("permission: ") && player != null)
            {
                for (String str : singleCondition.substring(12).split(";;")){
                    if (!player.hasPermission(str)){
                        conditionTrueOrFasle = false;
                        break;
                    }
                }
            } else if (CommonUtil.checkPluginLoad("PlaceholderAPI") && singleCondition.startsWith("placeholder: ") &&
            player != null) {
                try {
                    if (singleCondition.split(";;").length == 3) {
                        String[] conditionString = singleCondition.substring(13).split(";;");
                        String placeholder = conditionString[0];
                        String conditionValue = conditionString[1];
                        String value = conditionString[2];
                        placeholder = PlaceholderAPI.setPlaceholders(player, placeholder);
                        value = PlaceholderAPI.setPlaceholders(player, value);
                        if (conditionValue.equals("!=")) {
                            if (placeholder.equals(value)) {
                                conditionTrueOrFasle = false;
                                break;
                            }
                        }
                        if (conditionValue.equals("==")){
                            if (!placeholder.equals(value)){
                                conditionTrueOrFasle = false;
                                break;
                            }
                        }
                        if (conditionValue.equals("!*=")) {
                            if (placeholder.contains(value)) {
                                conditionTrueOrFasle = false;
                                break;
                            }
                        }
                        if (conditionValue.equals("*=")){
                            if (!placeholder.contains(value)){
                                conditionTrueOrFasle = false;
                                break;
                            }
                        }
                        if (conditionValue.equals(">=")){
                            if (!(Double.parseDouble(placeholder) >= Double.parseDouble(value))){
                                conditionTrueOrFasle = false;
                                break;
                            }
                        }
                        if (conditionValue.equals(">")){
                            if (!(Double.parseDouble(placeholder) > Double.parseDouble(value))){
                                conditionTrueOrFasle = false;
                                break;
                            }
                        }
                        if (conditionValue.equals("<=")){
                            if (!(Double.parseDouble(placeholder) <= Double.parseDouble(value))){
                                conditionTrueOrFasle = false;
                                break;
                            }
                        }
                        if(conditionValue.equals("<")){
                            if (!(Double.parseDouble(placeholder) < Double.parseDouble(value))){
                                conditionTrueOrFasle = false;
                                break;
                            }
                        }
                        if (conditionValue.equals("=")){
                            if (!(Double.parseDouble(placeholder) == Double.parseDouble(value))){
                                conditionTrueOrFasle = false;
                                break;
                            }
                        }
                    }
                    else {
                        ErrorManager.errorManager.sendErrorMessage("§x§9§8§F§B§9§8[MythicTotem] §cError: Your placeholder condition in totem configs can not being correctly load.");
                        return false;
                    }
                }
                catch (ArrayIndexOutOfBoundsException e) {
                    ErrorManager.errorManager.sendErrorMessage("§x§9§8§F§B§9§8[MythicTotem] §cError: Your placeholder condition in totem configs can not being correctly load.");
                    return false;
                }
            } else if (!MythicTotem.freeVersion && singleCondition.startsWith("mobs_near: ") && block != null) {
                String[] tempVal1 = singleCondition.substring(11).split(";;");
                if (tempVal1.length != 2) {
                    ErrorManager.errorManager.sendErrorMessage("§x§9§8§F§B§9§8[MythicTotem] §cError: Your mobs_near condition in totem configs can not being correctly load.");
                    return false;
                }
                for (Entity tempVal3 : CommonUtil.getNearbyEntity(block.getLocation(), Double.parseDouble(tempVal1[1]))) {
                    if (CommonUtil.checkPluginLoad("MythicMobs")) {
                        ActiveMob tempVal4 = MythicBukkit.inst().getMobManager().getMythicMobInstance(tempVal3);
                        if (tempVal4 != null && tempVal1[0].equals(tempVal4.getType().getInternalName())) {
                            return false;
                        }
                    }
                    String customName = tempVal3.getCustomName();
                    if (customName == null && tempVal1[0].equalsIgnoreCase(tempVal3.getType().name())) {
                        return false;
                    } else if (customName != null && customName.contains(tempVal1[0])) {
                        return false;
                    }
                }
            }
        }
        return conditionTrueOrFasle;
    }
}
