package cn.superiormc.mythictotem.hooks;

import cn.superiormc.mythicchanger.manager.MatchItemManager;
import cn.superiormc.mythictotem.MythicTotem;
import cn.superiormc.mythictotem.managers.ConfigManager;
import cn.superiormc.mythictotem.managers.ErrorManager;
import cn.superiormc.mythictotem.managers.HookManager;
import cn.superiormc.mythictotem.utils.ItemUtil;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ItemPriceUtil {

    public static boolean getPrice(String pluginName,
                                   String item,
                                   Player player,
                                   int value,
                                   boolean take,
                                   ItemStack keyItems) {
        if (MythicTotem.freeVersion) {
            ErrorManager.errorManager.sendErrorMessage("§x§9§8§F§B§9§8[MythicTotem] §cError: You are using free version, " +
                    "hook item price can not be used in this version!");
            return false;
        }
        if (value < 0) {
            return false;
        }
        if (item == null) {
            return false;
        }
        int amount = 0;
        if (keyItems == null) {
            ItemStack[] storage = player.getInventory().getStorageContents();
            if (!take) {
                for (ItemStack tempVal1 : storage) {
                    if (tempVal1 == null || tempVal1.getType().isAir()) {
                        continue;
                    }
                    String tempVal10 = HookManager.hookManager.getHookItemID(pluginName, tempVal1);
                    if (tempVal10 != null && tempVal10.equals(item)) {
                        amount += tempVal1.getAmount();
                    }
                }
            }
            if (take || amount >= value) {
                if (take) {
                    for (ItemStack itemStack : storage) {
                        if (itemStack == null || itemStack.getType().isAir()) {
                            continue;
                        }
                        String tempVal10 = HookManager.hookManager.getHookItemID(pluginName, itemStack);
                        if (tempVal10 != null && tempVal10.equals(item)) {
                            if (itemStack.getAmount() >= value) {
                                itemStack.setAmount(itemStack.getAmount() - value);
                                break;
                            } else {
                                value -= itemStack.getAmount();
                                itemStack.setAmount(0);
                            }
                        }
                    }
                    player.getInventory().setStorageContents(storage);
                }
                return true;
            }
        }
        else {
            if (!take) {
                String tempVal3 = HookManager.hookManager.getHookItemID(pluginName, keyItems);
                if (tempVal3 != null && tempVal3.equals(item)) {
                    amount = keyItems.getAmount();
                }
            }
            if (take || amount >= value) {
                if (take) {
                    keyItems.setAmount(keyItems.getAmount() - value);
                }
                return true;
            }
        }
        return false;
    }

    public static boolean getPrice(Player player,
                                   ItemStack item,
                                   int value,
                                   boolean take,
                                   ItemStack keyItems) {
        if (ConfigManager.configManager.getBoolean("debug", false)) {
            Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[MythicTotem] §aRequired Price Item: " + item + "!");
            Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[MythicTotem] §aConfirmed Key Item: " + keyItems + "!");
        }
        if (value < 0) {
            return false;
        }
        if (item == null) {
            return false;
        }
        ItemStack[] storage = player.getInventory().getStorageContents();
        int amount = 0;
        if (keyItems == null) {
            if (!take) {
                for (ItemStack tempVal1 : storage) {
                    if (tempVal1 == null || tempVal1.getType().isAir()) {
                        continue;
                    }
                    if (ItemUtil.isSameItem(tempVal1, item)) {
                        amount += tempVal1.getAmount();
                    }
                }
            }
            if (take || amount >= value) {
                if (take) {
                    for (ItemStack itemStack : storage) {
                        if (itemStack == null || itemStack.getType().isAir()) {
                            continue;
                        }
                        if (ItemUtil.isSameItem(itemStack, item)) {
                            if (itemStack.getAmount() >= value) {
                                itemStack.setAmount(itemStack.getAmount() - value);
                                break;
                            } else {
                                value -= itemStack.getAmount();
                                itemStack.setAmount(0);
                            }
                        }
                    }
                    player.getInventory().setStorageContents(storage);
                }
                return true;
            }
        }
        else {
            ItemStack temItem = keyItems.clone();
            if (ItemUtil.isSameItem(temItem, item)) {
                if (!take) {
                    amount = temItem.getAmount();
                }
                if (take || amount >= value) {
                    if (take) {
                        keyItems.setAmount(temItem.getAmount() - value);
                    }
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean getPrice(Player player, ConfigurationSection section, int value, boolean take) {
        if (value < 0) {
            return false;
        }
        ItemStack[] storage = player.getInventory().getStorageContents();
        int amount = 0;
        if (!take) {
            for (ItemStack tempVal1 : storage) {
                if (tempVal1 == null || tempVal1.getType().isAir()) {
                    continue;
                }
                if (MatchItemManager.matchItemManager.getMatch(section.getConfigurationSection("match-item"), tempVal1)) {
                    amount += tempVal1.getAmount();
                }
            }
        }
        if (take || amount >= value) {
            if (take) {
                for (ItemStack itemStack : storage) {
                    if (itemStack == null || itemStack.getType().isAir()) {
                        continue;
                    }
                    if (MatchItemManager.matchItemManager.getMatch(section.getConfigurationSection("match-item"), itemStack)) {
                        if (itemStack.getAmount() >= value) {
                            itemStack.setAmount(itemStack.getAmount() - value);
                            break;
                        } else {
                            value -= itemStack.getAmount();
                            itemStack.setAmount(0);
                        }
                    }
                }
                player.getInventory().setStorageContents(storage);
            }
            return true;
        }
        else {
            return false;
        }
    }

}
