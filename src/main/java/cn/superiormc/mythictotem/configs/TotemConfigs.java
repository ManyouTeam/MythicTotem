package cn.superiormc.mythictotem.configs;

import cn.superiormc.mythictotem.MythicTotem;
import cn.superiormc.mythictotem.managers.TotemManager;
import io.lumine.xikage.mythicmobs.MythicMobs;
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
                ConfigurationSection section = MythicTotem.instance.getConfig().getConfigurationSection("totems." + totemID);
                MythicTotem.getTotemMap.put(totemID, new TotemManager(section));
            }
            catch (NullPointerException exception){
                SetErrorValue();
                Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[MythicTotem] §cError: All config section is required in totem configs, if you do not want use there feature, please view plugin Wiki.");
            }
        }
    }
}
