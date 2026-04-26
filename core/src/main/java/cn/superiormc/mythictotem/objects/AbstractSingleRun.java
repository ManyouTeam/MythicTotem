package cn.superiormc.mythictotem.objects;

import cn.superiormc.mythictotem.managers.BonusEffectsManager;
import cn.superiormc.mythictotem.objects.checks.ObjectCheck;
import cn.superiormc.mythictotem.objects.checks.ObjectPlaceCheck;
import cn.superiormc.mythictotem.objects.effect.EffectUtil;
import cn.superiormc.mythictotem.objects.singlethings.AbstractThingData;
import cn.superiormc.mythictotem.objects.singlethings.BonusTotemData;
import cn.superiormc.mythictotem.objects.singlethings.TotemActiveData;
import cn.superiormc.mythictotem.utils.CommonUtil;
import cn.superiormc.mythictotem.utils.MathUtil;
import cn.superiormc.mythictotem.utils.TextUtil;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.List;

public abstract class AbstractSingleRun {

    protected ConfigurationSection section;

    public AbstractSingleRun(ConfigurationSection section) {
        this.section = section;
    }

    protected String replacePlaceholder(String content, Player player, AbstractThingData thingData) {
        if (thingData instanceof TotemActiveData totemActiveData) {
            ObjectPlaceCheck totem = totemActiveData.totem;
            ObjectCheck check = totemActiveData.check;
            Location startLocation = totemActiveData.startLocation;

            int row = totem.getRow();
            int column = totem.getColumn();
            int layer = totem.getLayer();

            content = CommonUtil.modifyString(player, content,
                    "world", check.getBlock().getWorld().getName(),
                    "player_world", player.getWorld().getName(),
                    "player_x", String.valueOf(player.getLocation().getX()),
                    "player_y", String.valueOf(player.getLocation().getY()),
                    "player_z", String.valueOf(player.getLocation().getZ()),
                    "player_pitch", String.valueOf(player.getLocation().getPitch()),
                    "player_yaw", String.valueOf(player.getLocation().getYaw()),
                    "player", player.getName(),
                    "block_x", String.valueOf(check.getBlock().getX()),
                    "block_y", String.valueOf(check.getBlock().getY()),
                    "block_z", String.valueOf(check.getBlock().getZ()),
                    "totem_column", String.valueOf(column),
                    "totem_row", String.valueOf(row),
                    "totem_layer", String.valueOf(layer),
                    "totem_id", totem.getTotem().getTotemID(),
                    "totem_start_x", String.valueOf(startLocation.getX()),
                    "totem_start_y", String.valueOf(startLocation.getY()),
                    "totem_start_z", String.valueOf(startLocation.getZ()),
                    "totem_center_x", String.valueOf(startLocation.getX() + (column - 1) / 2.0),
                    "totem_center_y", String.valueOf(startLocation.getY() + (layer - 1) / 2.0),
                    "totem_center_z", String.valueOf(startLocation.getZ() + (row - 1) / 2.0)
            );
        } else if (thingData instanceof BonusTotemData bonusTotemData) {
            content = CommonUtil.modifyString(player, content,
                    "world", bonusTotemData.location.getWorld().getName(),
                    "player_world", player.getWorld().getName(),
                    "player_x", String.valueOf(player.getLocation().getX()),
                    "player_y", String.valueOf(player.getLocation().getY()),
                    "player_z", String.valueOf(player.getLocation().getZ()),
                    "player_pitch", String.valueOf(player.getLocation().getPitch()),
                    "player_yaw", String.valueOf(player.getLocation().getYaw()),
                    "player", player.getName(),
                    "block_x", String.valueOf(bonusTotemData.location.getX()),
                    "block_y", String.valueOf(bonusTotemData.location.getY()),
                    "block_z", String.valueOf(bonusTotemData.location.getZ()),
                    "totem_id", bonusTotemData.totemId,
                    "bonus_uuid", bonusTotemData.totemUUID.toString(),
                    "bonus_level", String.valueOf(bonusTotemData.getLevel()),
                    "bonus_range", String.format("%.1f", bonusTotemData.getRange()),
                    "bonus_description", bonusTotemData.getDescription(),
                    "bonus_limit", String.valueOf(EffectUtil.getMaxEffectsAmount(player, bonusTotemData)),
                    "bonus_amount", String.valueOf(BonusEffectsManager.manager.getPlayerActivedBonus(player).size()),
                    "next_level", String.valueOf(bonusTotemData.getLevel() + 1),
                    "next_price", bonusTotemData.getUpgradePriceName(player),
                    "next_description", bonusTotemData.getDescription(bonusTotemData.getLevel() + 1),
                    "next_price_amount", bonusTotemData.getUpgradePrice(player) == null ? "0" : String.valueOf(bonusTotemData.getUpgradePrice(player).getCost()));
        }
        if (player != null) {
            content = TextUtil.withPAPI(content, player);
        }
        return content;
    }

    public String getString(String path) {
        return section.getString(path);
    }

    public List<String> getStringList(String path) {
        return section.getStringList(path);
    }

    public int getInt(String path) {
        return section.getInt(path);
    }

    public int getInt(String path, int defaultValue) {
        return section.getInt(path, defaultValue);
    }

    public double getDouble(String path) {
        return MathUtil.doCalculate(section.getString(path)).doubleValue();
    }

    public double getDouble(String path, Player player, AbstractThingData data) {
        return MathUtil.doCalculate(replacePlaceholder(section.getString(path), player, data)).doubleValue();
    }

    public boolean getBoolean(String path, boolean defaultValue) {
        return section.getBoolean(path, defaultValue);
    }

    public String getString(String path, Player player, AbstractThingData thingData) {
        return replacePlaceholder(section.getString(path), player, thingData);
    }

    public ConfigurationSection getSection() {
        return section;
    }
}
