package cn.superiormc.mythictotem.objects.actions;

import cn.superiormc.mythictotem.objects.checks.ObjectCheck;
import cn.superiormc.mythictotem.objects.checks.ObjectPlaceCheck;
import cn.superiormc.mythictotem.utils.CommonUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class ActionMythicMobsSpawn extends AbstractRunAction {

    public ActionMythicMobsSpawn() {
        super("mythicmobs_spawn");
        setRequiredArgs("entity");
    }

    @Override
    protected void onDoAction(ObjectSingleAction singleAction, Player player, Location startLocation, ObjectCheck check, ObjectPlaceCheck totem) {
        String mobName = singleAction.getString("entity");
        String worldName = singleAction.getString("world");
        Location location;
        if (player == null || singleAction.getBoolean("block-as-trigger", false)) {
            location = check.getBlock().getLocation();
        } else if (worldName == null) {
            location = player.getLocation();
        } else {
            World world = Bukkit.getWorld(worldName);
            location = new Location(world,
                    singleAction.getDouble("x"),
                    singleAction.getDouble("y"),
                    singleAction.getDouble("z"));

        }
        CommonUtil.summonMythicMobs(location, mobName, singleAction.getInt("level", 1));
    }
}
