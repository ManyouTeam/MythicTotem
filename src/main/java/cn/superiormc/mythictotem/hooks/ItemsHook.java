package cn.superiormc.mythictotem.hooks;

import cn.superiormc.mythictotem.MythicTotem;
import cn.superiormc.mythictotem.utils.CommonUtil;
import com.ssomar.executableitems.executableitems.manager.ExecutableItemsManager;
import com.ssomar.score.api.executableitems.config.ExecutableItemInterface;
import com.willfp.eco.core.items.Items;
import com.willfp.ecoarmor.sets.ArmorSet;
import com.willfp.ecoarmor.sets.ArmorSets;
import com.willfp.ecoarmor.sets.ArmorSlot;
import com.willfp.ecoitems.items.EcoItem;
import com.willfp.ecoitems.items.EcoItems;
import dev.lone.itemsadder.api.CustomStack;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.xikage.mythicmobs.MythicMobs;
import io.th0rgal.oraxen.api.OraxenItems;
import io.th0rgal.oraxen.items.ItemBuilder;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.Type;
import net.Indyuce.mmoitems.api.item.template.MMOItemTemplate;
import org.bukkit.inventory.ItemStack;
import pers.neige.neigeitems.manager.ItemManager;

import java.util.Optional;

public class ItemsHook {
    public static int mythicMobsVersion = 0;

    public static ItemStack getHookItem(String pluginName, String itemID) {
        if (!CommonUtil.checkPluginLoad(pluginName)) {
            MythicTotem.checkError("§x§9§8§F§B§9§8[MythicTotem] §cError: Your server don't have " + pluginName +
                    " plugin, but your totem config try use its hook!");
            return null;
        }
        switch (pluginName) {
            case "ItemsAdder":
                CustomStack customStack = CustomStack.getInstance(itemID);
                if (customStack == null) {
                    MythicTotem.checkError("§x§9§8§F§B§9§8[MythicTotem] §cError: Can not get "
                            + pluginName + " item: " + itemID + "!");
                    return null;
                } else {
                    return customStack.getItemStack();
                }
            case "Oraxen":
                ItemBuilder itemBuilder = OraxenItems.getItemById(itemID);
                if (itemBuilder == null) {
                    MythicTotem.checkError("§x§9§8§F§B§9§8[MythicTotem] §cError: Can not get "
                            + pluginName + " item: " + itemID + "!");
                    return null;
                } else {
                    return itemBuilder.build();
                }
            case "MMOItems":
                Type type = MMOItems.plugin.getTypes().get(itemID.split(";;")[0]);
                if (type == null) {
                    MythicTotem.checkError("§x§9§8§F§B§9§8[MythicTotem] §cError: Can not get "
                            + pluginName + " item: " + itemID + "!");
                    return null;
                } else {
                    MMOItemTemplate template = MMOItems.plugin.getTemplates().getTemplate(type, itemID.split(";;")[1]);
                    if (template == null) {
                        MythicTotem.checkError("§x§9§8§F§B§9§8[MythicTotem] §cError: Can not get "
                                + pluginName + " item: " + itemID + "!");
                        return null;
                    }
                    return MMOItemsHook.getItem(template);
                }
            case "EcoItems":
                EcoItem ecoItems = EcoItems.INSTANCE.getByID(itemID);
                if (ecoItems == null) {
                    MythicTotem.checkError("§x§9§8§F§B§9§8[MythicTotem] §cError: Can not get "
                            + pluginName + " item: " + itemID + "!");
                    return null;
                } else {
                    return ecoItems.getItemStack();
                }
            case "EcoArmor":
                if (ArmorSets.getByID(itemID.split(";;")[0]) == null) {
                    return null;
                }
                ArmorSet armorSet = ArmorSets.getByID(itemID);
                if (armorSet == null) {
                    MythicTotem.checkError("§x§9§8§F§B§9§8[MythicTotem] §cError: Can not get "
                            + pluginName + " item: " + itemID + "!");
                    return null;
                }
                ArmorSlot armorSlot = ArmorSlot.getSlot(itemID.split(";;")[1].toUpperCase());
                if (armorSlot == null) {
                    MythicTotem.checkError("§x§9§8§F§B§9§8[MythicTotem] §cError: Can not get "
                            + pluginName + " item: " + itemID + "!");
                    return null;
                }
                ItemStack itemStack = armorSet.getItemStack(armorSlot);
                if (itemStack == null) {
                    MythicTotem.checkError("§x§9§8§F§B§9§8[MythicTotem] §cError: Can not get "
                            + pluginName + " item: " + itemID + "!");
                    return null;
                } else {
                    return itemStack;
                }
            case "MythicMobs":
                if (mythicMobsVersion == 0) {
                    if (CommonUtil.getClass("io.lumine.mythic.bukkit.MythicBukkit")) {
                        mythicMobsVersion = 5;
                    } else if (CommonUtil.getClass("io.lumine.xikage.mythicmobs.MythicMobs")) {
                        mythicMobsVersion = 4;
                    }
                }
                if (mythicMobsVersion == 5) {
                    ItemStack mmItem = MythicBukkit.inst().getItemManager().getItemStack(itemID);
                    if (mmItem == null) {
                        MythicTotem.checkError("§x§9§8§F§B§9§8[SpinToWin] §cError: Can not get "
                                + pluginName + " item: " + itemID + "!");
                        return null;
                    } else {
                        return mmItem;
                    }
                } else if (mythicMobsVersion == 4) {
                    ItemStack mmItem = MythicMobs.inst().getItemManager().getItemStack(itemID);
                    if (mmItem == null) {
                        MythicTotem.checkError("§x§9§8§F§B§9§8[SpinToWin] §cError: Can not get "
                                + pluginName + " v4 item: " + itemID + "!");
                        return null;
                    } else {
                        return mmItem;
                    }
                } else {
                    return null;
                }
            case "eco":
                return Items.lookup(itemID).getItem();
            case "NeigeItems":
                return ItemManager.INSTANCE.getItemStack(itemID);
            case "ExecutableItems":
                Optional<ExecutableItemInterface> itemInterface = ExecutableItemsManager.getInstance().getExecutableItem(itemID);
                return itemInterface.map(executableItemInterface -> executableItemInterface.buildItem(1, Optional.empty())).orElse(null);
        }
        MythicTotem.checkError("§x§9§8§F§B§9§8[MythicTotem] §cError: You set hook plugin to "
                + pluginName + " in totem config, however for now MythicTotem is not support it!");
        return null;
    }
}
