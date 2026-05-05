package cn.superiormc.mythictotem.listeners;

import cn.superiormc.mythictotem.managers.ConfigManager;
import cn.superiormc.mythictotem.managers.RuntimeStateManager;
import cn.superiormc.mythictotem.objects.checks.ObjectCheck;
import cn.superiormc.mythictotem.utils.TextUtil;
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
        if (RuntimeStateManager.runtimeStateManager.isPlayerCoolingDown(event.getPlayer())) {
            return;
        }
        RuntimeStateManager.runtimeStateManager.startPlayerCooldown(event.getPlayer());
        new ObjectCheck(event);
        if (ConfigManager.configManager.getBoolean("debug", false)) {
            TextUtil.sendMessage(null, TextUtil.pluginPrefix() + " §eDrop trigger!");
        }
    }

    @EventHandler
    public void InventoryPickupEvent(InventoryPickupItemEvent event) {
        if (event.isCancelled()) {
            return;
        }
        if (RuntimeStateManager.runtimeStateManager.isDroppedItem(event.getItem())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void EntityPickupEvent(EntityPickupItemEvent event) {
        if (event.isCancelled()) {
            return;
        }
        if (RuntimeStateManager.runtimeStateManager.isDroppedItem(event.getItem())) {
            event.setCancelled(true);
        }
    }
}
