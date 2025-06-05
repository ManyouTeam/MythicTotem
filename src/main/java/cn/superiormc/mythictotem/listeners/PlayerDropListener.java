package cn.superiormc.mythictotem.listeners;

import cn.superiormc.mythictotem.managers.ConfigManager;
import cn.superiormc.mythictotem.objects.checks.ObjectCheck;
import cn.superiormc.mythictotem.utils.SchedulerUtil;
import cn.superiormc.mythictotem.utils.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.event.player.PlayerDropItemEvent;

public class PlayerDropListener implements Listener {

    @EventHandler
    public void DropEvent(PlayerDropItemEvent event) {
        if (event.isCancelled()) {
            return;
        }
        if (ConfigManager.configManager.getBoolean("trigger.PlayerDropItemEvent.black-creative-mode", true) && event.getPlayer().getGameMode().name().equals("CREATIVE")) {
            return;
        }
        if (ConfigManager.configManager.getBoolean("trigger.PlayerDropItemEvent.require-shift", true) && (!event.getPlayer().isSneaking())) {
            return;
        }
        if (ConfigManager.configManager.getCheckingPlayer.contains(event.getPlayer())) {
            return;
        }
        ConfigManager.configManager.getCheckingPlayer.add(event.getPlayer());
        SchedulerUtil.runTaskAsynchronously(() -> {
            synchronized(event) {
                new ObjectCheck(event);
            }
        });
        if (ConfigManager.configManager.getBoolean("debug", false)) {
            Bukkit.getConsoleSender().sendMessage(TextUtil.pluginPrefix() + " §eDrop trigger!");
        }
        SchedulerUtil.runTaskLater(() -> ConfigManager.configManager.getCheckingPlayer.remove(event.getPlayer()),
                ConfigManager.configManager.getLong("cooldown-tick", 5L));
    }

    @EventHandler
    public void InventoryPickupEvent(InventoryPickupItemEvent event) {
        if (event.isCancelled()) {
            return;
        }
        if (ConfigManager.configManager.getDroppedItems.contains(event.getItem())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void EntityPickupEvent(EntityPickupItemEvent event) {
        if (event.isCancelled()) {
            return;
        }
        if (ConfigManager.configManager.getDroppedItems.contains(event.getItem())) {
            event.setCancelled(true);
        }
    }
}
