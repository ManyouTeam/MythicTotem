package cn.superiormc.mythictotem.gui.inv;

import cn.superiormc.mythictotem.MythicTotem;
import cn.superiormc.mythictotem.gui.InvGUI;
import cn.superiormc.mythictotem.managers.BonusEffectsManager;
import cn.superiormc.mythictotem.managers.ConfigManager;
import cn.superiormc.mythictotem.methods.BuildItem;
import cn.superiormc.mythictotem.objects.effect.EffectUtil;
import cn.superiormc.mythictotem.objects.singlethings.BonusTotemData;
import cn.superiormc.mythictotem.utils.MathUtil;
import cn.superiormc.mythictotem.utils.TextUtil;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;

public class TotemInfoGUI extends InvGUI {

    public int totemInfoSlot;

    public int totemUpgradeSlot;

    public BonusTotemData data;

    private TotemInfoGUI(Player player, BonusTotemData data) {
        super(player);
        this.data = data;
        constructGUI();
    }

    @Override
    protected void constructGUI() {
        title = TextUtil.withPAPI(ConfigManager.configManager.getString(player, "bonus-effects.gui.title", "Bonus Totem info"), player);
        if (Objects.isNull(inv)) {
            int size = ConfigManager.configManager.getInt("bonus-effects.gui.size", 27);
            inv = MythicTotem.methodUtil.createNewInv(player, size, title);
        }
        String[] tempVal1 = {"world", data.location.getWorld().getName(),
                "player_world", player.getWorld().getName(),
                "player_x", String.valueOf(player.getLocation().getX()),
                "player_y", String.valueOf(player.getLocation().getY()),
                "player_z", String.valueOf(player.getLocation().getZ()),
                "player_pitch", String.valueOf(player.getLocation().getPitch()),
                "player_yaw", String.valueOf(player.getLocation().getYaw()),
                "player", player.getName(),
                "block_x", String.valueOf(data.location.getX()),
                "block_y", String.valueOf(data.location.getY()),
                "block_z", String.valueOf(data.location.getZ()),
                "totem_id", data.totemId,
                "bonus_uuid", data.totemUUID.toString(),
                "bonus_level", String.valueOf(data.getLevel()),
                "bonus_range", String.format("%.1f", data.getRange()),
                "bonus_description", data.getDescription(),
                "bonus_limit", String.valueOf(EffectUtil.getMaxEffectsAmount(player, data)),
                "bonus_amount", String.valueOf(BonusEffectsManager.bonusEffectsManager.getPlayerActivedBonus(player).size()),
                "next_level", String.valueOf(data.getLevel() + 1),
                "next_price", data.getUpgradePriceName(player),
                "next_price_amount", data.getUpgradePrice(player) == null ? "0" : MathUtil.toDisplayString(data.getUpgradePrice(player).getCost()),
                "next_description", data.getDescription(data.getLevel() + 1)};
        ConfigurationSection totemInfoSection = ConfigManager.configManager.getConfigurationSection("bonus-effects.gui.totem-info-item");
        if (totemInfoSection != null) {
            totemInfoSlot = totemInfoSection.getInt("slot", 11);
            if (totemInfoSection.getBoolean("enabled", true) && totemInfoSlot >= 0) {
                ItemStack totemInfoItem = BuildItem.buildItemStack(player, totemInfoSection, totemInfoSection.getInt("amount", 1), tempVal1);
                setItem(totemInfoSlot, totemInfoItem);
            }
        }
        boolean displayUpgradeOrMax = data.getLevel() < data.getMaxLevel() && data.getUpgradePrice(player) != null;
        if (displayUpgradeOrMax) {
            ConfigurationSection totemUpgradeSection = ConfigManager.configManager.getConfigurationSection("bonus-effects.gui.totem-upgrade-item");
            if (totemUpgradeSection != null) {
                totemUpgradeSlot = totemUpgradeSection.getInt("slot", 15);
                if (totemUpgradeSection.getBoolean("enabled", true) && totemUpgradeSlot >= 0) {
                    ItemStack totemUpgradeItem = BuildItem.buildItemStack(player, totemUpgradeSection, totemUpgradeSection.getInt("amount", 1), tempVal1);
                    setItem(totemUpgradeSlot, totemUpgradeItem);
                }
            }
        } else {
            ConfigurationSection totemMaxUpgradeSection = ConfigManager.configManager.getConfigurationSection("bonus-effects.gui.totem-max-upgrade-item");
            if (totemMaxUpgradeSection != null) {
                if (totemMaxUpgradeSection.getBoolean("enabled", true) && totemUpgradeSlot >= 0) {
                    ItemStack totemUpgradeItem = BuildItem.buildItemStack(player, totemMaxUpgradeSection, totemMaxUpgradeSection.getInt("amount", 1));
                    setItem(totemUpgradeSlot, totemUpgradeItem);
                }
            }
        }

        ConfigurationSection customItemSection = ConfigManager.configManager.getConfigurationSection("bonus-effects.gui.custom-item");
        if (customItemSection != null) {
            for (String key : customItemSection.getKeys(false)){
                ConfigurationSection itemSection = customItemSection.getConfigurationSection(key);
                if (itemSection != null) {
                    setItem(Integer.parseInt(key), BuildItem.buildItemStack(player, itemSection, itemSection.getInt("amount", 1)));
                }
            }
        }
    }

    @Override
    public boolean clickEventHandle(Inventory inventory, ClickType type, int slot) {
        if (slot == totemUpgradeSlot) {
            if (BonusEffectsManager.bonusEffectsManager.upgradeBonusTotem(player, data)) {
                openGUI(player, data);
            }
        }
        return true;
    }

    public static void openGUI(Player player, BonusTotemData data) {
        TotemInfoGUI gui = new TotemInfoGUI(player, data);
        gui.openGUI();
    }
}
