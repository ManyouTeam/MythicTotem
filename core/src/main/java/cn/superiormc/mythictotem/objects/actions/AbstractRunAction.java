package cn.superiormc.mythictotem.objects.actions;


import cn.superiormc.mythictotem.managers.ErrorManager;
import cn.superiormc.mythictotem.objects.checks.ObjectCheck;
import cn.superiormc.mythictotem.objects.checks.ObjectPlaceCheck;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public abstract class AbstractRunAction {

    private final String type;

    private String[] requiredArgs;

    private boolean requirePlayer = true;

    public AbstractRunAction(String type) {
        this.type = type;
    }

    protected void setRequiredArgs(String... requiredArgs) {
        this.requiredArgs = requiredArgs;
    }

    protected void setRequirePlayer(boolean b) {
        this.requirePlayer = b;
    }

    public void runAction(ObjectSingleAction singleAction, Player player, Location startLocation, ObjectCheck check, ObjectPlaceCheck totem) {
        if (player == null && requirePlayer) {
            return;
        }
        if (requiredArgs != null) {
            for (String arg : requiredArgs) {
                if (!singleAction.getSection().contains(arg)) {
                    ErrorManager.errorManager.sendErrorMessage("§cError: Your action missing required arg: " + arg + ".");
                    return;
                }
            }
        }
        onDoAction(singleAction, player, startLocation, check, totem);
    }

    protected abstract void onDoAction(ObjectSingleAction singleAction, Player player, Location startLocation, ObjectCheck check, ObjectPlaceCheck totem);

    public String getType() {
        return type;
    }
}
