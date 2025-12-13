package cn.superiormc.mythictotem.objects.conditions;

import cn.superiormc.mythictotem.managers.ErrorManager;
import cn.superiormc.mythictotem.objects.checks.ObjectCheck;
import cn.superiormc.mythictotem.objects.checks.ObjectPlaceCheck;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class ConditionPlaceholder extends AbstractCheckCondition {

    public ConditionPlaceholder() {
        super("placeholder");
        setRequiredArgs("placeholder", "rule", "value");
    }

    @Override
    protected boolean onCheckCondition(ObjectSingleCondition singleCondition, Player player, Location startLocation, ObjectCheck check, ObjectPlaceCheck totem) {
        String placeholder = singleCondition.getString("placeholder", player, startLocation, check, totem);
        String value = singleCondition.getString("value", player, startLocation, check, totem);
        try {
            switch (singleCondition.getString("rule")) {
                case ">=":
                    return Double.parseDouble(placeholder) >= Double.parseDouble(value);
                case ">":
                    return Double.parseDouble(placeholder) > Double.parseDouble(value);
                case "=":
                    return Double.parseDouble(placeholder) == Double.parseDouble(value);
                case "<":
                    return Double.parseDouble(placeholder) < Double.parseDouble(value);
                case "<=":
                    return Double.parseDouble(placeholder) <= Double.parseDouble(value);
                case "==":
                    return placeholder.equals(value);
                case "!=":
                    return !placeholder.equals(value);
                case "*=":
                    return placeholder.contains(value);
                case "=*":
                    return value.contains(placeholder);
                case "!*=":
                    return !placeholder.contains(value);
                case "!=*":
                    return !value.contains(placeholder);
                default:
                    ErrorManager.errorManager.sendErrorMessage("§cError: Your placeholder condition can not being correctly load.");
                    return true;
            }
        } catch (Throwable throwable) {
            ErrorManager.errorManager.sendErrorMessage("§cError: Your placeholder condition can not being correctly load.");
            return true;
        }
    }
}
