package cn.superiormc.mythictotem.paper.adapts.v1_21_5;

import cn.superiormc.mythictotem.paper.adapts.ComponentCodec;
import io.papermc.paper.datacomponent.DataComponentTypes;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class RepairCostCodec implements ComponentCodec {

    @Override
    public String getIdentifier() {
        return "repair-cost";
    }

    @Override
    public void apply(ItemStack item, Player player, ConfigurationSection section, String... args) {
        int repairCost = section.getInt("repair-cost", -1);
        if (repairCost >= 0) {
            item.setData(DataComponentTypes.REPAIR_COST, repairCost);
        }
    }

    @Override
    public void write(ItemStack item, ConfigurationSection section) {
        if (item.isDataOverridden(DataComponentTypes.REPAIR_COST)) {
            Integer repairCost = item.getData(DataComponentTypes.REPAIR_COST);
            section.set("repair-cost", repairCost);
        }
    }

}
