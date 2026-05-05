package cn.superiormc.mythictotem.listeners;

import cn.superiormc.mythictotem.managers.ConfigManager;
import cn.superiormc.mythictotem.managers.RuntimeStateManager;
import cn.superiormc.mythictotem.objects.checks.ObjectCheck;
import cn.superiormc.mythictotem.utils.TextUtil;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.EquipmentSlot;

public class PlayerPlaceListener implements Listener {

    @EventHandler
    public void PlaceEvent(BlockPlaceEvent event){
        if (event.isCancelled()) {
            return;
        }
        if (ConfigManager.configManager.getBoolean("trigger.BlockPlaceEvent.black-creative-mode", true) && event.getPlayer().getGameMode().name().equals("CREATIVE")) {
            return;
        }
        if (ConfigManager.configManager.getBoolean("trigger.BlockPlaceEvent.require-shift", false) && (!event.getPlayer().isSneaking())) {
            return;
        }
        if (RuntimeStateManager.runtimeStateManager.isPlayerCoolingDown(event.getPlayer())) {
            return;
        }
        if (!EquipmentSlot.HAND.equals(event.getHand())) {
            return;
        }
        if (event.canBuild() && (!event.isCancelled())) {
            RuntimeStateManager.runtimeStateManager.startPlayerCooldown(event.getPlayer());
            new ObjectCheck(event);
        }
        if (ConfigManager.configManager.getBoolean("debug", false)) {
            TextUtil.sendMessage(null, TextUtil.pluginPrefix() + " §ePlace trigger!");
        }
    }
}
