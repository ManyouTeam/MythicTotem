package cn.superiormc.mythictotem.objects.conditions;


import cn.superiormc.mythictotem.objects.checks.ObjectCheck;
import cn.superiormc.mythictotem.objects.checks.ObjectPlaceCheck;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class ConditionPermission extends AbstractCheckCondition {

    public ConditionPermission() {
        super("permission");
        setRequiredArgs("permission");
    }

    @Override
    protected boolean onCheckCondition(ObjectSingleCondition singleCondition, Player player, Location startLocation, ObjectCheck check, ObjectPlaceCheck totem) {
        return player.hasPermission(singleCondition.getString("permission"));
    }
}
