package cn.superiormc.mythictotem.objects.actions;

import cn.superiormc.mythictotem.MythicTotem;
import cn.superiormc.mythictotem.objects.ObjectAction;
import cn.superiormc.mythictotem.objects.checks.ObjectCheck;
import cn.superiormc.mythictotem.objects.checks.ObjectPlaceCheck;
import cn.superiormc.mythictotem.utils.SchedulerUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public class ActionDelay extends AbstractRunAction {

    public ActionDelay() {
        super("delay");
        setRequiredArgs("time", "actions");
        setRequirePlayer(false);
    }

    @Override
    protected void onDoAction(ObjectSingleAction singleAction, Player player, Location startLocation, ObjectCheck check, ObjectPlaceCheck totem) {
        if (MythicTotem.freeVersion) {
            return;
        }
        ConfigurationSection chanceSection = singleAction.getSection().getConfigurationSection("actions");
        if (chanceSection == null) {
            return;
        }
        long time = singleAction.getSection().getLong("time");
        ObjectAction action = new ObjectAction(chanceSection);
        SchedulerUtil.runTaskLater(() -> action.runAllActions(player, startLocation, check, totem), time);
    }
}
