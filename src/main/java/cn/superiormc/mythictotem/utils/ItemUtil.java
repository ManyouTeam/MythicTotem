package cn.superiormc.mythictotem.utils;

import cn.superiormc.mythictotem.managers.ConfigManager;
import cn.superiormc.mythictotem.methods.DebuildItem;
import org.bukkit.configuration.MemoryConfiguration;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public class ItemUtil {

    public static boolean isSameItem(ItemStack item1, ItemStack item2) {
        if (ConfigManager.configManager.getString("price.check-method", "Bukkit").equals("Bukkit")) {
            return item1.isSimilar(item2);
        }
        Map<String, Object> item1Result = DebuildItem.debuildItem(item1, new MemoryConfiguration()).getValues(true);
        Map<String, Object> item2Result = DebuildItem.debuildItem(item2, new MemoryConfiguration()).getValues(true);
        for (String key : item1Result.keySet()) {
            if (ConfigManager.configManager.getStringList("price.ignore-item-format-key").contains(key)) {
                continue;
            }
            if (key.equals("amount")) {
                continue;
            }
            if (item2Result.get(key) == null || !item2Result.get(key).equals(item1Result.get(key))) {
                return false;
            }
        }
        return true;
    }

}
