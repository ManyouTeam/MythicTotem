package cn.superiormc.mythictotem.paper.adapts.v1_21_5;

import cn.superiormc.mythictotem.paper.adapts.ComponentCodec;
import cn.superiormc.mythictotem.paper.utils.PaperTextUtil;
import cn.superiormc.mythictotem.utils.CommonUtil;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.ItemLore;
import java.util.List;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class LoreCodec implements ComponentCodec {

    @Override
    public String getIdentifier() {
        return "lore";
    }

    @Override
    public void apply(ItemStack item, Player player, ConfigurationSection section, String... args) {
        List<String> lores = section.getStringList("lore");
        if (!lores.isEmpty()) {
            ItemLore.Builder builder = ItemLore.lore();
            for (String lore : lores) {
                lore = CommonUtil.modifyString(player, lore, args);
                for (String singleLore : lore.split("\\\\n")) {
                    builder.addLine(PaperTextUtil.modernParse(singleLore, player));
                }
            }
            item.setData(DataComponentTypes.LORE, builder.build());
        }
    }

    @Override
    public void write(ItemStack item, ConfigurationSection section) {
        if (item.isDataOverridden(DataComponentTypes.LORE)) {
            ItemLore lore = item.getData(DataComponentTypes.LORE);
            if (lore != null) {
                List<String> lores = lore.lines().stream()
                        .map(PaperTextUtil::changeToString)
                        .toList();
                section.set("lore", lores);
            }
        }
    }

}
