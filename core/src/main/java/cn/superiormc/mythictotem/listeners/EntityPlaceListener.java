package cn.superiormc.mythictotem.listeners;

import cn.superiormc.mythictotem.managers.ConfigManager;
import cn.superiormc.mythictotem.managers.RuntimeStateManager;
import cn.superiormc.mythictotem.objects.checks.ObjectCheck;
import cn.superiormc.mythictotem.utils.SchedulerUtil;
import cn.superiormc.mythictotem.utils.TextUtil;
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
        if (RuntimeStateManager.runtimeStateManager.isPlayerCoolingDown(event.getPlayer())) {
            return;
        }
        RuntimeStateManager.runtimeStateManager.startPlayerCooldown(event.getPlayer());
        SchedulerUtil.runSync(event.getEntity(), () -> {
            synchronized(event) {
                new ObjectCheck(event);
            }
        });
         if (ConfigManager.configManager.getBoolean("debug", false)) {
            TextUtil.sendMessage(null, TextUtil.pluginPrefix() + " §eEntity place trigger!");
        }
    }
}
