package cn.superiormc.mythictotem.configs;

import cn.superiormc.mythictotem.MythicTotem;
import cn.superiormc.mythictotem.managers.TotemManager;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.*;

public class TotemConfigs {

    public static void initTotemConfigs() {
        File dir = new File(MythicTotem.instance.getDataFolder(), "totems");
        if (!dir.exists()) {
            dir.mkdir();
        }
        loadTotems(dir);
    }

    private static void loadTotems(File folder) {
        File[] files = folder.listFiles();
        if (!Objects.nonNull(files)) {
            return;
        }
        for (File file : files) {
            if (file.isDirectory()) {
                loadTotems(file); // Recursive call to load files from subfolders
            } else {
                String fileName = file.getName();
                if (fileName.endsWith(".yml")) {
                    String substring = fileName.substring(0, fileName.length() - 4);
                    if (MythicTotem.getTotemMap.containsKey(substring)) {
                        MythicTotem.checkError("§x§9§8§F§B§9§8[MythicTotem] §cError: Already loaded a totem config called: " +
                                fileName + "!");
                        continue;
                    }
                    MythicTotem.getTotemMap.put(substring, new TotemManager(substring, YamlConfiguration.loadConfiguration(file)));
                }
            }
        }
    }
}
