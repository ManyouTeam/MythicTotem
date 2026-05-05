package cn.superiormc.mythictotem.listeners;

import cn.superiormc.mythictotem.managers.ConfigManager;
import cn.superiormc.mythictotem.managers.RuntimeStateManager;
import cn.superiormc.mythictotem.objects.checks.ObjectCheck;
import cn.superiormc.mythictotem.utils.SchedulerUtil;
import cn.superiormc.mythictotem.utils.TextUtil;
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
        if (!EquipmentSlot.HAND.equals(event.getHand())) {
            return;
        }
        if (event.getClickedBlock() == null) {
            return;
        }

        SchedulerUtil.runTaskLater(event.getClickedBlock().getLocation(), () -> {
            if (RuntimeStateManager.runtimeStateManager.isPlayerCoolingDown(event.getPlayer())) {
                return;
            }
            RuntimeStateManager.runtimeStateManager.startPlayerCooldown(event.getPlayer());
            new ObjectCheck(event);
        }, 1L);
        if (ConfigManager.configManager.getBoolean("debug", false)) {
            TextUtil.sendMessage(null, TextUtil.pluginPrefix() + " §eClick trigger!");
        }
    }
}
