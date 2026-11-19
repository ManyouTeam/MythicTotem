package cn.superiormc.mythictotem.objects.actions;

import cn.superiormc.mythictotem.MythicTotem;
import cn.superiormc.mythictotem.objects.singlethings.AbstractThingData;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

public class ActionEntitySpawn extends AbstractRunAction {

    public ActionEntitySpawn() {
        super("entity_spawn");
        setRequiredArgs("entity");
        setRequirePlayer(false);
    }

    @Override
    protected void onDoAction(ObjectSingleAction singleAction, Player player, AbstractThingData thingData) {
        EntityType entity = EntityType.valueOf(singleAction.getString("entity").toUpperCase());
        Location location = singleAction.getSpawnLocation(player, thingData);
        if (location != null) {
            MythicTotem.methodUtil.spawnEntity(location, entity);
        }
    }
}
