package cn.superiormc.mythictotem.managers;

import cn.superiormc.mythictotem.utils.CheckPluginLoad;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class ConditionManager {

    private List<String> condition;

    private Player player;

    private Block block;

    private String event;

    public ConditionManager(List<String> condition, String event, Player player, Block block) {
        this.condition = condition;
        this.event = event;
        this.player = player;
        this.block = block;
    }

    public boolean CheckCondition() {
        boolean conditionTrueOrFasle = true;
        for (String singleCondition : condition){
            if (singleCondition.startsWith("none")){
                return true;
            } else if (singleCondition.startsWith("trigger: "))
            {
                return singleCondition.substring(9).equals(event);
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
                for(String str : singleCondition.substring(12).split(";;")){
                    if(!player.hasPermission(str)){
                        conditionTrueOrFasle = false;
                        break;
                    }
                }
            } else if (CheckPluginLoad.DoIt("PlaceholderAPI") && singleCondition.startsWith("placeholder: ") &&
            player != null)
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
}
