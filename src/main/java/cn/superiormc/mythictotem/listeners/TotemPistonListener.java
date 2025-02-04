package cn.superiormc.mythictotem.listeners;

import cn.superiormc.mythictotem.MythicTotem;
import cn.superiormc.mythictotem.objects.checks.ObjectCheck;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPistonExtendEvent;

public class TotemPistonListener implements Listener {

    @EventHandler
    public void PistonEvent(BlockPistonExtendEvent event) {
        Bukkit.getScheduler().runTaskLaterAsynchronously(MythicTotem.instance, () -> {
            synchronized(event) {
                new ObjectCheck(event);
            }
        }, event.getBlocks().size() + 4);
    }
}
