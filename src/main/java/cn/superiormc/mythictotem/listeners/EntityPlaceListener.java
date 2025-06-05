package cn.superiormc.mythictotem.listeners;

import cn.superiormc.mythictotem.MythicTotem;
import cn.superiormc.mythictotem.managers.ConfigManager;
import cn.superiormc.mythictotem.objects.checks.ObjectCheck;
import cn.superiormc.mythictotem.utils.SchedulerUtil;
import cn.superiormc.mythictotem.utils.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPlaceEvent;

public class EntityPlaceListener implements Listener {

    @EventHandler
    public void EntityPlace(EntityPlaceEvent event){
        if (event.isCancelled() || event.getPlayer() == null) {
            return;
        }
        if (ConfigManager.configManager.getBoolean("trigger.EntityPlaceEvent.black-creative-mode", false) && event.getPlayer().getGameMode().name().equals("CREATIVE")) {
            return;
        }
        if (ConfigManager.configManager.getBoolean("trigger.EntityPlaceEvent.require-shift", false) && (!event.getPlayer().isSneaking())) {
            return;
        }
        if (ConfigManager.configManager.getCheckingPlayer.contains(event.getPlayer())) {
            return;
        }
        ConfigManager.configManager.getCheckingPlayer.add(event.getPlayer());
        SchedulerUtil.runTaskAsynchronously(() -> {
            synchronized(event) {
                new ObjectCheck(event);
            }
        });
        SchedulerUtil.runTaskLater(() -> ConfigManager.configManager.getCheckingPlayer.remove(event.getPlayer()), ConfigManager.configManager.getLong("cooldown-tick", 5L));
         if (ConfigManager.configManager.getBoolean("debug", false)) {
            Bukkit.getConsoleSender().sendMessage(TextUtil.pluginPrefix() + " §eEntity place trigger!");
        }
    }
}
