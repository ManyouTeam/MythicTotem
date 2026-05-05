package cn.superiormc.mythictotem.listeners;

import cn.superiormc.mythictotem.managers.ConfigManager;
import cn.superiormc.mythictotem.objects.checks.ObjectCheck;
import cn.superiormc.mythictotem.utils.SchedulerUtil;
import cn.superiormc.mythictotem.utils.TextUtil;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPistonExtendEvent;

public class TotemPistonListener implements Listener {

    @EventHandler
    public void PistonEvent(BlockPistonExtendEvent event) {
        SchedulerUtil.runTaskLater(event.getBlock().getLocation(), () -> new ObjectCheck(event), event.getBlocks().size() + 4);
        if (ConfigManager.configManager.getBoolean("debug", false)) {
            TextUtil.sendMessage(null, TextUtil.pluginPrefix() + " §ePiston trigger!");
        }
    }
}
