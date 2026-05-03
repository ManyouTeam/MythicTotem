package cn.superiormc.mythictotem.gui;

import cn.superiormc.mythictotem.MythicTotem;
import cn.superiormc.mythictotem.listeners.GUIListener;
import cn.superiormc.mythictotem.methods.Dupe;
import cn.superiormc.mythictotem.utils.SchedulerUtil;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public abstract class InvGUI extends AbstractGUI {

    protected Inventory inv;

    public Listener guiListener;

    public String title;

    public InvGUI(Player player) {
        super(player);
    }

    public abstract boolean clickEventHandle(Inventory inventory, ClickType type, int slot);

    @Override
    public void openGUI() {
        constructGUI();
        if (inv != null) {
            SchedulerUtil.runSync(player, () -> {
                this.guiListener = new GUIListener(this);
                Bukkit.getPluginManager().registerEvents(guiListener, MythicTotem.instance);
                player.openInventory(inv);
            });
        }
    }

    public Inventory getInv() {
        return inv;
    }

    public void setItem(int slot, ItemStack item) {
        inv.setItem(slot, Dupe.markGuiDisplayItem(item));
    }

    public ConfigurationSection getSection() {
        return null;
    }
}
