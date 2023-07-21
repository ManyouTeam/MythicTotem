package cn.superiormc.mythictotem.managers;

import org.bukkit.inventory.ItemStack;

public class SavedItemManager {

    private String key;
    private ItemStack itemStack;
    public SavedItemManager(String key, ItemStack itemStack) {
        this.key = key;
        this.itemStack = itemStack;
    }
    public ItemStack GetItemStack() {
        return itemStack;
    }
    public String GetKey() {
        return key;
    }

}
