package cn.superiormc.mythictotem.objects.actions;

import cn.superiormc.mythictotem.MythicTotem;
import cn.superiormc.mythictotem.objects.checks.ObjectCheck;
import cn.superiormc.mythictotem.objects.checks.ObjectPlaceCheck;
import cn.superiormc.mythictotem.utils.CommonUtil;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class ActionConsoleCommand extends AbstractRunAction {

    public ActionConsoleCommand() {
        super("console_command");
        setRequiredArgs("command");
        setRequirePlayer(false);
    }

    @Override
    protected void onDoAction(ObjectSingleAction singleAction, Player player, Location startLocation, ObjectCheck check, ObjectPlaceCheck totem) {
        MythicTotem.methodUtil.dispatchCommand(singleAction.getString("command", player, startLocation, check, totem));
    }
}
