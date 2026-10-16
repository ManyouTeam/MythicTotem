package cn.superiormc.mythictotem.paper.adapts.v26_3;

import cn.superiormc.mythictotem.paper.adapts.ComponentCodec;
import io.papermc.paper.datacomponent.DataComponentTypes;
import java.util.Locale;
import org.bukkit.DyeColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class CushionColorCodec implements ComponentCodec {

    @Override
    public String getIdentifier() {
        return "cushion-color";
    }

    @Override
    public void apply(ItemStack item, Player player, ConfigurationSection section, String... args) {
        String color = section.getString("cushion-color");
        if (color != null) {
            try {
                item.setData(DataComponentTypes.CUSHION_COLOR, DyeColor.valueOf(color.toUpperCase(Locale.ENGLISH)));
            } catch (IllegalArgumentException ignored) {
            }
        }
    }

    @Override
    public void write(ItemStack item, ConfigurationSection section) {
        if (item.isDataOverridden(DataComponentTypes.CUSHION_COLOR)) {
            DyeColor color = item.getData(DataComponentTypes.CUSHION_COLOR);
            if (color != null) {
                section.set("cushion-color", color.name());
            }
        }
    }
}
