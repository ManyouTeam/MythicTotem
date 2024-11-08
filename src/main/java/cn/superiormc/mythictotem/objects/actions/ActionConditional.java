package cn.superiormc.mythictotem.objects.actions;

import cn.superiormc.mythictotem.MythicTotem;
import cn.superiormc.mythictotem.objects.ObjectAction;
import cn.superiormc.mythictotem.objects.ObjectCondition;
import cn.superiormc.mythictotem.objects.checks.ObjectCheck;
import cn.superiormc.mythictotem.objects.checks.ObjectPlaceCheck;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public class ActionConditional extends AbstractRunAction {

    public ActionConditional() {
        super("conditional");
        setRequiredArgs("actions", "conditions");
    }

    @Override
    protected void onDoAction(ObjectSingleAction singleAction, Player player, Location startLocation, ObjectCheck check, ObjectPlaceCheck totem) {
        if (MythicTotem.freeVersion) {
            return;
        }
        ConfigurationSection conditionSection = singleAction.getSection().getConfigurationSection("actions");
        if (conditionSection == null) {
            return;
        }
        ObjectCondition condition = new ObjectCondition(conditionSection);
        if (!condition.getAllBoolean(player, startLocation, check, totem)) {
            return;
        }
        ConfigurationSection actionSection = singleAction.getSection().getConfigurationSection("actions");
        if (actionSection == null) {
            return;
        }
        ObjectAction action = new ObjectAction(actionSection);
        action.runAllActions(player, startLocation, check, totem);
    }
}
