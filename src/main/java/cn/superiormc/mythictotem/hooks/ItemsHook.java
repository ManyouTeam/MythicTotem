package cn.superiormc.mythictotem.hooks;

import cn.superiormc.mythictotem.MythicTotem;
import cn.superiormc.mythictotem.utils.CommonUtil;
import com.willfp.ecoarmor.sets.ArmorSet;
import com.willfp.ecoarmor.sets.ArmorSets;
import com.willfp.ecoarmor.sets.ArmorSlot;
import com.willfp.ecoitems.items.EcoItem;
import com.willfp.ecoitems.items.EcoItems;
import com.willfp.eco.core.items.Items;
import dev.lone.itemsadder.api.CustomStack;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.xikage.mythicmobs.MythicMobs;
import io.th0rgal.oraxen.api.OraxenItems;
import io.th0rgal.oraxen.items.ItemBuilder;
import net.Indyuce.mmoitems.MMOItems;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;

import java.util.concurrent.ExecutionException;

public class ItemsHook {
    public static ItemStack getHookItem(String pluginName, String itemID) {
        if (!CommonUtil.checkPluginLoad(pluginName)) {
            MythicTotem.checkError("§x§9§8§F§B§9§8[MythicTotem] §cError: Your server don't have " + pluginName +
                    " plugin, but your totem config try use its hook!");
            return null;
        }
        try {
            return Bukkit.getScheduler().callSyncMethod(MythicTotem.instance, () -> {
                        switch (pluginName) {
                            case "ItemsAdder":
                                if (CustomStack.getInstance(itemID) == null) {
                                    Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[MythicTotemd] §cError: Can not get "
                                            + pluginName + " item: " + itemID + "!");
                                    return null;
                                } else {
                                    CustomStack customStack = CustomStack.getInstance(itemID);
                                    return customStack.getItemStack();
                                }
                            case "Oraxen":
                                if (OraxenItems.getItemById(itemID) == null) {
                                    MythicTotem.checkError("§x§9§8§F§B§9§8[MythicTotem] §cError: Can not get "
                                            + pluginName + " item: " + itemID + "!");
                                    return null;
                                } else {
                                    ItemBuilder builder = OraxenItems.getItemById(itemID);
                                    return builder.build();
                                }
                            case "MMOItems":
                                if (MMOItems.plugin.getTypes().get(itemID.split(";;")[0]) == null) {
                                    MythicTotem.checkError("§x§9§8§F§B§9§8[MythicTotem] §cError: Can not get "
                                            + pluginName + " item: " + itemID + "!");
                                    return null;
                                } else if (MMOItems.plugin.getItem(itemID.split(";;")[0], itemID.split(";;")[1]) == null) {
                                    MythicTotem.checkError("§x§9§8§F§B§9§8[MythicTotem] §cError: Can not get "
                                            + pluginName + " item: " + itemID + "!");
                                    return null;
                                }
                                return MMOItems.plugin.getItem(itemID.split(";;")[0], itemID.split(";;")[1]);
                            case "EcoItems":
                                EcoItems ecoItems = EcoItems.INSTANCE;
                                if (ecoItems.getByID(itemID) == null) {
                                    MythicTotem.checkError("§x§9§8§F§B§9§8[MythicTotem] §cError: Can not get "
                                            + pluginName + " item: " + itemID + "!");
                                    return null;
                                } else {
                                    EcoItem ecoItem = ecoItems.getByID(itemID);
                                    return ecoItem.getItemStack();
                                }
                            case "eco":
                                return Items.lookup(itemID).getItem();
                            case "EcoArmor":
                                if (ArmorSets.getByID(itemID.split(";;")[0]) == null) {
                                    return null;
                                }
                                ArmorSet armorSet = ArmorSets.getByID(itemID);
                                ItemStack itemStack = null;
                                switch (itemID.split(";;")[1].toUpperCase()) {
                                    case "BOOTS":
                                        itemStack = armorSet.getItemStack(ArmorSlot.BOOTS);
                                        break;
                                    case "CHESTPLATE":
                                        itemStack = armorSet.getItemStack(ArmorSlot.CHESTPLATE);
                                        break;
                                    case "ELYTRA":
                                        itemStack = armorSet.getItemStack(ArmorSlot.ELYTRA);
                                        break;
                                    case "HELMET":
                                        itemStack = armorSet.getItemStack(ArmorSlot.HELMET);
                                        break;
                                    case "LEGGINGS":
                                        itemStack = armorSet.getItemStack(ArmorSlot.LEGGINGS);
                                        break;
                                }
                                if (itemStack == null) {
                                    MythicTotem.checkError("§x§9§8§F§B§9§8[MythicTotem] §cError: Can not get "
                                            + pluginName + " item: " + itemID + "!");
                                    return null;
                                } else {
                                    return itemStack;
                                }
                            case "MythicMobs":
                                try {
                                    if (MythicBukkit.inst().getItemManager().getItemStack(itemID) == null) {
                                        MythicTotem.checkError("§x§9§8§F§B§9§8[MythicTotem] §cError: Can not get "
                                                + pluginName + " v5 item: " + itemID + "!");
                                        return null;
                                    } else {
                                        return MythicBukkit.inst().getItemManager().getItemStack(itemID);
                                    }
                                } catch (NoClassDefFoundError ep) {
                                    if (MythicMobs.inst().getItemManager().getItemStack(itemID) == null) {
                                        MythicTotem.checkError("§x§9§8§F§B§9§8[MythicTotem] §cError: Can not get "
                                                + pluginName + " v4 item: " + itemID + "!");
                                        return null;
                                    } else {
                                        return MythicMobs.inst().getItemManager().getItemStack(itemID);
                                    }
                                }
                        }
                MythicTotem.checkError("§x§9§8§F§B§9§8[MythicTotem] §cError: You set hook plugin to "
                        + pluginName + " in totem config, however for now MythicTotem does not support it!");
                return null;
            }).get();
        } catch (InterruptedException | ExecutionException ignored) {
            MythicTotem.checkError("§x§9§8§F§B§9§8[MythicTotem] §cError: Failed to get hook item!");
            return null;
        }
    }
}
