package cn.superiormc.mythictotem.utils;

import cn.superiormc.mythictotem.MythicTotem;
import cn.superiormc.mythictotem.managers.SavedItemManager;
import com.google.common.base.Enums;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.Method;
import java.util.List;
import java.util.UUID;

public class ItemUtil {
    
    public static ItemStack buildItemStack(ConfigurationSection section, int amount) {
        ItemStack item = new ItemStack(Material.STONE);
        String materialKey = section.getString("material");
        if (materialKey != null) {
            Material material = Material.getMaterial(materialKey.toUpperCase());
            if (material != null) {
                item.setType(material);
            } else {
                ItemStack savedItem = SavedItemManager.GetItemByKey(section.getString("material", ""));
                if (savedItem != null) {
                    item = savedItem;
                }
            }
        }
        if (amount > 0) {
            item.setAmount(amount);
        }
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return item;
        }
        String displayNameKey = section.getString("name", section.getString("display"));
        if (displayNameKey != null) {
            meta.setDisplayName(TextUtil.parse(displayNameKey));
        }
        List<String> loreKey = section.getStringList("lore");
        if (!loreKey.isEmpty()) {
            meta.setLore(TextUtil.getListWithColor(loreKey));
        }
        if (CommonUtil.getMajorVersion(14)) {
            int customModelDataKey = section.getInt("custom-model-data", section.getInt("cmd", -1));
            if (customModelDataKey > 0) {
                meta.setCustomModelData(customModelDataKey);
            }
        }
        List<String> itemFlagKey = section.getStringList("flags");
        if (!itemFlagKey.isEmpty()) {
            for (String flag : itemFlagKey) {
                flag = flag.toUpperCase();
                ItemFlag itemFlag = Enums.getIfPresent(ItemFlag.class, flag).orNull();
                if (itemFlag != null) {
                    meta.addItemFlags(itemFlag);
                }
            }
        }
        ConfigurationSection enchantsKey = section.getConfigurationSection("enchants");
        if (enchantsKey != null) {
            for (String ench : enchantsKey.getKeys(false)) {
                Enchantment vanillaEnchant = Enchantment.getByKey(NamespacedKey.minecraft(ench.toLowerCase()));
                if (vanillaEnchant != null) {
                    meta.addEnchant(vanillaEnchant, enchantsKey.getInt(ench), true);
                }
            }
        }
        if (meta instanceof SkullMeta) {
            SkullMeta skullMeta = (SkullMeta) meta;
            String skullTextureNameKey = section.getString("skull-meta", section.getString("skull"));
            if (skullTextureNameKey != null) {
                GameProfile profile = new GameProfile(UUID.randomUUID(), "");
                profile.getProperties().put("textures", new Property("textures", skullTextureNameKey));
                try {
                    Method mtd = skullMeta.getClass().getDeclaredMethod("setProfile", GameProfile.class);
                    mtd.setAccessible(true);
                    mtd.invoke(skullMeta, profile);
                } catch (Exception exception) {
                    MythicTotem.checkError("§x§9§8§F§B§9§8[MythicTotem] §cError: Can not parse skull texture in a item!");
                }
            }
            item.setItemMeta(skullMeta);
        }
        item.setItemMeta(meta);
        return item;
    }

}
