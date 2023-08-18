package cn.superiormc.mythictotem.configs;

import cn.superiormc.mythictotem.MythicTotem;
import cn.superiormc.mythictotem.managers.TotemManager;
import io.lumine.xikage.mythicmobs.MythicMobs;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;

import java.util.*;

public class TotemConfigs {
    public static Set<String> totemList = new HashSet<>();

    public static void GetTotemConfigs(){
        try {
            totemList = MythicTotem.instance.getConfig().getConfigurationSection("totems").getKeys(false);
        }
        catch (NullPointerException exception){
            MythicTotem.checkError("§x§9§8§F§B§9§8[MythicTotem] §cError: We can not find any totem configs, you must provide at least 1 totem config to use this plugin.");
        }
        for (String totemID : totemList){
            ConfigurationSection section = MythicTotem.instance.getConfig().getConfigurationSection("totems." + totemID);
            MythicTotem.getTotemMap.put(totemID, new TotemManager(section));
        }
    }
}
