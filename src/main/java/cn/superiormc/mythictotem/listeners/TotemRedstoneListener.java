package cn.superiormc.mythictotem.listeners;

import cn.superiormc.mythictotem.MythicTotem;
import cn.superiormc.mythictotem.managers.ConfigManager;
import cn.superiormc.mythictotem.objects.checks.ObjectCheck;
import cn.superiormc.mythictotem.utils.SchedulerUtil;
import cn.superiormc.mythictotem.utils.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockRedstoneEvent;

public class TotemRedstoneListener implements Listener {

    @EventHandler
    public void RedstoneEvent(BlockRedstoneEvent event) {
        SchedulerUtil.runTaskAsynchronously(() -> {
            synchronized(event) {
                new ObjectCheck(event);
            }
        });
        if (ConfigManager.configManager.getBoolean("debug", false)) {
            Bukkit.getConsoleSender().sendMessage(TextUtil.pluginPrefix() + " §eLocation: " + event.getBlock().getLocation());
            //Bukkit.getConsoleSender().sendMessage(TextUtil.pluginPrefix() + " §bIA Block: " + CustomBlock.byAlreadyPlaced(event.getBlock()).getNamespacedID());
        }
    }
}
