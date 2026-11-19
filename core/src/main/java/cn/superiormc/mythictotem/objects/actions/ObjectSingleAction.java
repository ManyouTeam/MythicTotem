package cn.superiormc.mythictotem.objects.actions;

import cn.superiormc.mythictotem.managers.ActionManager;
import cn.superiormc.mythictotem.managers.ConfigManager;
import cn.superiormc.mythictotem.objects.AbstractSingleRun;
import cn.superiormc.mythictotem.objects.ObjectAction;
import cn.superiormc.mythictotem.objects.singlethings.AbstractThingData;
import cn.superiormc.mythictotem.objects.singlethings.BonusTotemData;
import cn.superiormc.mythictotem.objects.singlethings.TotemActiveData;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.Locale;

public class ObjectSingleAction extends AbstractSingleRun {

    private final ObjectAction action;

    public ObjectSingleAction(ObjectAction action, ConfigurationSection actionSection) {
        super(actionSection);
        this.action = action;
    }

    public void doAction(Player player, AbstractThingData thingData) {
        ActionManager.actionManager.doAction(this, player, thingData);
    }

    public Location getSpawnLocation(Player player, AbstractThingData data) {
        boolean useTrigger = getBoolean("block-as-trigger", false);
        String worldName = getString("world");
        if (!useTrigger && player != null && worldName != null) {
            return new Location(Bukkit.getWorld(worldName),
                    getDouble("x", player, data),
                    getDouble("y", player, data),
                    getDouble("z", player, data));
        }

        String mode = useTrigger ? "TRIGGER_BLOCK" : ConfigManager.configManager
                .getString("spawn-location", "TOTEM_CENTER").trim().toUpperCase(Locale.ROOT);
        Location location = switch (mode) {
            case "PLAYER" -> player == null ? null : player.getLocation();
            case "TRIGGER_BLOCK" -> getTriggerLocation(data);
            case "TOTEM_START" -> data instanceof TotemActiveData totem
                    ? totem.startLocation.clone() : getTriggerLocation(data);
            default -> getTotemCenter(data);
        };
        if (location != null || mode.equals("TRIGGER_BLOCK")) {
            return location;
        }
        return player == null ? getTriggerLocation(data) : player.getLocation();
    }

    private Location getTriggerLocation(AbstractThingData data) {
        if (data instanceof TotemActiveData totem) {
            return totem.check.getBlock().getLocation();
        }
        return data instanceof BonusTotemData bonus ? bonus.location.clone() : null;
    }

    private Location getTotemCenter(AbstractThingData data) {
        if (data instanceof TotemActiveData totem) {
            return totem.getCenterLocation();
        }
        return data instanceof BonusTotemData bonus ? bonus.location.clone().add(0.5, 0.5, 0.5) : null;
    }

    public ObjectAction getAction() {
        return action;
    }

}
