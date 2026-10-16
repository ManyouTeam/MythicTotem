package cn.superiormc.mythictotem.paper.adapts.v1_21_11;

import cn.superiormc.mythictotem.paper.adapts.ComponentCodec;
import io.papermc.paper.datacomponent.DataComponentTypes;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class MinimumAttackChargeCodec implements ComponentCodec {

    @Override
    public String getIdentifier() {
        return "minimum-attack-charge";
    }

    @Override
    public void apply(ItemStack item, Player player, ConfigurationSection section, String... args) {
        double minimumAttackChargeKey = section.getDouble("minimum-attack-charge", -1);
        if (minimumAttackChargeKey >= 0 && minimumAttackChargeKey <= 1) {
            item.setData(DataComponentTypes.MINIMUM_ATTACK_CHARGE, (float) minimumAttackChargeKey);
        }
    }

    @Override
    public void write(ItemStack item, ConfigurationSection section) {
        if (item.isDataOverridden(DataComponentTypes.MINIMUM_ATTACK_CHARGE)) {
            Float minCharge = item.getData(DataComponentTypes.MINIMUM_ATTACK_CHARGE);
            if (minCharge != null) {
                section.set("minimum-attack-charge", minCharge);
            }
        }
    }

}
