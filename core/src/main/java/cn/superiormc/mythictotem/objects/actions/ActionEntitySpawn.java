package cn.superiormc.mythictotem.objects.actions;

import cn.superiormc.mythictotem.MythicTotem;
import cn.superiormc.mythictotem.objects.checks.ObjectCheck;
import cn.superiormc.mythictotem.objects.checks.ObjectPlaceCheck;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

public class ActionEntitySpawn extends AbstractRunAction {

    public ActionEntitySpawn() {
        super("entity_spawn");
        setRequiredArgs("entity");
        setRequirePlayer(false);
    }

    @Override
    protected void onDoAction(ObjectSingleAction singleAction, Player player, Location startLocation, ObjectCheck check, ObjectPlaceCheck totem) {
        EntityType entity = EntityType.valueOf(singleAction.getString("entity").toUpperCase());
        String worldName = singleAction.getString("world");
        Location location;
        if (player == null || singleAction.getBoolean("block-as-trigger", false)) {
            location = check.getBlock().getLocation();
        } else if (worldName == null) {
            location = player.getLocation();
        } else {
            World world = Bukkit.getWorld(worldName);
            location = new Location(world,
                    singleAction.getDouble("x", player, startLocation, check, totem),
                    singleAction.getDouble("y", player, startLocation, check, totem),
                    singleAction.getDouble("z", player, startLocation, check, totem));

        }
        MythicTotem.methodUtil.spawnEntity(location, entity);
    }
}
