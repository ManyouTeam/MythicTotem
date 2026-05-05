package cn.superiormc.mythictotem.listeners;

import cn.superiormc.mythictotem.managers.ConfigManager;
import cn.superiormc.mythictotem.objects.checks.ObjectCheck;
import cn.superiormc.mythictotem.utils.SchedulerUtil;
import cn.superiormc.mythictotem.utils.TextUtil;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockRedstoneEvent;

public class TotemRedstoneListener implements Listener {

    @EventHandler
    public void RedstoneEvent(BlockRedstoneEvent event) {
        SchedulerUtil.runSync(event.getBlock().getLocation(), () -> new ObjectCheck(event));
        if (ConfigManager.configManager.getBoolean("debug", false)) {
            TextUtil.sendMessage(null, TextUtil.pluginPrefix() + " §eRedstone trigger!");
        }
    }
}
