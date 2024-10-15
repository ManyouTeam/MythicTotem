package cn.superiormc.mythictotem.managers;

import cn.superiormc.mythictotem.MythicTotem;
import cn.superiormc.mythictotem.objects.ObjectTotem;
import cn.superiormc.mythictotem.objects.checks.ObjectPlaceCheck;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ConfigManager {

    public List<Block> getCheckingBlock = Collections.synchronizedList(new ArrayList<>());

    public List<Player> getCheckingPlayer = new ArrayList<>();

    public Map<String, List<ObjectPlaceCheck>> getTotemMaterial = new ConcurrentHashMap<>();

    public List<Item> getDroppedItems = new ArrayList<>();

    public static ConfigManager configManager;

    public FileConfiguration config;

    public Map<String, ObjectTotem> totems = new HashMap<>();

    private int threeDtotemAmount = 0;

    public ConfigManager() {
        configManager = this;
        MythicTotem.instance.saveDefaultConfig();
        this.config = MythicTotem.instance.getConfig();
        initTotemConfigs();
    }

    private void initTotemConfigs() {
        File dir = new File(MythicTotem.instance.getDataFolder(), "totems");
        if (!dir.exists()) {
            dir.mkdir();
        }
        loadTotems(dir);
    }

    private void loadTotems(File folder) {
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
                    if (totems.containsKey(substring)) {
                        ErrorManager.errorManager.sendErrorMessage("§x§9§8§F§B§9§8[MythicTotem] §cError: Already loaded a totem config called: " +
                                fileName + "!");
                        continue;
                    }
                    totems.put(substring, new ObjectTotem(substring, YamlConfiguration.loadConfiguration(file)));
                }
            }
        }
    }

    public Map<String, ObjectTotem> getTotems() {
        return totems;
    }

    public boolean getBoolean(String path, boolean defaultValue) {
        return config.getBoolean(path, defaultValue);
    }

    public long getLong(String path, long defaultValue) {
        return config.getLong(path, defaultValue);
    }

    public String getString(String path, String... args) {
        String s = config.getString(path);
        if (s == null) {
            if (args.length == 0) {
                return null;
            }
            s = args[0];
        }
        for (int i = 1 ; i < args.length ; i += 2) {
            String var = "{" + args[i] + "}";
            if (args[i + 1] == null) {
                s = s.replace(var, "");
            }
            else {
                s = s.replace(var, args[i + 1]);
            }
        }
        return s.replace("{plugin_folder}", String.valueOf(MythicTotem.instance.getDataFolder()));
    }

    public void plus3DTotem() {
        threeDtotemAmount++;
    }

    public int getThreeDtotemAmount() {
        return threeDtotemAmount;
    }

}
