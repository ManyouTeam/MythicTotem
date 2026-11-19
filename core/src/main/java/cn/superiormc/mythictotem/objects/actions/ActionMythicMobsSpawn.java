package cn.superiormc.mythictotem.objects.actions;

import cn.superiormc.mythictotem.MythicTotem;
import cn.superiormc.mythictotem.objects.singlethings.AbstractThingData;
import cn.superiormc.mythictotem.utils.CommonUtil;
import cn.superiormc.mythictotem.utils.SchedulerUtil;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class ActionMythicMobsSpawn extends AbstractRunAction {

    public ActionMythicMobsSpawn() {
        super("mythicmobs_spawn");
        setRequiredArgs("entity");
    }

    @Override
    protected void onDoAction(ObjectSingleAction singleAction, Player player, AbstractThingData thingData) {
        String mobName = singleAction.getString("entity");
        Location location = singleAction.getSpawnLocation(player, thingData);
        if (location == null) {
            return;
        }
        int level = singleAction.getInt("level", 1);
        if (MythicTotem.isFolia) {
            SchedulerUtil.runSync(location, () -> CommonUtil.summonMythicMobs(location, mobName, level));
        } else {
            CommonUtil.summonMythicMobs(location, mobName, level);
        }
    }
}
