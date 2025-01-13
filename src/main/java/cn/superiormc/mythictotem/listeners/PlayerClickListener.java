package cn.superiormc.mythictotem.listeners;

import cn.superiormc.mythictotem.MythicTotem;
import cn.superiormc.mythictotem.managers.ConfigManager;
import cn.superiormc.mythictotem.objects.checks.ObjectCheck;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

public class PlayerClickListener implements Listener {

    @EventHandler
    public void InteractEvent(PlayerInteractEvent event) {
        if (event.isCancelled()) {
            return;
        }
        if (ConfigManager.configManager.getBoolean("trigger.PlayerInteractEvent.black-creative-mode", true) && event.getPlayer().getGameMode().name().equals("CREATIVE")) {
            return;
        }
        if (ConfigManager.configManager.getBoolean("trigger.PlayerInteractEvent.require-shift", true) && (!event.getPlayer().isSneaking())) {
            return;
        }
        if (ConfigManager.configManager.getCheckingPlayer.contains(event.getPlayer())) {
            return;
        }
        if (!EquipmentSlot.HAND.equals(event.getHand())) {
            return;
        }
        ConfigManager.configManager.getCheckingPlayer.add(event.getPlayer());
        Bukkit.getScheduler().runTaskAsynchronously(MythicTotem.instance, () -> {
            synchronized(event) {
                new ObjectCheck(event);
            }
        });
        Bukkit.getScheduler().runTaskLater(MythicTotem.instance, () -> {
            ConfigManager.configManager.getCheckingPlayer.remove(event.getPlayer());
        }, ConfigManager.configManager.getLong("cooldown-tick", 5L));
         if (ConfigManager.configManager.getBoolean("debug", false)) {
            Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[MythicTotem] §eLocation: " + event.getClickedBlock().getLocation());
            //Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[MythicTotem] §bIA Block: " + CustomBlock.byAlreadyPlaced(event.getClickedBlock()).getNamespacedID());
            //Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[MythicTotem] §cBiome: " + event.getClickedBlock().getBiome().name());
        }
    }
}
