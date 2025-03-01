package cn.superiormc.mythictotem.listeners;

import cn.superiormc.mythictotem.objects.checks.ObjectCheck;
import cn.superiormc.mythictotem.utils.SchedulerUtil;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPistonExtendEvent;

public class TotemPistonListener implements Listener {

    @EventHandler
    public void PistonEvent(BlockPistonExtendEvent event) {
        SchedulerUtil.runTaskLaterAsynchronously( () -> {
            synchronized(event) {
                new ObjectCheck(event);
            }
        }, event.getBlocks().size() + 4);
    }
}
