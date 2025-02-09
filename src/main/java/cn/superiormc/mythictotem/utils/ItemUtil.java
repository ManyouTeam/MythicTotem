package cn.superiormc.mythictotem.utils;

import cn.superiormc.mythictotem.managers.ConfigManager;
import cn.superiormc.mythictotem.methods.DebuildItem;
import org.bukkit.configuration.MemoryConfiguration;
import org.bukkit.configuration.MemorySection;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public class ItemUtil {

    public static boolean isSameItem(ItemStack item1, ItemStack item2) {
        if (ConfigManager.configManager.getString("item-price.check-method", "item-price-mode").equals("Bukkit")) {
            return item1.isSimilar(item2);
        }
        Map<String, Object> item1Result = DebuildItem.debuildItem(item1, new MemoryConfiguration()).getValues(true);
        Map<String, Object> item2Result = DebuildItem.debuildItem(item2, new MemoryConfiguration()).getValues(true);
        if (ConfigManager.configManager.getBoolean("item-price.item-format.require-same-key", false)) {
            for (String key : item1Result.keySet()) {
                if (canIgnore(key)) {
                    continue;
                }
                if (!item2Result.containsKey(key)) {
                    return false;
                }
            }
        }
        for (String key : item2Result.keySet()) {
            if (canIgnore(key)) {
                continue;
            }
            Object object = item1Result.get(key);
            if (object == null) {
                return false;
            }
            if (object instanceof MemorySection) {
                continue;
            }
            if (!object.equals(item2Result.get(key))) {
                return false;
            }
        }
        return true;
    }

    public static boolean canIgnore(String key) {
        if (key == null) {
            return true;
        }
        if (key.equals("amount")) {
            return true;
        }
        for (String tempVal1 : ConfigManager.configManager.getStringList("item-price.item-format.ignore-key")) {
            if (tempVal1.equals(key) || key.startsWith(tempVal1 + ".")) {
                return true;
            }
        }
        return false;
    }

}
