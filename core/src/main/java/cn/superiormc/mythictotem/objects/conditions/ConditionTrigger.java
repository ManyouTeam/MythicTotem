package cn.superiormc.mythictotem.objects.conditions;

import cn.superiormc.mythictotem.objects.checks.ObjectCheck;
import cn.superiormc.mythictotem.objects.checks.ObjectPlaceCheck;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class ConditionTrigger extends AbstractCheckCondition {

    public ConditionTrigger() {
        super("trigger");
        setRequiredArgs("event");
        setRequirePlayer(false);
    }

    @Override
    protected boolean onCheckCondition(ObjectSingleCondition singleCondition, Player player, Location startLocation, ObjectCheck check, ObjectPlaceCheck totem) {
        return check.getEvent().equals(singleCondition.getString("event"));
    }
}
