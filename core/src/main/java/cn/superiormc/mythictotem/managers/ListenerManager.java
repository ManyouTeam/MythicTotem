package cn.superiormc.mythictotem.managers;

import cn.superiormc.mythictotem.MythicTotem;
import cn.superiormc.mythictotem.gui.InvGUI;
import cn.superiormc.mythictotem.listeners.*;
import cn.superiormc.mythictotem.utils.CommonUtil;
import cn.superiormc.mythictotem.utils.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ListenerManager {

    public static ListenerManager listenerManager;

    private final Map<UUID, InvGUI> listeners = new HashMap<>();

    public ListenerManager() {
        listenerManager = this;
        registerListeners();
    }

    private void registerListeners() {
        Bukkit.getPluginManager().registerEvents(new GUIListener(), MythicTotem.instance);
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
        if (CommonUtil.getMajorVersion(19) && MythicTotem.methodUtil.methodID().equals("paper") &&
                ConfigManager.configManager.getBoolean("bonus-effects.gui.anti-dupe-checker", false)) {
            Bukkit.getPluginManager().registerEvents(new DupeListener(), MythicTotem.instance);
        }
    }

    public void registerNewGUIListener(Player player, InvGUI inv) {
        unregisterListeners(player);
        listeners.put(player.getUniqueId(), inv);
    }

    public void unregisterNewGUIListener(Player player, InvGUI inv) {
        listeners.remove(player.getUniqueId(), inv);
    }

    public void unregisterListeners(Player player) {
        listeners.remove(player.getUniqueId());
    }

    public InvGUI getInvGUI(Player player) {
        return listeners.get(player.getUniqueId());
    }

    public void unregisterAllListener() {
        HandlerList.unregisterAll(MythicTotem.instance);
    }
}
