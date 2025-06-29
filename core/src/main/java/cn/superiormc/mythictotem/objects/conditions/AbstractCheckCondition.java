package cn.superiormc.mythictotem.objects.conditions;

import cn.superiormc.mythictotem.managers.ErrorManager;
import cn.superiormc.mythictotem.objects.checks.ObjectCheck;
import cn.superiormc.mythictotem.objects.checks.ObjectPlaceCheck;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public abstract class AbstractCheckCondition {

    private final String type;

    private String[] requiredArgs;

    private boolean requirePlayer = true;

    public AbstractCheckCondition(String type) {
        this.type = type;
    }

    protected void setRequiredArgs(String... requiredArgs) {
        this.requiredArgs = requiredArgs;
    }

    protected void setRequirePlayer(boolean b) {
        this.requirePlayer = b;
    }

    public boolean checkCondition(ObjectSingleCondition singleCondition, Player player, Location startLocation, ObjectCheck check, ObjectPlaceCheck totem) {
        if (player == null && requirePlayer) {
            return false;
        }
        if (requiredArgs != null) {
            for (String arg : requiredArgs) {
                if (!singleCondition.getSection().contains(arg)) {
                    ErrorManager.errorManager.sendErrorMessage("§cError: Your condition missing required arg: " + arg + ".");
                    return true;
                }
            }
        }
        return onCheckCondition(singleCondition, player, startLocation, check, totem);
    }

    protected abstract boolean onCheckCondition(ObjectSingleCondition singleCondition, Player player, Location startLocation, ObjectCheck check, ObjectPlaceCheck totem);

    public String getType() {
        return type;
    }
}
