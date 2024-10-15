package cn.superiormc.mythictotem.listeners;

import cn.superiormc.mythictotem.MythicTotem;
import cn.superiormc.mythictotem.managers.ConfigManager;
import cn.superiormc.mythictotem.objects.checks.ObjectCheck;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockRedstoneEvent;

public class TotemRedstoneListener implements Listener {

    @EventHandler
    public void RedstoneEvent(BlockRedstoneEvent event) {
        Bukkit.getScheduler().runTaskAsynchronously(MythicTotem.instance, () -> {
            synchronized(event) {
                new ObjectCheck(event);
            }
        });
        if (ConfigManager.configManager.getBoolean("debug", false)) {
            Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[MythicTotem] §eLocation: " + event.getBlock().getLocation());
            //Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[MythicTotem] §bIA Block: " + CustomBlock.byAlreadyPlaced(event.getBlock()).getNamespacedID());
        }
    }
}
