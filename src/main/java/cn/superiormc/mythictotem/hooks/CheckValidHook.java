package cn.superiormc.mythictotem.hooks;

import cn.superiormc.mythictotem.MythicTotem;
import com.willfp.ecoarmor.sets.ArmorSet;
import com.willfp.ecoarmor.sets.ArmorSlot;
import com.willfp.ecoarmor.sets.ArmorUtils;
import com.willfp.ecoitems.items.EcoItem;
import com.willfp.ecoitems.items.ItemUtilsKt;
import dev.lone.itemsadder.api.CustomStack;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.th0rgal.oraxen.api.OraxenItems;
import net.Indyuce.mmoitems.MMOItems;
import org.bukkit.inventory.ItemStack;

public class CheckValidHook {

    public static String checkValid(String pluginName, ItemStack itemStack) {
        if (!MythicTotem.instance.getServer().getPluginManager().isPluginEnabled(pluginName)) {
            MythicTotem.checkError("§x§9§8§F§B§9§8[MythicTotem] §cError: Your server don't have " + pluginName +
                    " plugin, but your totem prices config try use its hook!");
            return null;
        }
        else if (pluginName.equals("ItemsAdder")) {
            CustomStack customStack = CustomStack.byItemStack(itemStack);
            if (customStack != null) {
                return customStack.getNamespacedID();
            }
            else {
                return null;
            }
        }
        else if (pluginName.equals("Oraxen")) {
            String tempVal1 = OraxenItems.getIdByItem(itemStack);
            if (tempVal1 == null) {
                return null;
            }
            else {
                return tempVal1;
            }
        }
        else if (pluginName.equals("MMOItems")) {
            String tempVal1 = MMOItems.getID(itemStack);
            String tempVal2 = MMOItems.getTypeName(itemStack);
            if (tempVal1 == null || tempVal2 == null || tempVal1.isEmpty() || tempVal2.isEmpty()) {
                return null;
            }
            else {
                return tempVal2 + ";;" + tempVal1;
            }
        }
        else if (pluginName.equals("EcoItems")) {
            EcoItem tempVal1 = ItemUtilsKt.getEcoItem(itemStack);
            if (tempVal1 == null) {
                return null;
            }
            else {
                return tempVal1.getID();
            }
        }
        else if (pluginName.equals("EcoArmor")) {
            ArmorSet tempVal1 = ArmorUtils.getSetOnItem(itemStack);
            if (tempVal1 == null) {
                return null;
            }
            else {
                String tempVal2 = tempVal1.getId();
                ArmorSlot tempVal3 = ArmorSlot.getSlot(itemStack);
                if (tempVal3 == null) {
                    return null;
                }
                return tempVal2 + ";;" + tempVal3.toString();
            }
        }
        else if (pluginName.equals("MythicMobs")) {
            String tempVal1 = MythicBukkit.inst().getItemManager().getMythicTypeFromItem(itemStack);
            if (tempVal1 == null) {
                return null;
            }
            else {
                return tempVal1;
            }
        }
        else {
            MythicTotem.checkError("§x§9§8§F§B§9§8[MythicTotem] §cError: You set hook plugin to "
                    + pluginName + " in totem prices config, however for now MythicTotem is not support it!");
            return null;
        }
    }
}
