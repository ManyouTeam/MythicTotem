package cn.superiormc.mythictotem.objects.singlethings;

import cn.superiormc.mythictotem.managers.ConfigManager;
import cn.superiormc.mythictotem.objects.ObjectAction;
import cn.superiormc.mythictotem.objects.ObjectTotem;
import cn.superiormc.mythictotem.objects.checks.ObjectPriceCheck;
import cn.superiormc.mythictotem.objects.effect.AbstractEffect;
import cn.superiormc.mythictotem.objects.effect.EffectStatus;
import cn.superiormc.mythictotem.objects.effect.EffectUtil;
import cn.superiormc.mythictotem.utils.CommonUtil;
import cn.superiormc.mythictotem.utils.MathUtil;
import cn.superiormc.mythictotem.utils.TextUtil;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;

import java.util.*;

public class BonusTotemData extends AbstractThingData implements Comparable<BonusTotemData> {

    public final Location location;

    private int level;

    public final long placeTime;

    public final String totemId;

    public final boolean isCore;

    public final UUID totemUUID;

    public long lastCircleTime;

    public ObjectTotem totem;

    private ConfigurationSection section;

    private ObjectAction bonusEffectApplyActions;

    private ObjectAction bonusEffectRemoveActions;

    private ObjectAction bonusEffectCircleActions;

    private final Map<UUID, EffectStatus> mmoEffects = new HashMap<>();

    public BonusTotemData(Location location,
                          int level,
                          long placeTime,
                          String totemId,
                          boolean isCore,
                          UUID totemUUID) {

        this.location = location;
        this.level = level;
        this.placeTime = placeTime;
        this.totemId = totemId;
        this.isCore = isCore;
        this.totemUUID = totemUUID;
        this.totem = ConfigManager.configManager.getTotem(totemId);
        if (totem != null) {
            setSection(level);
        }
        if (section != null) {
            setAction();
        }
    }

    public void setNewLastCircleTime() {
        this.lastCircleTime = System.currentTimeMillis();
    }

    public boolean canExecuteCircleActionAgain() {
        return lastCircleTime + totem.getSection().getLong("period-ticks", 60) * 50 < System.currentTimeMillis();
    }

    public int getMaxLevel() {
        if (totem == null) {
            return Integer.MAX_VALUE;
        }
        return totem.getSection().getInt("bonus-effects.max-level", 1);
    }

    public ObjectPriceCheck getUpgradePrice(Player player) {
        if (totem == null) {
            return null;
        }

        ConfigurationSection priceSection = section.getConfigurationSection("price");
        if (priceSection == null) {
            return null;
        }

        return new ObjectPriceCheck(
                priceSection,
                player,
                location.getBlock()
        );
    }

    public String getUpgradePriceName(Player player) {
        ObjectPriceCheck priceCheck = getUpgradePrice(player);
        if (priceCheck == null || priceCheck.getSection() == null) {
            return "Unknown";
        }
        return CommonUtil.modifyString(player, priceCheck.getSection().getString("placeholder", "Unknown"), "amount", MathUtil.toDisplayString(priceCheck.getCost()));
    }

    public double getRange() {
        return section.getDouble("range", 10.0);
    }

    public void runBonusEffectsApplyActions(Player player) {
        if (!isCore) {
            return;
        }
        if (section.getBoolean("effects.enabled", false)) {
            TextUtil.sendMessage(null, TextUtil.pluginPrefix() + " §fStarted effect for player " + player.getName());
            mmoEffects.put(player.getUniqueId(), EffectUtil.startEffect(player, this));
        }
        bonusEffectApplyActions.runAllActions(player, this);
    }

    public void runBonusEffectsCircleActions(Player player) {
        if (!isCore) {
            return;
        }
        if (mmoEffects.get(player.getUniqueId()) != null) {
            mmoEffects.get(player.getUniqueId()).retryActiveEffects(this);
        }
        bonusEffectCircleActions.runAllActions(player, this);
    }

    public void runBonusEffectsRemoveActions(Player player) {
        if (!isCore) {
            return;
        }
        if (mmoEffects.get(player.getUniqueId()) != null) {
            for (AbstractEffect tempVal1 : mmoEffects.get(player.getUniqueId()).getActivedEffects()) {
                tempVal1.removePlayerStat();
            }
            mmoEffects.remove(player.getUniqueId());
        }
        bonusEffectRemoveActions.runAllActions(player, this);
    }

    public void setSection(int level) {
        this.section = getSection(level);
    }

    private ConfigurationSection getSection(int level) {
        ConfigurationSection totemBonusEffectsSection = totem.getSection().getConfigurationSection("bonus-effects");
        if (totemBonusEffectsSection == null || !totemBonusEffectsSection.getBoolean("enabled", false)) {
            return null;
        }

        Set<Integer> levels = new TreeSet<>();

        for (String key : totemBonusEffectsSection.getKeys(false)) {
            if (isInteger(key)) {
                levels.add(Integer.parseInt(key));
            }
        }

        if (levels.isEmpty()) {
            this.section = totemBonusEffectsSection;
        }

        int targetLevel = -1;

        for (int l : levels) {
            if (l <= level) {
                targetLevel = l;
            } else {
                break;
            }
        }

        if (targetLevel == -1) {
            targetLevel = levels.iterator().next();
        }

        return totemBonusEffectsSection.getConfigurationSection(String.valueOf(targetLevel));
    }

    public void setAction() {
        if (isCore) {
            this.bonusEffectApplyActions = new ObjectAction(section.getConfigurationSection("apply-actions"));
            this.bonusEffectRemoveActions = new ObjectAction(section.getConfigurationSection("remove-actions"));
            this.bonusEffectCircleActions = new ObjectAction(section.getConfigurationSection("circle-actions"));
        }
    }

    public ConfigurationSection getSection() {
        return section;
    }

    public int getLevel() {
        return level;
    }

    public void levelUp() {
        level++;
        setSection(level);
        setAction();
    }

    private boolean isInteger(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (Exception ignored) {
            return false;
        }
    }

    public String getDescription() {
        return section.getString("description", "Unknown");
    }

    public String getDescription(int level) {
        ConfigurationSection tempVal1 = getSection(level);
        if (tempVal1 == null) {
            return "Unknown";
        }
        return tempVal1.getString("description", "Unknown");
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof BonusTotemData data) {
            if (data.isCore) {
                return this.isCore && this.totemUUID.equals(data.totemUUID);
            }
            if (this.isCore) {
                return false;
            }
            return this.location.equals(data.location) && this.totemUUID.equals(data.totemUUID);
        }
        return false;
    }


    @Override
    public int compareTo(@NonNull BonusTotemData data) {
        return (int) (data.placeTime - this.placeTime);
    }
}