package cn.superiormc.mythictotem.paper.adapts.v26_3;

import cn.superiormc.mythictotem.paper.adapts.ComponentCodec;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.VillagerFood;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class VillagerFoodCodec implements ComponentCodec {

    @Override
    public String getIdentifier() {
        return "villager-food";
    }

    @Override
    public void apply(ItemStack item, Player player, ConfigurationSection section, String... args) {
        if (section.contains("villager-food")) {
            item.setData(DataComponentTypes.VILLAGER_FOOD, VillagerFood
                    .villagerFood(section.getInt("villager-food")));
        }
    }

    @Override
    public void write(ItemStack item, ConfigurationSection section) {
        if (item.isDataOverridden(DataComponentTypes.VILLAGER_FOOD)) {
            VillagerFood food = item.getData(DataComponentTypes.VILLAGER_FOOD);
            if (food != null) {
                section.set("villager-food", food.nutrition());
            }
        }
    }
}
