package cn.superiormc.mythictotem.events;

import cn.superiormc.mythictotem.MythicTotem;
import cn.superiormc.mythictotem.configs.GeneralSettingConfigs;
import cn.superiormc.mythictotem.managers.ValidManager;
import dev.lone.itemsadder.api.CustomBlock;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.inventory.EquipmentSlot;

public class TotemRedstoneEvent implements Listener {

    @EventHandler
    public void RedstoneEvent(BlockRedstoneEvent event) {
        if (event.isCancelled()) {
            return;
        }
        Bukkit.getScheduler().runTaskAsynchronously(MythicTotem.instance, () -> {
            synchronized(event) {
                new ValidManager(event);
            }
        });
        if (MythicTotem.instance.getConfig().getBoolean("settings.debug", false)) {
            Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[MythicTotem] §eLocation: " + event.getBlock().getLocation());
            Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[MythicTotem] §bIA Block: " + CustomBlock.byAlreadyPlaced(event.getBlock()).getNamespacedID());
        }
    }
}
