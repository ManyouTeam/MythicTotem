package cn.superiormc.mythictotem.hooks;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class MMOItemsReloadListener implements Listener {

    @EventHandler
    public void onReloadMMOItems(PlayerCommandPreprocessEvent event) {
        if (event.getMessage().contains("mmoitems reload") || event.getMessage().contains("mi reload")) {
            MMOItemsHook.generateNewCache();
        }
    }
}
