package cn.superiormc.mythictotem;

import cn.superiormc.mythictotem.commands.MainTotem;
import cn.superiormc.mythictotem.configs.GeneralSettingConfigs;
import cn.superiormc.mythictotem.configs.TotemConfigs;
import cn.superiormc.mythictotem.events.PlayerClickEvent;
import cn.superiormc.mythictotem.events.PlayerPlaceEvent;
import cn.superiormc.mythictotem.managers.*;
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
        Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[MythicTotem] §fPlugin is loaded. Author: PQguanfang.");
    }

    @Override
    public void onDisable() {
        Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[MythicTotem] §fPlugin is disabled. Author: PQguanfang.");
    }

    public void Events() {
        if(GeneralSettingConfigs.GetBlockPlaceEventEnabled()) {
            Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[MythicTotem] §fEnabled BlockPlaceEvent trigger.");
            Bukkit.getPluginManager().registerEvents(new PlayerPlaceEvent(), this);
        }
        if(GeneralSettingConfigs.GetPlayerInteractEventEnabled()) {
            Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[MythicTotem] §fEnabled PlayerInteractEvent trigger.");
            Bukkit.getPluginManager().registerEvents(new PlayerClickEvent(), this);
        }
    }

    public void Commands() {
        Objects.requireNonNull(Bukkit.getPluginCommand("mythictotem")).setExecutor(new MainTotem());
    }

    public static void SetErrorValue(){
        if (!getError){
            Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[MythicTotem] §cFailed to load plugin configs, see errors below to fix this.");
            getError = true;
        }
    }
}
