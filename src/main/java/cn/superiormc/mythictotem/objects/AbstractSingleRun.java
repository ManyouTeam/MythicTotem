package cn.superiormc.mythictotem.objects;

import cn.superiormc.mythictotem.objects.checks.ObjectCheck;
import cn.superiormc.mythictotem.objects.checks.ObjectPlaceCheck;
import cn.superiormc.mythictotem.utils.CommonUtil;
import cn.superiormc.mythictotem.utils.TextUtil;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.List;

public abstract class AbstractSingleRun {

    protected ConfigurationSection section;


    public AbstractSingleRun(ConfigurationSection section) {
        this.section = section;
    }

    protected String replacePlaceholder(String content, Player player, Location startLocation, ObjectCheck check, ObjectPlaceCheck totem) {
        content = CommonUtil.modifyString(content
                ,"world", check.getBlock().getWorld().getName()
                ,"player_world", player.getWorld().getName()
                ,"player_x", String.valueOf(player.getLocation().getX())
                ,"player_y", String.valueOf(player.getLocation().getY())
                ,"player_z", String.valueOf(player.getLocation().getZ())
                ,"player_pitch", String.valueOf(player.getLocation().getPitch())
                ,"player_yaw", String.valueOf(player.getLocation().getYaw())
                ,"player", player.getName(),
                "block_x", String.valueOf(check.getBlock().getX()),
                "block_y", String.valueOf(check.getBlock().getY()),
                "block_z", String.valueOf(check.getBlock().getZ()),
                "totem_column", String.valueOf(totem.getColumn()),
                "totem_row", String.valueOf(totem.getRow()),
                "totem_layout", String.valueOf(totem.getLayer()),
                "totem_id", totem.getTotem().getTotemID(),
                "totem_start_x", String.valueOf(startLocation.getX()),
                "totem_start_y", String.valueOf(startLocation.getY()),
                "totem_start_z", String.valueOf(startLocation.getZ())
                );
        content = TextUtil.parse(player, content);
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
        return section.getDouble(path);
    }

    public boolean getBoolean(String path, boolean defaultValue) {
        return section.getBoolean(path, defaultValue);
    }

    public String getString(String path, Player player, Location startLocation, ObjectCheck check, ObjectPlaceCheck totem) {
        return replacePlaceholder(section.getString(path), player, startLocation, check, totem);
    }

    public ConfigurationSection getSection() {
        return section;
    }
}
