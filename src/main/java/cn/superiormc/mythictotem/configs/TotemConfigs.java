package cn.superiormc.mythictotem.configs;

import cn.superiormc.mythictotem.MythicTotem;
import cn.superiormc.mythictotem.managers.TotemManager;
import io.lumine.xikage.mythicmobs.MythicMobs;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.*;

public class TotemConfigs {

    public static void initTotemConfigs(){
       File dir = new File(MythicTotem.instance.getDataFolder(), "totems");
       if (!dir.exists()) {
           dir.mkdir();
       }
       File[] files = dir.listFiles();
       if (!Objects.nonNull(files) && files.length != 0) {
           return;
       }
       for (File file : files) {
           String fileName = file.getName();
           if (fileName.endsWith(".yml")) {
               String substring = fileName.substring(0, fileName.length() - 4);
               MythicTotem.getTotemMap.put(substring, new TotemManager(substring, YamlConfiguration.loadConfiguration(file)));
           }
       }
    }
}
