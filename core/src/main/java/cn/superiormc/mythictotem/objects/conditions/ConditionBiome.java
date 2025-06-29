package cn.superiormc.mythictotem.objects.conditions;

import cn.superiormc.mythictotem.objects.checks.ObjectCheck;
import cn.superiormc.mythictotem.objects.checks.ObjectPlaceCheck;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class ConditionBiome extends AbstractCheckCondition {

    public ConditionBiome() {
        super("biome");
        setRequiredArgs("biome");
        setRequirePlayer(false);
    }

    @Override
    protected boolean onCheckCondition(ObjectSingleCondition singleCondition, Player player, Location startLocation, ObjectCheck check, ObjectPlaceCheck totem) {
        if (player == null || singleCondition.getBoolean("block-as-trigger", false)) {
            return check.getBlock().getBiome().name().equals(singleCondition.getString("biome").toUpperCase());
        }
        return player.getLocation().getBlock().getBiome().name().equals(singleCondition.getString("biome").toUpperCase());
    }
}
