package cn.superiormc.mythictotem.events;

import cn.superiormc.mythictotem.MythicTotem;
import cn.superiormc.mythictotem.configs.GeneralSettingConfigs;
import cn.superiormc.mythictotem.managers.ValidManager;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

public class PlayerClickEvent implements Listener {

    @EventHandler
    public void InteractEvent(PlayerInteractEvent event) {
        if (event.isCancelled()) {
            return;
        }
        if (GeneralSettingConfigs.GetPlayerInteractEventBlackCreative() && event.getPlayer().getGameMode().name().equals("CREATIVE")) {
            return;
        }
        if (GeneralSettingConfigs.GetPlayerInteractEventRequireShift() && (!event.getPlayer().isSneaking())) {
            return;
        }
        if (MythicTotem.getCheckingPlayer.contains(event.getPlayer())) {
            return;
        }
        if (!EquipmentSlot.HAND.equals(event.getHand())) {
            return;
        }
        MythicTotem.getCheckingPlayer.add(event.getPlayer());
        Bukkit.getScheduler().runTaskAsynchronously(MythicTotem.instance, () -> {
            synchronized(event) {
                new ValidManager(event);
            }
        });
        Bukkit.getScheduler().runTaskLater(MythicTotem.instance, () -> {
            MythicTotem.getCheckingPlayer.remove(event.getPlayer());
        }, MythicTotem.instance.getConfig().getLong("settings.cooldown-tick", 5L));
        if (MythicTotem.instance.getConfig().getBoolean("settings.debug", false)) {
            Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[MythicTotem] §eLocation: " + event.getClickedBlock().getLocation());
            //Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[MythicTotem] §bIA Block: " + CustomBlock.byAlreadyPlaced(event.getClickedBlock()).getNamespacedID());
            Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[MythicTotem] §cBiome: " + event.getClickedBlock().getBiome().name());
        }
    }
}
