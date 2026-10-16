package cn.superiormc.mythictotem.paper.adapts.v1_21_5;

import cn.superiormc.mythictotem.methods.DebuildItem;
import cn.superiormc.mythictotem.paper.adapts.ComponentCodec;
import cn.superiormc.mythictotem.paper.methods.BuildItemPaper;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.UseRemainder;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class UseRemainderCodec implements ComponentCodec {

    @Override
    public String getIdentifier() {
        return "use-reminder";
    }

    @Override
    public void apply(ItemStack item, Player player, ConfigurationSection section, String... args) {
        ConfigurationSection useReminderKey = section.getConfigurationSection("use-reminder");
        if (useReminderKey != null) {
            item.setData(DataComponentTypes.USE_REMAINDER, UseRemainder.useRemainder(BuildItemPaper.buildItemStack(player, useReminderKey, useReminderKey.getInt("amount", 1), args)));
        }
    }

    @Override
    public void write(ItemStack item, ConfigurationSection section) {
        if (item.isDataOverridden(DataComponentTypes.USE_REMAINDER)) {
            UseRemainder reminder = item.getData(DataComponentTypes.USE_REMAINDER);
            if (reminder != null) {
                ConfigurationSection reminderSection = section.createSection("use-reminder");
                DebuildItem.debuildItem(reminder.transformInto(), reminderSection);
            }
        }
    }

}
