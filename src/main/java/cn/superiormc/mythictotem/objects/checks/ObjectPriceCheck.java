package cn.superiormc.mythictotem.objects.checks;

import cn.superiormc.mythictotem.hooks.ItemPriceUtil;
import cn.superiormc.mythictotem.managers.ConfigManager;
import cn.superiormc.mythictotem.managers.ErrorManager;
import cn.superiormc.mythictotem.managers.HookManager;
import cn.superiormc.mythictotem.methods.BuildItem;
import cn.superiormc.mythictotem.utils.CommonUtil;
import cn.superiormc.mythictotem.utils.SchedulerUtil;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ObjectPriceCheck {

    private final ConfigurationSection section;

    private final Player player;

    private final Block block;

    private final String type;

    public ObjectPriceCheck(ConfigurationSection section, Player player, Block block) {
        this.section = section;
        this.player = player;
        this.block = block;
        if (section == null) {
            type = "unknown";
        } else if (section.contains("hook-plugin") && section.contains("hook-item")) {
            type = "hook";
        } else if (section.contains("match-item") && CommonUtil.checkPluginLoad("MythicChanger")) {
            type = "match";
        } else if (section.contains("material")) {
            type = "vanilla";
        } else if (section.contains("economy-plugin")) {
            type = "economy";
        } else if (section.contains("economy-type") && !(section.contains("economy-plugin"))) {
            type = "exp";
        } else if (section.getBoolean("block-as-price")) {
            type = "block";
        } else {
            type = "free";
        }
        if (ConfigManager.configManager.getBoolean("debug", false)) {
            Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[MythicTotem] §aPrice Type: " + type + "!");
        }
    }
    public boolean CheckPrice(boolean take, ItemStack keyItems) {
        if (ConfigManager.configManager.getBoolean("debug", false)) {
            Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[MythicTotem] §aKey Item: " + keyItems + "!");
        }
        boolean priceBoolean = false;
        switch (type) {
            case "free":
                priceBoolean = true;
                break;
            case "block":
                SchedulerUtil.runSync(() -> {
                    CommonUtil.removeBlock(block);
                });
                break;
            case "hook":
                priceBoolean = ItemPriceUtil.getPrice(section.getString("hook-plugin"),
                        section.getString("hook-item"),
                        player,
                        section.getInt("amount", 1), take, keyItems);
                break;
            case "match":
                priceBoolean = ItemPriceUtil.getPrice(player, section, section.getInt("amount", 1), take);
                break;
            case "vanilla":
                priceBoolean = ItemPriceUtil.getPrice(player,
                        BuildItem.buildItemStack(section),
                        section.getInt("amount", 1),
                        take, keyItems);
                break;
            case "economy":
                priceBoolean = HookManager.hookManager.getPrice(player, section.getString("economy-plugin"),
                        section.getString("economy-type", "default"),
                        section.getDouble("amount", 0), take);
                break;
            case "exp":
                priceBoolean = HookManager.hookManager.getPrice(player, section.getString("economy-type"),
                        section.getInt("amount", 0), take);
                break;
            case "unknwon":
                ErrorManager.errorManager.sendErrorMessage("§x§9§8§F§B§9§8[MythicTotem] §cError: There is something wrong in your totem configs!");
                break;
        }
        return priceBoolean;
    }
}
