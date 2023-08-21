package cn.superiormc.mythictotem.managers;

import cn.superiormc.mythictotem.hooks.PriceHook;
import cn.superiormc.mythictotem.utils.ColorParser;
import cn.superiormc.mythictotem.utils.SavedItem;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public class PriceManager {

    private ConfigurationSection section;

    private Player player;

    private Block block;

    private String type;

    public PriceManager(ConfigurationSection section, Player player, Block block) {
        this.section = section;
        this.player = player;
        this.block = block;
        if (section == null) {
            type = "free";
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
    }
    public boolean CheckPrice(boolean take) {
        boolean priceBoolean = false;
        if (type.equals("free")) {
            priceBoolean = true;
        }
        else if (type.equals("hook")) {
            priceBoolean = PriceHook.getPrice(section.getString("hook-plugin"),
                    player,
                    section.getString("hook-item"),
                    section.getInt("amount", 0), take);
        }
        else if (type.equals("vanilla")) {
            ItemStack itemStack = null;
            String str = section.getString("material", "").toUpperCase();
            if (!str.equals("")) {
                if (str.equals("SKULL")) {
                    itemStack = new ItemStack(Material.getMaterial("PLAYER_HEAD"));
                    SkullMeta skullMeta = (SkullMeta) itemStack.getItemMeta();
                    GameProfile profile = new GameProfile(UUID.randomUUID(), null);
                    profile.getProperties().put("textures", new Property("textures", section.getString("skull_meta")));
                    try {
                        Method mtd = skullMeta.getClass().getDeclaredMethod("setProfile", GameProfile.class);
                        mtd.setAccessible(true);
                        mtd.invoke(skullMeta, profile);
                    } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException ex) {
                        ex.printStackTrace();
                    }
                    itemStack.setItemMeta(skullMeta);
                } else {
                    Material material = Material.getMaterial(str);
                    if (material == null) {
                        itemStack = SavedItem.GetItemByKey(section.getString("material", ""));
                        if (itemStack == null) {
                            Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[FlipCard] §cCan not get material: " + str + "!");
                        }
                    } else {
                        itemStack = new ItemStack(material);
                    }
                }
                ItemMeta itemMeta = itemStack.getItemMeta();
                if (itemMeta == null) {
                    Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[FlipCard] §cCan not get material: " + str + "!");
                }
                String display = section.getString("display", "");
                if (!display.equals("")) {
                    display = ColorParser.parse(display);
                    itemMeta.setDisplayName(display);
                }
                List<String> lore = section.getStringList("lore");
                if (Objects.nonNull(lore)) {
                    for (int i = 0; i < lore.size(); i++) {
                        String str1 = lore.get(i);
                        str1 = ColorParser.parse("&f" + str);
                        lore.set(i, str1);
                    }
                    itemMeta.setLore(lore);
                }
                int cmd = section.getInt("cmd", 0);
                if (cmd != 0) {
                    itemMeta.setCustomModelData(cmd);
                }
                itemStack.setItemMeta(itemMeta);
            }
            priceBoolean = PriceHook.checkItemPrice(player, itemStack, section.getInt("amount", 0), take);
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
            Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[MythicTotem] §cThere is something wrong in your UI configs!");
            priceBoolean = false;
        }
        return priceBoolean;
    }
}
