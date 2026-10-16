package cn.superiormc.mythictotem.paper.adapts.v26_3;

import cn.superiormc.mythictotem.paper.adapts.ComponentCodec;
import com.google.common.base.Enums;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.SwingAnimation;
import java.util.Locale;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class SwingAnimationCodec implements ComponentCodec {

    @Override
    public String getIdentifier() {
        return "swing-animation";
    }

    @Override
    public void apply(ItemStack item, Player player, ConfigurationSection section, String... args) {
        ConfigurationSection legacy = section.getConfigurationSection("swing-animation");
        if (legacy == null) {
            return;
        }
        SwingAnimation.Builder builder = SwingAnimation.swingAnimation();
        String type = legacy.getString("type");
        if (type != null) {
            builder.type(Enums.getIfPresent(SwingAnimation.Animation.class,
                    type.toUpperCase(Locale.ENGLISH)).or(SwingAnimation.Animation.NONE));
        }
        int duration = legacy.getInt("duration", -1);
        if (duration > 0) {
            builder.duration(duration);
        }
        SwingAnimation animation = builder.build();
        if (!section.isConfigurationSection("attack-animation")) {
            item.setData(DataComponentTypes.ATTACK_ANIMATION, animation);
        }
        if (!section.isConfigurationSection("interact-animation")) {
            item.setData(DataComponentTypes.INTERACT_ANIMATION, animation);
        }
    }

    @Override
    public void write(ItemStack item, ConfigurationSection section) {
    }
}
