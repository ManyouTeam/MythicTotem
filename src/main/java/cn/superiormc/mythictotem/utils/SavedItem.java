package cn.superiormc.mythictotem.utils;

import cn.superiormc.mythictotem.MythicTotem;
import cn.superiormc.mythictotem.managers.SavedItemManager;
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

public class SavedItem {

    private static Map<String, SavedItemManager> savedItemMap = new HashMap<>();
    public static void SaveMainHandItem(Player player, String key) {
        ItemStack itemStack = player.getInventory().getItemInMainHand();
        File dir = new File(MythicTotem.instance.getDataFolder()+"/item");
        if(!dir.exists()) {
            dir.mkdir();
        }
        YamlConfiguration briefcase = new YamlConfiguration();
        briefcase.set("item", itemStack);
        String yaml = briefcase.saveToString();
        Bukkit.getScheduler().runTaskAsynchronously(MythicTotem.instance,() -> {
            Path path = new File(dir.getPath(),key+".yml").toPath();
            try {
                Files.write(path,yaml.getBytes(StandardCharsets.UTF_8));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        savedItemMap.put(key, new SavedItemManager(key, itemStack));
    }
    public static void ReadSavedItems() {
        savedItemMap.clear();
        File dir = new File(MythicTotem.instance.getDataFolder()+"/item");
        if(!dir.exists()) {
            dir.mkdir();
        }
        File[] tempList = dir.listFiles();
        for (File file : tempList) {
            if (file.getName().endsWith(".yml")) {
                ItemStack itemStack = YamlConfiguration.loadConfiguration(file).getItemStack("item");
                String key = file.getName();
                key = key.substring(0, key.length()-4);
                savedItemMap.put(key, new SavedItemManager(key, itemStack));
            }
        }
    }
    public static ItemStack GetItemByKey(String key) {
        if (savedItemMap.containsKey(key)) {
            return savedItemMap.get(key).GetItemStack();
        }
        return null;
    }
}
