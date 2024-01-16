package cn.superiormc.mythictotem.managers;

import cn.superiormc.mythictotem.MythicTotem;
import cn.superiormc.mythictotem.hooks.PriceHook;
import cn.superiormc.mythictotem.utils.ItemUtil;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class PriceManager {

    private final ConfigurationSection section;

    private final Player player;

    private final Block block;

    private final String type;

    public PriceManager(ConfigurationSection section, Player player, Block block) {
        this.section = section;
        this.player = player;
        this.block = block;
        if (section == null) {
            type = "unknown";
        } else if (section.contains("hook-plugin") && section.contains("hook-item")) {
            type = "hook";
        } else if (section.contains("material")) {
            type = "vanilla";
        } else if (section.contains("economy-plugin")) {
            type = "economy";
        } else if (section.contains("economy-type") && !(section.contains("economy-plugin"))) {
            type = "exp";
        } else {
            type = "free";
        }
        if (MythicTotem.instance.getConfig().getBoolean("debug", false)) {
            Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[MythicTotem] §aPrice Type: " + type + "!");
        }
    }
    public boolean CheckPrice(boolean take, ItemStack keyItems) {
        if (MythicTotem.instance.getConfig().getBoolean("debug", false)) {
            Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[MythicTotem] §aKey Item: " + keyItems + "!");
        }
        boolean priceBoolean = false;
        if (type.equals("free")) {
            priceBoolean = true;
        }
        else if (type.equals("hook")) {
            priceBoolean = PriceHook.getPrice(section.getString("hook-plugin"),
                    section.getString("hook-item"),
                    player,
                    section.getInt("amount", 1), take, keyItems);
        }
        else if (type.equals("vanilla")) {
            priceBoolean = PriceHook.getPrice(player,
                    ItemUtil.buildItemStack(section, 1),
                    section.getInt("amount", 1),
                    take,
                    keyItems);
        }
        else if (type.equals("economy")) {
            priceBoolean = PriceHook.getPrice(section.getString("economy-plugin"),
                    section.getString("economy-type", "default"),
                    player,
                    section.getDouble("amount", 0), take);
        }
        else if (type.equals("exp")) {
            priceBoolean = PriceHook.getPrice(section.getString("economy-type"),
                    player,
                    section.getInt("amount", 0), take);
        }
        else if (type.equals("unknwon")) {
            Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[MythicTotem] §cThere is something wrong in your totem configs!");
            priceBoolean = false;
        }
        return priceBoolean;
    }
}
