package cn.superiormc.mythictotem.objects.conditions;

import cn.superiormc.mythictotem.objects.singlethings.BonusTotemData;
import cn.superiormc.mythictotem.objects.singlethings.AbstractThingData;
import cn.superiormc.mythictotem.objects.singlethings.TotemActiveData;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class ConditionWorld extends AbstractCheckCondition {

    public ConditionWorld() {
        super("world");
        setRequiredArgs("world");
        setRequirePlayer(false);
    }

    @Override
    protected boolean onCheckCondition(ObjectSingleCondition singleCondition, Player player, AbstractThingData thingData) {
        Location location = null;
        if (player == null || singleCondition.getBoolean("block-as-trigger", false)) {
            if (thingData instanceof TotemActiveData totemActiveData) {
                location = totemActiveData.check.getBlock().getLocation();
            } else if (thingData instanceof BonusTotemData bonusTotemData) {
                location = bonusTotemData.location;
            }
        }
        if (location != null) {
            if (player == null || singleCondition.getBoolean("block-as-trigger", false)) {
                return location.getWorld().getName().equals(singleCondition.getString("world"));
            }
            return false;
        }
        return player.getWorld().getName().equals(singleCondition.getString("world", player, thingData));
    }
}
