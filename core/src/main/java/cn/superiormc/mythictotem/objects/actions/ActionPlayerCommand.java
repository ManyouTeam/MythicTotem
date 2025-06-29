package cn.superiormc.mythictotem.objects.actions;

import cn.superiormc.mythictotem.MythicTotem;
import cn.superiormc.mythictotem.objects.checks.ObjectCheck;
import cn.superiormc.mythictotem.objects.checks.ObjectPlaceCheck;
import cn.superiormc.mythictotem.utils.CommonUtil;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class ActionPlayerCommand extends AbstractRunAction {

    public ActionPlayerCommand() {
        super("player_command");
        setRequiredArgs("command");
    }

    @Override
    protected void onDoAction(ObjectSingleAction singleAction, Player player, Location startLocation, ObjectCheck check, ObjectPlaceCheck totem) {
        MythicTotem.methodUtil.dispatchCommand(player, singleAction.getString("command", player, startLocation, check, totem));
    }
}
