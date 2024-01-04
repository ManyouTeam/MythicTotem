package cn.superiormc.mythictotem.events;

import cn.superiormc.mythictotem.MythicTotem;
import cn.superiormc.mythictotem.configs.GeneralSettingConfigs;
import cn.superiormc.mythictotem.managers.ValidManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPlaceEvent;

public class EntityPlaceListener implements Listener {

    @EventHandler
    public void EntityPlace(EntityPlaceEvent event){
        if (event.isCancelled() || event.getPlayer() == null) {
            return;
        }
        if (GeneralSettingConfigs.GetEntityPlaceEventBlackCreative() && event.getPlayer().getGameMode().name().equals("CREATIVE")) {
            return;
        }
        if (GeneralSettingConfigs.GetEntityPlaceEventRequireShift() && (!event.getPlayer().isSneaking())) {
            return;
        }
        if (MythicTotem.getCheckingPlayer.contains(event.getPlayer())) {
            return;
        }
        if (event.getEntity().getType() != EntityType.ENDER_CRYSTAL) {
            return;
        }
        MythicTotem.getCheckingPlayer.add(event.getPlayer());
        Bukkit.getScheduler().runTaskAsynchronously(MythicTotem.instance, () -> {
            synchronized(event) {
                new ValidManager(event);
            }
        });
        Bukkit.getScheduler().runTaskLater(MythicTotem.instance, () -> {
            MythicTotem.getCheckingPlayer.remove(event.getPlayer());
        }, MythicTotem.instance.getConfig().getLong("settings.cooldown-tick", 5L));
        if (MythicTotem.instance.getConfig().getBoolean("settings.debug", false)) {
            Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[MythicTotem] §eEntity place trigger!");
        }
    }
}
