package cn.superiormc.mythictotem;

import cn.superiormc.mythictotem.managers.*;
import cn.superiormc.mythictotem.utils.CommonUtil;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class MythicTotem extends JavaPlugin {

    public static JavaPlugin instance;

    public static final boolean freeVersion = true;

    public static boolean isPaper = false;

    public static int majorVersion;

    public static int minorVersion;

    public static boolean newSkullMethod;

    @Override
    public void onEnable() {
        instance = this;
        try {
            String[] versionParts = Bukkit.getBukkitVersion().split("-")[0].split("\\.");
            majorVersion = versionParts.length > 1 ? Integer.parseInt(versionParts[1]) : 0;
            minorVersion = versionParts.length > 2 ? Integer.parseInt(versionParts[2]) : 0;
        } catch (Throwable throwable) {
            Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[MythicTotem] §cError: Can not get your Minecraft version! Default set to 1.0.0.");
        }
        new ErrorManager();
        new InitManager();
        new ActionManager();
        new ConditionManager();
        new ConfigManager();
        new HookManager();
        if (CommonUtil.getClass("com.destroystokyo.paper.PaperConfig")) {
            Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[MythicTotem] §fPaper is found, enabled Paper only feature!");
            isPaper = true;
        }
        new LanguageManager();
        new ListenerManager();
        new CommandManager();
        new ItemManager();
        if (!CommonUtil.checkClass("com.mojang.authlib.properties.Property", "getValue") && CommonUtil.getMinorVersion(21, 1)) {
            newSkullMethod = true;
            Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[MythicTotem] §fNew AuthLib found, enabled new skull get method!");
        }
        Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[MythicTotem] §fYour Minecraft version is: 1." + majorVersion + "." + minorVersion + "!");
        Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[MythicTotem] §fPlugin is loaded. Author: PQguanfang.");
    }

    @Override
    public void onDisable() {
        Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[MythicTotem] §fPlugin is disabled. Author: PQguanfang.");
    }

}
