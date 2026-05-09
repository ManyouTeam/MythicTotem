package cn.superiormc.mythictotem.objects.conditions;

import cn.superiormc.mythictotem.objects.singlethings.AbstractThingData;
import cn.superiormc.mythictotem.objects.singlethings.TotemActiveData;
import org.bukkit.entity.Player;

public class ConditionTrigger extends AbstractCheckCondition {

    public ConditionTrigger() {
        super("trigger");
        setRequiredArgs("event");
        setRequirePlayer(false);
    }

    @Override
    protected boolean onCheckCondition(ObjectSingleCondition singleCondition, Player player, AbstractThingData thingData) {
        if (thingData instanceof TotemActiveData totemActiveData) {
            return totemActiveData.check.getEvent().equals(singleCondition.getString("event"));
        }
        return false;
    }
}
