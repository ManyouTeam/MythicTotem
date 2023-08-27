package cn.superiormc.mythictotem;

import cn.superiormc.mythictotem.commands.MainTotem;
import cn.superiormc.mythictotem.commands.MainTotemTab;
import cn.superiormc.mythictotem.configs.GeneralSettingConfigs;
import cn.superiormc.mythictotem.configs.TotemConfigs;
import cn.superiormc.mythictotem.events.PlayerClickEvent;
import cn.superiormc.mythictotem.events.PlayerPlaceEvent;
import cn.superiormc.mythictotem.events.TotemRedstoneEvent;
import cn.superiormc.mythictotem.libreforge.TriggerTotemActived;
import cn.superiormc.mythictotem.managers.PlacedBlockCheckManager;
import cn.superiormc.mythictotem.managers.TotemManager;
import cn.superiormc.mythictotem.utils.CheckPluginLoad;
import io.th0rgal.protectionlib.ProtectionLib;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public final class MythicTotem extends JavaPlugin {

    public static JavaPlugin instance;

    public static boolean getError = false;

    public static String lastErrorMessage = "";

    public static boolean freeVersion = false;

    public static int threeDtotemAmount = 0;

    public static List<Block> getCheckingBlock = new ArrayList<>();

    public static List<Player> getCheckingPlayer = new ArrayList<>();

    // 图腾ID，图腾信息
    public static Map<String, TotemManager> getTotemMap = new HashMap<>();

    // 方块ID，方块所在图腾信息
    public static Map<String, List<PlacedBlockCheckManager>> getTotemMaterial = new HashMap<>();

    @Override
    public void onEnable() {
        instance = this;
        ProtectionLib.init(this);
        this.saveDefaultConfig();
        if (GeneralSettingConfigs.GetRegisterLibreforge()) {
            Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[MythicTotem] §fHooking into libreforge...");
            TriggerTotemActived.load();
            Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[MythicTotem] §fRegistered totem_actived trigger for libreforge!");
        }
        TotemConfigs.GetTotemConfigs();
        Events();
        Commands();
        if ((CheckPluginLoad.DoIt("MythicMobs"))) {
            Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[MythicTotem] §fFound MythicMobs in server, try hooking into it...");
        }
        if (freeVersion) {
            Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[MythicTotem] §cYou are using free version, " +
                    "you can only create 3 3D totem with this version.");
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
        if(GeneralSettingConfigs.GetBlockRedstoneEventEnabled()) {
            Bukkit.getPluginManager().registerEvents(new TotemRedstoneEvent(), this);
            Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[MythicTotem] §fEnabled BlockRedstoneEvent trigger.");
        }
    }

    public void Commands() {
        Objects.requireNonNull(Bukkit.getPluginCommand("mythictotem")).setExecutor(new MainTotem());
        Objects.requireNonNull(Bukkit.getPluginCommand("mythictotem")).setTabCompleter(new MainTotemTab());
    }

    public static void checkError(String text) {
        if (!getError || !text.equals(lastErrorMessage)) {
            Bukkit.getConsoleSender().sendMessage(text);
            lastErrorMessage = text;
            getError = true; // Mark the error message as displayed
            new BukkitRunnable() {
                @Override
                public void run() {
                    getError = false; // Reset errorMessageDisplayed after 5 seconds
                }
            }.runTaskLater(instance, 100); // 100 ticks = 5 seconds
        }
    }
}
