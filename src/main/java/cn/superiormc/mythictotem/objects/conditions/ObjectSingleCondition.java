package cn.superiormc.mythictotem.objects.conditions;

import cn.superiormc.mythictotem.managers.ConditionManager;
import cn.superiormc.mythictotem.objects.AbstractSingleRun;
import cn.superiormc.mythictotem.objects.ObjectCondition;
import cn.superiormc.mythictotem.objects.checks.ObjectCheck;
import cn.superiormc.mythictotem.objects.checks.ObjectPlaceCheck;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public class ObjectSingleCondition extends AbstractSingleRun {

    private final ObjectCondition condition;

    public ObjectSingleCondition(ObjectCondition condition, ConfigurationSection conditionSection) {
        super(conditionSection);
        this.condition = condition;
    }

    public boolean checkBoolean(Player player, Location startLocation, ObjectCheck check, ObjectPlaceCheck totem) {
        if (check.getItem() == null) {
            return false;
        }
        return ConditionManager.conditionManager.checkBoolean(this, player, startLocation, check, totem);
    }

    public ObjectCondition getCondition() {
        return condition;
    }

}
