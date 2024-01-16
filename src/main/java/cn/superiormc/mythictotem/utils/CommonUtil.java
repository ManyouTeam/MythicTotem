package cn.superiormc.mythictotem.utils;

import cn.superiormc.mythictotem.MythicTotem;
import cn.superiormc.mythictotem.configs.GeneralSettingConfigs;
import dev.lone.itemsadder.api.CustomBlock;
import io.lumine.mythic.api.mobs.MythicMob;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.xikage.mythicmobs.MythicMobs;
import io.th0rgal.protectionlib.ProtectionLib;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageEvent;

import java.io.File;
import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommonUtil {

    public static boolean checkPluginLoad(String pluginName){
        return MythicTotem.instance.getServer().getPluginManager().isPluginEnabled(pluginName);
    }

    public static int getMajorVersion() {
        String version = Bukkit.getVersion();
        Matcher matcher = Pattern.compile("MC: \\d\\.(\\d+)").matcher(version);
        if (matcher.find()) {
            return Integer.parseInt(matcher.group(1));
        }
        return 20;
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


    public static void summonMythicMobs(Location location, String mobID, int level) {
        try {
            MythicMob mob = MythicBukkit.inst().getMobManager().getMythicMob(mobID).orElse(null);
            if (mob != null) {
                mob.spawn(BukkitAdapter.adapt(location), level);
            }
        }
        catch (NoClassDefFoundError ep) {
            io.lumine.xikage.mythicmobs.mobs.MythicMob mob = MythicMobs.inst().getMobManager().getMythicMob(mobID);
            if (mob != null) {
                mob.spawn(io.lumine.xikage.mythicmobs.adapters.bukkit.BukkitAdapter.adapt(location), level);
            }
        }
    }

    public static void removeBlock(Player player, Location loc){
        loc.getBlock().setType(Material.AIR);
        if (CommonUtil.checkPluginLoad("ItemsAdder")) {
            CustomBlock.remove(loc);
        }
        if (GeneralSettingConfigs.GetBlockBreakEventCancel() && !loc.getBlock().getType().isAir()) {
            BlockBreakEvent bbe = new BlockBreakEvent(loc.getBlock(), player);
            bbe.setDropItems(false);
            bbe.setExpToDrop(0);
            Bukkit.getPluginManager().callEvent(bbe);
        }
        if (GeneralSettingConfigs.GetBlockDamageEventCancel() && !loc.getBlock().getType().isAir()) {
            BlockDamageEvent bde = new BlockDamageEvent(player, loc.getBlock(), null, true);
            Bukkit.getPluginManager().callEvent(bde);
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
        if (MythicTotem.instance.getConfig().getBoolean("check-protection.can-build") &&
                !ProtectionLib.canBuild(player, loc)) {
            i ++;
        }
        if (MythicTotem.instance.getConfig().getBoolean("check-protection.can-break") &&
                !ProtectionLib.canBreak(player, loc)) {
            i ++;
        }
        if (MythicTotem.instance.getConfig().getBoolean("check-protection.can-interact") &&
                !ProtectionLib.canInteract(player, loc)) {
            i ++;
        }
        if (MythicTotem.instance.getConfig().getBoolean("check-protection.can-use") &&
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
}
