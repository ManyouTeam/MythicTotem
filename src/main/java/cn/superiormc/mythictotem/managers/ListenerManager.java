package cn.superiormc.mythictotem.managers;
import cn.superiormc.mythictotem.MythicTotem;
import cn.superiormc.mythictotem.listeners.*;
import org.bukkit.Bukkit;

public class ListenerManager {

    public static ListenerManager listenerManager;

    public ListenerManager(){
        listenerManager = this;
        registerListeners();
    }

    private void registerListeners(){
        if (ConfigManager.configManager.getBoolean("trigger.BlockPlaceEvent.enabled", true)) {
            Bukkit.getPluginManager().registerEvents(new PlayerPlaceListener(), MythicTotem.instance);
            Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[MythicTotem] §fEnabled BlockPlaceEvent trigger.");
        }
        if (ConfigManager.configManager.getBoolean("trigger.PlayerInteractEvent.enabled", true)) {
            Bukkit.getPluginManager().registerEvents(new PlayerClickListener(), MythicTotem.instance);
            Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[MythicTotem] §fEnabled PlayerInteractEvent trigger.");
        }
        if (ConfigManager.configManager.getBoolean("trigger.BlockRedstoneEvent.enabled", true)) {
            Bukkit.getPluginManager().registerEvents(new TotemRedstoneListener(), MythicTotem.instance);
            Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[MythicTotem] §fEnabled BlockRedstoneEvent trigger.");
        }
        if (ConfigManager.configManager.getBoolean("trigger.PlayerDropItemEvent.enabled", true)) {
            Bukkit.getPluginManager().registerEvents(new PlayerDropListener(), MythicTotem.instance);
            Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[MythicTotem] §fEnabled PlayerDropItemEvent trigger.");
        }
        if (ConfigManager.configManager.getBoolean("trigger.BlockPistonEvent.enabled", true) && !MythicTotem.freeVersion) {
            Bukkit.getPluginManager().registerEvents(new TotemPistonListener(), MythicTotem.instance);
            Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[MythicTotem] §fEnabled BlockPistonEvent trigger.");
        }
        if (ConfigManager.configManager.getBoolean("trigger.EntityPlaceEvent.enabled", true)) {
            Bukkit.getPluginManager().registerEvents(new EntityPlaceListener(), MythicTotem.instance);
            Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[MythicTotem] §fEnabled EntityPlaceEvent trigger.");
        }
    }
}
