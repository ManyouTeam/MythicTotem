package cn.superiormc.mythictotem.managers;

import cn.superiormc.mythictotem.MythicTotem;
import cn.superiormc.mythictotem.listeners.BlockIdInspectListener;
import cn.superiormc.mythictotem.listeners.BonusEffectsListener;
import cn.superiormc.mythictotem.listeners.EntityPlaceListener;
import cn.superiormc.mythictotem.listeners.PlayerClickListener;
import cn.superiormc.mythictotem.listeners.PlayerDropListener;
import cn.superiormc.mythictotem.listeners.PlayerPlaceListener;
import cn.superiormc.mythictotem.listeners.TotemPistonListener;
import cn.superiormc.mythictotem.listeners.TotemRedstoneListener;
import cn.superiormc.mythictotem.utils.TextUtil;
import org.bukkit.Bukkit;

public class ListenerManager {

    public static ListenerManager listenerManager;

    public ListenerManager() {
        listenerManager = this;
        registerListeners();
    }

    private void registerListeners() {
        Bukkit.getPluginManager().registerEvents(new BlockIdInspectListener(), MythicTotem.instance);
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
        if (!MythicTotem.isFolia && ConfigManager.configManager.getBoolean("bonus-effects.enabled", false)) {
            Bukkit.getPluginManager().registerEvents(new BonusEffectsListener(), MythicTotem.instance);
        }
    }
}
