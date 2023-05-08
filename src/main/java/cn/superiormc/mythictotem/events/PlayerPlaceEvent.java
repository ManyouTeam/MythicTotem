package cn.superiormc.mythictotem.events;

import cn.superiormc.mythictotem.MythicTotem;
import cn.superiormc.mythictotem.configs.GeneralSettingConfigs;
import cn.superiormc.mythictotem.managers.ValidManager;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

public class PlayerPlaceEvent implements Listener {

    @EventHandler
    public void PlaceEvent(BlockPlaceEvent event){
        if (GeneralSettingConfigs.GetPlayerPlaceEventBlackCreative() && event.getPlayer().getGameMode().name().equals("CREATIVE")) {
            return;
        }
        if (GeneralSettingConfigs.GetBlockPlaceEventRequireShift() && (!event.getPlayer().isSneaking())) {
            return;
        }
        if (MythicTotem.getCheckingPlayer.contains(event.getPlayer())) {
            return;
        }
        MythicTotem.getCheckingPlayer.add(event.getPlayer());
        if (event.canBuild() && (!event.isCancelled())){
            Bukkit.getScheduler().runTaskAsynchronously(MythicTotem.instance, () -> {
                synchronized(event) {
                    new ValidManager(event);
                }
            });
            Bukkit.getScheduler().runTaskLater(MythicTotem.instance, () -> {
                MythicTotem.getCheckingPlayer.remove(event.getPlayer());
            }, MythicTotem.instance.getConfig().getLong("settings.cooldown-tick", 5L));
        }
        if (MythicTotem.instance.getConfig().getBoolean("settings.debug", false)) {
            Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[MythicTotem] §eLocation: " + event.getBlockPlaced().getLocation());
            //Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[MythicTotem] §bIA Block: " + CustomBlock.byAlreadyPlaced(event.getBlockPlaced()).getNamespacedID());
        }
    }
}
