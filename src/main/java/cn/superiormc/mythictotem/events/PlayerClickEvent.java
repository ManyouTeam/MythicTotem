package cn.superiormc.mythictotem.events;

import cn.superiormc.mythictotem.MythicTotem;
import cn.superiormc.mythictotem.configs.GeneralSettingConfigs;
import cn.superiormc.mythictotem.managers.ValidManager;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

public class PlayerClickEvent implements Listener {

    @EventHandler
    public void InteractEvent(PlayerInteractEvent event) {
        if (GeneralSettingConfigs.GetPlayerInteractEventRequireShift() && (!event.getPlayer().isSneaking())) {
            return;
        }
        Bukkit.getScheduler().runTaskAsynchronously(MythicTotem.instance, () -> {
            Object obj = new Object();
            synchronized(obj) {
                new ValidManager(event);
            }
        });
    }
}
