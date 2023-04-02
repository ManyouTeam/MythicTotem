package cn.superiormc.mythictotem;

import cn.superiormc.mythictotem.commands.MainTotem;
import cn.superiormc.mythictotem.commands.MainTotemTab;
import cn.superiormc.mythictotem.configs.GeneralSettingConfigs;
import cn.superiormc.mythictotem.configs.TotemConfigs;
import cn.superiormc.mythictotem.events.PlayerClickEvent;
import cn.superiormc.mythictotem.events.PlayerPlaceEvent;
import cn.superiormc.mythictotem.managers.*;
import cn.superiormc.mythictotem.utils.CheckPluginLoad;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public final class MythicTotem extends JavaPlugin {

    public static JavaPlugin instance;

    public static boolean getError = false;

    // 图腾ID，图腾信息
    public static Map<String, TotemManager> getTotemMap = new HashMap<>();

    // 方块ID，方块所在图腾信息
    public static Map<String, List<PlacedBlockCheckManager>> getTotemMaterial = new HashMap<>();

    @Override
    public void onEnable() {
        instance = this;
        this.saveDefaultConfig();
        TotemConfigs.GetTotemConfigs();
        Events();
        Commands();
        if ((CheckPluginLoad.DoIt("MythicMobs"))) {
            Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[MythicTotem] §fFound MythicMobs in server, try hooking into it...");
        }
        Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[MythicTotem] §fPlugin is loaded. Author: PQguanfang.");
    }

    @Override
    public void onDisable() {
        Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[MythicTotem] §fPlugin is disabled. Author: PQguanfang.");
    }

    public void Events() {
        if(GeneralSettingConfigs.GetBlockPlaceEventEnabled()) {
            Bukkit.getPluginManager().registerEvents(new PlayerPlaceEvent(), this);
            Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[MythicTotem] §fEnabled BlockPlaceEvent trigger.");
        }
        if(GeneralSettingConfigs.GetPlayerInteractEventEnabled()) {
            Bukkit.getPluginManager().registerEvents(new PlayerClickEvent(), this);
            Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[MythicTotem] §fEnabled PlayerInteractEvent trigger.");
        }
    }

    public void Commands() {
        Objects.requireNonNull(Bukkit.getPluginCommand("mythictotem")).setExecutor(new MainTotem());
        Objects.requireNonNull(Bukkit.getPluginCommand("mythictotem")).setTabCompleter(new MainTotemTab());
    }

    public static void SetErrorValue(){
        if (!getError){
            Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[MythicTotem] §cFailed to load plugin configs, see errors below to fix this.");
            getError = true;
        }
    }
}
