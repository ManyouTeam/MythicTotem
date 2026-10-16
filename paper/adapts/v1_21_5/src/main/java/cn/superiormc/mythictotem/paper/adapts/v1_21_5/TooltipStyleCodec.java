package cn.superiormc.mythictotem.paper.adapts.v1_21_5;

import cn.superiormc.mythictotem.paper.adapts.ComponentCodec;
import cn.superiormc.mythictotem.utils.CommonUtil;
import io.papermc.paper.datacomponent.DataComponentTypes;
import net.kyori.adventure.key.Key;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class TooltipStyleCodec implements ComponentCodec {

    @Override
    public String getIdentifier() {
        return "tooltip-style";
    }

    @Override
    public void apply(ItemStack item, Player player, ConfigurationSection section, String... args) {
        String tooltipStyle = section.getString("tooltip-style", null);
        if (tooltipStyle != null) {
            item.setData(DataComponentTypes.TOOLTIP_STYLE, CommonUtil.parseNamespacedKey(tooltipStyle));
        }
    }

    @Override
    public void write(ItemStack item, ConfigurationSection section) {
        if (item.isDataOverridden(DataComponentTypes.TOOLTIP_STYLE)) {
            Key tooltipKey = item.getData(DataComponentTypes.TOOLTIP_STYLE);
            if (tooltipKey != null) {
                section.set("tooltip-style", tooltipKey.asString());
            }
        }
    }

}
