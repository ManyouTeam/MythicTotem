package cn.superiormc.mythictotem.hooks.protection;

import net.william278.husktowns.api.BukkitHuskTownsAPI;
import net.william278.husktowns.libraries.cloplib.operation.OperationType;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class ProtectionHuskTownsHook extends AbstractProtectionHook {

    public BukkitHuskTownsAPI api = BukkitHuskTownsAPI.getInstance();

    public ProtectionHuskTownsHook() {
        super("HuskTowns");
    }

    @Override
    public boolean canUse(Player player, Location location) {
        return api.isOperationAllowed(api.getOnlineUser(player.getUniqueId()), OperationType.BLOCK_BREAK, api.getPosition(location));
    }
}
