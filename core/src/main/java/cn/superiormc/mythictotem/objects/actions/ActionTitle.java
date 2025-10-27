package cn.superiormc.mythictotem.objects.actions;

import cn.superiormc.mythictotem.MythicTotem;
import cn.superiormc.mythictotem.objects.checks.ObjectCheck;
import cn.superiormc.mythictotem.objects.checks.ObjectPlaceCheck;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class ActionTitle extends AbstractRunAction {

    public ActionTitle() {
        super("title");
        setRequiredArgs("main-title", "sub-title", "fade-in", "stay", "fade-out");
    }

    @Override
    protected void onDoAction(ObjectSingleAction singleAction, Player player, Location startLocation, ObjectCheck check, ObjectPlaceCheck totem) {
        MythicTotem.methodUtil.sendTitle(player,
                singleAction.getString("main-title", player, startLocation, check, totem),
                singleAction.getString("sub-title", player, startLocation, check, totem),
                singleAction.getInt("fade-in"),
                singleAction.getInt("stay"),
                singleAction.getInt("fade-out"));
    }
}
