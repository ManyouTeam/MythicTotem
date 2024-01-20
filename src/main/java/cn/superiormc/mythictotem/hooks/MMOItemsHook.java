package cn.superiormc.mythictotem.hooks;

import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.Type;
import net.Indyuce.mmoitems.api.item.template.MMOItemTemplate;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MMOItemsHook {

    private static Map<MMOItemTemplate, ItemStack> itemCaches = new ConcurrentHashMap<>();

    //private static boolean using;

    public static void generateNewCache() {
        itemCaches = new ConcurrentHashMap<>();
        for (Type type : MMOItems.plugin.getTypes().getAll()) {
            for (MMOItemTemplate template : MMOItems.plugin.getTemplates().getTemplates(type)) {
                itemCaches.put(template, template.newBuilder().build().newBuilder().build());
            }
        }
    }

    public static ItemStack getItem(MMOItemTemplate template) {
        //using = true;
        ItemStack resultItem = itemCaches.get(template);
        //using = false;
        if (resultItem == null || resultItem.getType() == Material.STONE) {
            return null;
        }
        return resultItem;
    }

}
