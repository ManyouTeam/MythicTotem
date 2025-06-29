package cn.superiormc.mythictotem.objects.actions;

import cn.superiormc.mythictotem.MythicTotem;
import cn.superiormc.mythictotem.objects.checks.ObjectCheck;
import cn.superiormc.mythictotem.objects.checks.ObjectPlaceCheck;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class ActionTeleport extends AbstractRunAction {

    public ActionTeleport() {
        super("teleport");
        setRequiredArgs("world", "x", "y", "z");
    }

    @Override
    protected void onDoAction(ObjectSingleAction singleAction, Player player, Location startLocation, ObjectCheck check, ObjectPlaceCheck totem) {
        Location loc = new Location(Bukkit.getWorld(singleAction.getString("world")),
                    singleAction.getDouble("x"),
                    singleAction.getDouble("y"),
                    singleAction.getDouble("z"),
                    singleAction.getInt("yaw", (int) player.getLocation().getYaw()),
                    singleAction.getInt("pitch", (int) player.getLocation().getPitch()));
        MythicTotem.methodUtil.playerTeleport(player, loc);
    }
}
