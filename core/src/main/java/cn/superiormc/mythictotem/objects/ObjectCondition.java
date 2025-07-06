package cn.superiormc.mythictotem.objects;

import cn.superiormc.mythictotem.objects.checks.ObjectCheck;
import cn.superiormc.mythictotem.objects.checks.ObjectPlaceCheck;
import cn.superiormc.mythictotem.objects.conditions.ObjectSingleCondition;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemoryConfiguration;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class ObjectCondition {

    private ConfigurationSection section;

    private final List<ObjectSingleCondition> conditions = new ArrayList<>();

    public ObjectCondition() {
        this.section = new MemoryConfiguration();
    }

    public ObjectCondition(ConfigurationSection section) {
        this.section = section;
        initCondition();
    }

    private void initCondition() {
        if (section == null) {
            this.section = new MemoryConfiguration();
            return;
        }
        for (String key : section.getKeys(false)) {
            ConfigurationSection singleActionSection = section.getConfigurationSection(key);
            if (singleActionSection == null) {
                continue;
            }
            ObjectSingleCondition singleAction = new ObjectSingleCondition(this, singleActionSection);
            conditions.add(singleAction);
        }
    }

    public boolean getAllBoolean(Player player, Location startLocation, ObjectCheck check, ObjectPlaceCheck totem) {
        for (ObjectSingleCondition singleCondition : conditions){
            if (!singleCondition.checkBoolean(player, startLocation, check, totem)) {
                return false;
            }
        }
        return true;
    }

    public boolean getAnyBoolean(Player player, Location startLocation, ObjectCheck check, ObjectPlaceCheck totem) {
        if (player == null) {
            return false;
        }
        for (ObjectSingleCondition singleCondition : conditions){
            if (singleCondition.checkBoolean(player, startLocation, check, totem)) {
                return true;
            }
        }
        return false;
    }
}
