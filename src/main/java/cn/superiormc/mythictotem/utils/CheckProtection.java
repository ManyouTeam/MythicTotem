package cn.superiormc.mythictotem.utils;

import cn.superiormc.mythictotem.MythicTotem;
import io.th0rgal.protectionlib.ProtectionLib;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class CheckProtection {

    public static boolean DoIt (Player player, Location loc) {
        if (player == null) {
            return true;
        }
        if (loc == null) {
            return true;
        }
        int i = 0;
        if (MythicTotem.instance.getConfig().getBoolean("settings.check-protection.can-build") &&
                !ProtectionLib.canBuild(player, loc)) {
            i ++;
        }
        if (MythicTotem.instance.getConfig().getBoolean("settings.check-protection.can-break") &&
                !ProtectionLib.canBreak(player, loc)) {
            i ++;
        }
        if (MythicTotem.instance.getConfig().getBoolean("settings.check-protection.can-interact") &&
                !ProtectionLib.canInteract(player, loc)) {
            i ++;
        }
        if (MythicTotem.instance.getConfig().getBoolean("settings.check-protection.can-use") &&
                !ProtectionLib.canUse(player, loc)) {
            i ++;
        }
        return i == 0;
    }
}
