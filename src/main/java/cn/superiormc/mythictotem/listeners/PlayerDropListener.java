package cn.superiormc.mythictotem.listeners;

import cn.superiormc.mythictotem.MythicTotem;
import cn.superiormc.mythictotem.configs.GeneralSettingConfigs;
import cn.superiormc.mythictotem.managers.ValidManager;
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
        if (GeneralSettingConfigs.GetPlayerDropEventBlackCreative() && event.getPlayer().getGameMode().name().equals("CREATIVE")) {
            return;
        }
        if (GeneralSettingConfigs.GetPlayerDropEventRequireShift() && (!event.getPlayer().isSneaking())) {
            return;
        }
        if (MythicTotem.getCheckingPlayer.contains(event.getPlayer())) {
            return;
        }
        MythicTotem.getCheckingPlayer.add(event.getPlayer());
        Bukkit.getScheduler().runTaskAsynchronously(MythicTotem.instance, () -> {
            synchronized(event) {
                new ValidManager(event);
            }
        });
        if (MythicTotem.instance.getConfig().getBoolean("debug", false)) {
            Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[MythicTotem] §eDrop trigger!");
        }
        Bukkit.getScheduler().runTaskLater(MythicTotem.instance, () -> {
            MythicTotem.getCheckingPlayer.remove(event.getPlayer());
        }, MythicTotem.instance.getConfig().getLong("cooldown-tick", 5L));
    }

    @EventHandler
    public void InventoryPickupEvent(InventoryPickupItemEvent event) {
        if (event.isCancelled()) {
            return;
        }
        if (MythicTotem.getDroppedItems.contains(event.getItem())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void EntityPickupEvent(EntityPickupItemEvent event) {
        if (event.isCancelled()) {
            return;
        }
        if (MythicTotem.getDroppedItems.contains(event.getItem())) {
            event.setCancelled(true);
        }
    }
}
