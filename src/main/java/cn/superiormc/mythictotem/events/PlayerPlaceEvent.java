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
        if (GeneralSettingConfigs.GetBlockPlaceEventRequireShift() && (!event.getPlayer().isSneaking())) {
            return;
        }
        if (event.canBuild() && (!event.isCancelled())){
            Bukkit.getScheduler().runTaskAsynchronously(MythicTotem.instance, () -> {
                synchronized(event) {
                    new ValidManager(event);
                }
            });
        }
        if (MythicTotem.instance.getConfig().getBoolean("settings.debug")) {
            Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[MythicTotem] §eLocation: " + event.getBlockPlaced().getLocation());
            //Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[MythicTotem] §bIA Block: " + CustomBlock.byAlreadyPlaced(event.getBlockPlaced()).getNamespacedID());
        }
    }
}
