package cn.superiormc.mythictotem.hooks.protection;

import com.bekvon.bukkit.residence.Residence;
import com.bekvon.bukkit.residence.containers.ResidencePlayer;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class ProtectionResidenceHook extends AbstractProtectionHook {

    public ProtectionResidenceHook() {
        super("Residence");
    }

    @Override
    public boolean canUse(Player player, Location location) {
        ResidencePlayer rPlayer = Residence.getInstance().getPlayerManager().getResidencePlayer(player);
        return rPlayer.canBreakBlock(location.getBlock(), true);
    }
}
