package cn.superiormc.mythictotem.objects.conditions;

import cn.superiormc.mythictotem.objects.checks.ObjectCheck;
import cn.superiormc.mythictotem.objects.checks.ObjectPlaceCheck;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class ConditionWorld extends AbstractCheckCondition {

    public ConditionWorld() {
        super("world");
        setRequiredArgs("world");
        setRequirePlayer(false);
    }

    @Override
    protected boolean onCheckCondition(ObjectSingleCondition singleCondition, Player player, Location startLocation, ObjectCheck check, ObjectPlaceCheck totem) {
        if (player == null || singleCondition.getBoolean("block-as-trigger", false)) {
            return check.getBlock().getWorld().getName().equals(singleCondition.getString("world"));
        }
        return player.getWorld().getName().equals(singleCondition.getString("world", player, startLocation, check, totem));
    }
}
