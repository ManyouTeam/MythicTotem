package cn.superiormc.mythictotem.objects.conditions;

import cn.superiormc.mythictotem.MythicTotem;
import cn.superiormc.mythictotem.objects.checks.ObjectCheck;
import cn.superiormc.mythictotem.objects.checks.ObjectPlaceCheck;
import cn.superiormc.mythictotem.utils.CommonUtil;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.mobs.ActiveMob;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class ConditionMobsNear extends AbstractCheckCondition {

    public ConditionMobsNear() {
        super("mobs_near");
        setRequiredArgs("entity", "distance");
        setRequirePlayer(false);
    }

    @Override
    protected boolean onCheckCondition(ObjectSingleCondition singleCondition, Player player, Location startLocation, ObjectCheck check, ObjectPlaceCheck totem) {
        if (MythicTotem.freeVersion) {
            return true;
        }
        String tempVal1 = singleCondition.getString("entity");
        for (Entity tempVal3 : CommonUtil.getNearbyEntity(check.getBlock().getLocation(), singleCondition.getDouble("distance"))) {
            if (CommonUtil.checkPluginLoad("MythicMobs")) {
                ActiveMob tempVal4 = MythicBukkit.inst().getMobManager().getMythicMobInstance(tempVal3);
                if (tempVal4 != null && tempVal1.equals(tempVal4.getType().getInternalName())) {
                    return true;
                }
            }
            String customName = tempVal3.getCustomName();
            if (customName == null && tempVal1.equalsIgnoreCase(tempVal3.getType().name())) {
                return true;
            } else if (customName != null && customName.contains(tempVal1)) {
                return true;
            }
        }
        return false;
    }
}
