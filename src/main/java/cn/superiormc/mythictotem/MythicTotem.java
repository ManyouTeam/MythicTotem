package cn.superiormc.mythictotem;

import cn.superiormc.mythictotem.commands.MainTotem;
import cn.superiormc.mythictotem.commands.MainTotemTab;
import cn.superiormc.mythictotem.configs.GeneralSettingConfigs;
import cn.superiormc.mythictotem.configs.Messages;
import cn.superiormc.mythictotem.configs.TotemConfigs;
import cn.superiormc.mythictotem.listeners.*;
import cn.superiormc.mythictotem.managers.InitManager;
import cn.superiormc.mythictotem.managers.PlacedBlockCheckManager;
import cn.superiormc.mythictotem.managers.TotemManager;
import cn.superiormc.mythictotem.managers.SavedItemManager;
import io.th0rgal.protectionlib.ProtectionLib;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public final class MythicTotem extends JavaPlugin {

    public static JavaPlugin instance;

    public static boolean getError = false;

    public static String lastErrorMessage = "";

    public static final boolean freeVersion = true;

    public static int threeDtotemAmount = 0;

    public static List<Block> getCheckingBlock = Collections.synchronizedList(new ArrayList<>());

    public static List<Player> getCheckingPlayer = new ArrayList<>();

    // 图腾ID，图腾信息
    public static Map<String, TotemManager> getTotemMap = new HashMap<>();

    // 方块ID，方块所在图腾信息
    public static Map<String, List<PlacedBlockCheckManager>> getTotemMaterial = new ConcurrentHashMap<>();

    public static List<Item> getDroppedItems = new ArrayList<>();

    public static int majorVersion;

    public static int miniorVersion;

    @Override
    public void onEnable() {
        instance = this;
        try {
            String[] versionParts = Bukkit.getBukkitVersion().split("-")[0].split("\\.");
            majorVersion = versionParts.length > 1 ? Integer.parseInt(versionParts[1]) : 0;
            miniorVersion = versionParts.length > 2 ? Integer.parseInt(versionParts[2]) : 0;
        } catch (Throwable throwable) {
            Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[MythicChanger] §cError: Can not get your Minecraft version! Default set to 1.0.0.");
        }
        Init();
        ProtectionLib.init(this);
        this.saveDefaultConfig();
        TotemConfigs.initTotemConfigs();
        Messages.initLanguage();
        Events();
        Commands();
        if (MythicTotem.instance.getConfig().getBoolean("debug", false)) {
            Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[MythicTotem] §4Loaded material map: " + getTotemMaterial);
        }
        SavedItemManager.ReadSavedItems();
        Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[MythicTotem] §fYour Minecraft version is: 1." + majorVersion + "." + miniorVersion + "!");
        Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[MythicTotem] §fPlugin is loaded. Author: PQguanfang.");
    }

    @Override
    public void onDisable() {
        Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[MythicTotem] §fPlugin is disabled. Author: PQguanfang.");
    }

    public void Init() {
        InitManager manager = new InitManager();
        manager.init();
    }

    public void Events() {
        if (GeneralSettingConfigs.GetBlockPlaceEventEnabled()) {
            Bukkit.getPluginManager().registerEvents(new PlayerPlaceListener(), this);
            Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[MythicTotem] §fEnabled BlockPlaceEvent trigger.");
        }
        if (GeneralSettingConfigs.GetPlayerInteractEventEnabled()) {
            Bukkit.getPluginManager().registerEvents(new PlayerClickListener(), this);
            Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[MythicTotem] §fEnabled PlayerInteractEvent trigger.");
        }
        if (GeneralSettingConfigs.GetBlockRedstoneEventEnabled()) {
            Bukkit.getPluginManager().registerEvents(new TotemRedstoneListener(), this);
            Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[MythicTotem] §fEnabled BlockRedstoneEvent trigger.");
        }
        if (GeneralSettingConfigs.GetPlayerDropEventEnabled()) {
            Bukkit.getPluginManager().registerEvents(new PlayerDropListener(), this);
            Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[MythicTotem] §fEnabled PlayerDropItemEvent trigger.");
        }
        if (GeneralSettingConfigs.GetBlockPistonEventEnabled() && !MythicTotem.freeVersion) {
            Bukkit.getPluginManager().registerEvents(new TotemPistonListener(), this);
            Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[MythicTotem] §fEnabled BlockPistonEvent trigger.");
        }
        if (GeneralSettingConfigs.GetEntityPlaceEventEnabled()) {
            Bukkit.getPluginManager().registerEvents(new EntityPlaceListener(), this);
            Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[MythicTotem] §fEnabled EntityPlaceEvent trigger.");
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
