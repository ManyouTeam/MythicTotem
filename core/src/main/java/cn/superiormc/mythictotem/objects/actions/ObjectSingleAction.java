package cn.superiormc.mythictotem.objects.actions;

import cn.superiormc.mythictotem.managers.ActionManager;
import cn.superiormc.mythictotem.objects.AbstractSingleRun;
import cn.superiormc.mythictotem.objects.ObjectAction;
import cn.superiormc.mythictotem.objects.checks.ObjectCheck;
import cn.superiormc.mythictotem.objects.checks.ObjectPlaceCheck;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public class ObjectSingleAction extends AbstractSingleRun {

    private final ObjectAction action;


    public ObjectSingleAction(ObjectAction action, ConfigurationSection actionSection) {
        super(actionSection);
        this.action = action;
    }

    public void doAction(Player player, Location startLocation, ObjectCheck check, ObjectPlaceCheck totem) {
        ActionManager.actionManager.doAction(this, player, startLocation, check, totem);
    }


    public ObjectAction getAction() {
        return action;
    }

}
