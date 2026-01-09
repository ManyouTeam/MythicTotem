package cn.superiormc.mythictotem.managers;
import cn.superiormc.mythictotem.MythicTotem;
import cn.superiormc.mythictotem.listeners.*;
import cn.superiormc.mythictotem.utils.TextUtil;
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
            TextUtil.sendMessage(null, TextUtil.pluginPrefix() + " §fEnabled BlockPlaceEvent trigger.");
        }
        if (ConfigManager.configManager.getBoolean("trigger.PlayerInteractEvent.enabled", true)) {
            Bukkit.getPluginManager().registerEvents(new PlayerClickListener(), MythicTotem.instance);
            TextUtil.sendMessage(null, TextUtil.pluginPrefix() + " §fEnabled PlayerInteractEvent trigger.");
        }
        if (ConfigManager.configManager.getBoolean("trigger.BlockRedstoneEvent.enabled", true)) {
            Bukkit.getPluginManager().registerEvents(new TotemRedstoneListener(), MythicTotem.instance);
            TextUtil.sendMessage(null, TextUtil.pluginPrefix() + " §fEnabled BlockRedstoneEvent trigger.");
        }
        if (ConfigManager.configManager.getBoolean("trigger.PlayerDropItemEvent.enabled", true)) {
            Bukkit.getPluginManager().registerEvents(new PlayerDropListener(), MythicTotem.instance);
            TextUtil.sendMessage(null, TextUtil.pluginPrefix() + " §fEnabled PlayerDropItemEvent trigger.");
        }
        if (ConfigManager.configManager.getBoolean("trigger.BlockPistonEvent.enabled", true) && !MythicTotem.freeVersion) {
            Bukkit.getPluginManager().registerEvents(new TotemPistonListener(), MythicTotem.instance);
            TextUtil.sendMessage(null, TextUtil.pluginPrefix() + " §fEnabled BlockPistonEvent trigger.");
        }
        if (ConfigManager.configManager.getBoolean("trigger.EntityPlaceEvent.enabled", true)) {
            Bukkit.getPluginManager().registerEvents(new EntityPlaceListener(), MythicTotem.instance);
            TextUtil.sendMessage(null, TextUtil.pluginPrefix() + " §fEnabled EntityPlaceEvent trigger.");
        }
    }
}
