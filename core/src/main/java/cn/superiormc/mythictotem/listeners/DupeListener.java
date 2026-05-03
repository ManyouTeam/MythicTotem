package cn.superiormc.mythictotem.listeners;

import cn.superiormc.mythictotem.managers.LanguageManager;
import cn.superiormc.mythictotem.methods.Dupe;
import io.papermc.paper.event.player.PlayerInventorySlotChangeEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

public class DupeListener implements Listener {

    @EventHandler
    public void onCheckChange(PlayerInventorySlotChangeEvent event) {
        ItemStack item = event.getNewItemStack();
        if (Dupe.isGuiDisplayItem(item)) {
            event.getPlayer().getInventory().setItem(event.getSlot(), null);
            LanguageManager.languageManager.sendStringText(event.getPlayer(), "dupe-removed");
        }
    }
}
