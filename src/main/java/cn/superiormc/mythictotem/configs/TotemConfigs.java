package cn.superiormc.mythictotem.configs;

import cn.superiormc.mythictotem.MythicTotem;
import cn.superiormc.mythictotem.managers.TotemManager;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;

import java.util.*;

import static cn.superiormc.mythictotem.MythicTotem.SetErrorValue;

public class TotemConfigs {
    public static Set<String> totemList = new HashSet<>();

    public static void GetTotemConfigs(){
        try {
            totemList = MythicTotem.instance.getConfig().getConfigurationSection("totems").getKeys(false);
        }
        catch (NullPointerException exception){
            SetErrorValue();
            Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[MythicTotem] §cError: We can not find any totem configs, you must provide at least 1 totem config to use this plugin.");
        }
        for (String totemID : totemList){
            try {
                boolean totemDisappear = MythicTotem.instance.getConfig().getBoolean("totems." + totemID + ".disappear");
                List<String> totemLayout = MythicTotem.instance.getConfig().getStringList("totems." + totemID + ".layout");
                List<String> totemAction = MythicTotem.instance.getConfig().getStringList("totems." + totemID + ".actions");
                List<String> totemCondition = MythicTotem.instance.getConfig().getStringList("totems." + totemID + ".conditions");
                ConfigurationSection totemLayoutsExplainConfig = MythicTotem.instance.getConfig().getConfigurationSection("totems." + totemID + ".explains");
                Set<String> totemLayoutsExplainList = totemLayoutsExplainConfig.getKeys(false);
                Map<String, String> totemLayoutExplain = new HashMap<>();
                for (String totemLayoutsChar : totemLayoutsExplainList) {
                    String totemLayoutsMaterial = totemLayoutsExplainConfig.getString(totemLayoutsChar).toLowerCase();
                    totemLayoutExplain.put(totemLayoutsChar, totemLayoutsMaterial);
                    if (totemLayoutsChar.length() > 1) {
                        SetErrorValue();
                        Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[MythicTotem] §cError: Totem layout explain config keys must be a char, like A.");
                        return;
                    }
                }
                MythicTotem.getTotemMap.put(totemID, new TotemManager(totemDisappear, totemLayout, totemAction, totemCondition, totemLayoutExplain));
            }
            catch (NullPointerException exception){
                SetErrorValue();
                Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[MythicTotem] §cError: All config section is required in totem configs, if you do not want use there feature, please view plugin Wiki.");
                throw new RuntimeException(exception);
            }
        }
    }
}
