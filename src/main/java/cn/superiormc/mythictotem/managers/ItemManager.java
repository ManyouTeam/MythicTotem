package cn.superiormc.mythictotem.managers;

import cn.superiormc.mythictotem.MythicTotem;
import cn.superiormc.mythictotem.utils.CommonUtil;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class ItemManager {

    private static Map<String, ItemStack> savedItemMap = new HashMap<>();

    public static ItemManager itemManager;

    public ItemManager() {
        itemManager = this;
        initSavedItems();
    }

    public void initSavedItems() {
        savedItemMap.clear();
        File dir = new File(MythicTotem.instance.getDataFolder() + "/item");
        if(!dir.exists()) {
            dir.mkdir();
        }
        File[] tempList = dir.listFiles();
        for (File file : tempList) {
            if (file.getName().endsWith(".yml")) {
                YamlConfiguration section = YamlConfiguration.loadConfiguration(file);
                String key = file.getName();
                key = key.substring(0, key.length() - 4);
                Object object = section.get("item");
                if (object instanceof ItemStack) {
                    savedItemMap.put(key, (ItemStack) object);
                } else {
                    savedItemMap.put(key, ItemStack.deserializeBytes((byte[]) object));
                }
            }
        }
    }

    public void saveMainHandItem(Player player, String key) {
        ItemStack itemStack = player.getInventory().getItemInMainHand();
        File dir = new File(MythicTotem.instance.getDataFolder() + "/item");
        if (!dir.exists()) {
            dir.mkdir();
        }
        YamlConfiguration briefcase = new YamlConfiguration();
        if (MythicTotem.isPaper && CommonUtil.getMajorVersion(15) && ConfigManager.configManager.getBoolean("paper-api.save-item", true)) {
            briefcase.set("item", itemStack.serializeAsBytes());
        } else {
            briefcase.set("item", itemStack);
        }
        String yaml = briefcase.saveToString();
        Bukkit.getScheduler().runTaskAsynchronously(MythicTotem.instance,() -> {
            Path path = new File(dir.getPath(), key + ".yml").toPath();
            try {
                Files.write(path, yaml.getBytes(StandardCharsets.UTF_8));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        savedItemMap.put(key, itemStack);
    }

    public ItemStack getItemByKey(String key) {
        if (savedItemMap.containsKey(key)) {
            return savedItemMap.get(key).clone();
        }
        return null;
    }

    public Map<String, ItemStack> getSavedItemMap() {
        return savedItemMap;
    }
}
