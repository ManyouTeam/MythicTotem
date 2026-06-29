package cn.superiormc.mythictotem.gui;

import cn.superiormc.mythictotem.methods.Dupe;
import cn.superiormc.mythictotem.utils.SchedulerUtil;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NonNull;

public abstract class InvGUI extends AbstractGUI implements InventoryHolder {

    protected Inventory inv;

    public String title;

    public InvGUI(Player player) {
        super(player);
    }

    public abstract boolean clickEventHandle(Inventory inventory, ClickType type, int slot);

    @Override
    public void openGUI() {
        constructGUI();
        if (inv != null) {
            SchedulerUtil.runSync(player, () -> player.openInventory(inv));
        }
    }

    @Override
    public @NonNull Inventory getInventory() {
        return inv;
    }

    public void setItem(int slot, ItemStack item) {
        inv.setItem(slot, Dupe.markGuiDisplayItem(item));
    }

    public ConfigurationSection getSection() {
        return null;
    }
}
