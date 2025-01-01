package cn.superiormc.mythictotem.utils;

import cn.superiormc.mythictotem.MythicTotem;
import cn.superiormc.mythictotem.managers.ConfigManager;
import cn.superiormc.mythictotem.managers.ErrorManager;
import dev.lone.itemsadder.api.CustomBlock;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.xikage.mythicmobs.MythicMobs;
import io.th0rgal.protectionlib.ProtectionLib;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.ExecutionException;

public class CommonUtil {

    public static String modifyString(String text, String... args) {
        for (int i = 0 ; i < args.length ; i += 2) {
            String var1 = "{" + args[i] + "}";
            String var2 = "%" + args[i] + "%";
            if (args[i + 1] == null) {
                text = text.replace(var1, "").replace(var2, "");
            }
            else {
                text = text.replace(var1, args[i + 1]).replace(var2, args[i + 1]);
            }
        }
        return text;
    }

    public static boolean checkPluginLoad(String pluginName){
        return MythicTotem.instance.getServer().getPluginManager().isPluginEnabled(pluginName);
    }

    public static boolean getClass(String className) {
        try {
            Class.forName(className);
            return true;
        }
        catch (ClassNotFoundException e) {
            return false;
        }
    }

    public static boolean getMajorVersion(int version) {
        return MythicTotem.majorVersion >= version;
    }

    public static boolean getMinorVersion(int majorVersion, int minorVersion) {
        return MythicTotem.majorVersion > majorVersion || (MythicTotem.majorVersion == majorVersion &&
                MythicTotem.minorVersion >= minorVersion);
    }

    public static void giveOrDrop(Player player, ItemStack... item) {
        HashMap<Integer, ItemStack> result = player.getInventory().addItem(item);
        if (!result.isEmpty()) {
            for (int id : result.keySet()) {
                player.getWorld().dropItem(player.getLocation(), result.get(id));
            }
        }
    }

    public static void dispatchCommand(String command){
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
    }

    public static void dispatchCommand(Player player, String command){
        Bukkit.dispatchCommand(player, command);
    }

    public static void dispatchOpCommand(Player player, String command) {
        boolean playerIsOp = player.isOp();
        try {
            player.setOp(true);
            Bukkit.dispatchCommand(player, command);
        } finally {
            player.setOp(playerIsOp);
        }
    }

    public static Collection<Entity> getNearbyEntity(Location location, double distance) {
        Collection<Entity> tempVal2;
        try {
            tempVal2 = Bukkit.getScheduler().callSyncMethod(MythicTotem.instance, () -> location.getWorld().getNearbyEntities(location, distance,
                    distance, distance)).get();
        } catch (InterruptedException | ExecutionException e) {
            ErrorManager.errorManager.sendErrorMessage("§x§9§8§F§B§9§8[MythicTotem] §cError: There is something wrong when get nearby entities.");
            return new HashSet<>();
        }
        return tempVal2;
    }

    public static void summonMythicMobs(Location location, String mobID, int level) {
        try {
            MythicBukkit.inst().getMobManager().getMythicMob(mobID).ifPresent(mob -> mob.spawn(BukkitAdapter.adapt(location), level));
        }
        catch (NoClassDefFoundError ep) {
            io.lumine.xikage.mythicmobs.mobs.MythicMob mob = MythicMobs.inst().getMobManager().getMythicMob(mobID);
            if (mob != null) {
                mob.spawn(io.lumine.xikage.mythicmobs.adapters.bukkit.BukkitAdapter.adapt(location), level);
            }
        }
    }

    public static void removeBlock(Block block) {
        if (block == null) {
            return;
        } else if (CommonUtil.checkPluginLoad("ItemsAdder") && CustomBlock.byAlreadyPlaced(block) != null) {
            CustomBlock.remove(block.getLocation());
        } else {
            block.setType(Material.AIR);
        }
    }

    public static boolean checkProtection(Player player, Location loc) {
        if (player == null) {
            return true;
        }
        if (loc == null) {
            return true;
        }
        int i = 0;
        if (ConfigManager.configManager.getBoolean("check-protection.can-build", false) &&
                !ProtectionLib.canBuild(player, loc)) {
            i ++;
        }
        if (ConfigManager.configManager.getBoolean("check-protection.can-break", false) &&
                !ProtectionLib.canBreak(player, loc)) {
            i ++;
        }
        if (ConfigManager.configManager.getBoolean("check-protection.can-interact", false) &&
                !ProtectionLib.canInteract(player, loc)) {
            i ++;
        }
        if (ConfigManager.configManager.getBoolean("check-protection.can-use", false) &&
                !ProtectionLib.canUse(player, loc)) {
            i ++;
        }
        return i == 0;
    }

    public static void mkDir(File dir) {
        if (!dir.exists()) {
            File parentFile = dir.getParentFile();
            if (parentFile == null) {
                return;
            }
            String parentPath = parentFile.getPath();
            mkDir(new File(parentPath));
            dir.mkdir();
        }
    }

    public static NamespacedKey parseNamespacedKey(String key) {
        String[] keySplit = key.split(":");
        if (keySplit.length == 1) {
            return NamespacedKey.minecraft(key.toLowerCase());
        }
        return NamespacedKey.fromString(key);
    }

    public static Color parseColor(String color) {
        String[] keySplit = color.replace(" ", "").split(",");
        if (keySplit.length == 3) {
            return Color.fromRGB(Integer.parseInt(keySplit[0]), Integer.parseInt(keySplit[1]), Integer.parseInt(keySplit[2]));
        }
        return Color.fromRGB(Integer.parseInt(color));
    }

    public static boolean checkClass(String className, String methodName) {
        try {
            Class<?> targetClass = Class.forName(className);
            Method[] methods = targetClass.getDeclaredMethods();

            for (Method method : methods) {
                if (method.getName().equals(methodName)) {
                    return true;
                }
            }

            return false;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }
}
