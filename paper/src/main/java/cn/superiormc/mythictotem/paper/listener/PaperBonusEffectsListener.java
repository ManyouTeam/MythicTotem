package cn.superiormc.mythictotem.paper.listener;

import cn.superiormc.mythictotem.listeners.BonusEffectsListener;
import cn.superiormc.mythictotem.managers.BonusEffectsManager;
import com.destroystokyo.paper.event.block.BlockDestroyEvent;
import io.papermc.paper.event.block.BlockBreakBlockEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

/** Adds Paper-only destruction sources while reusing the common Bukkit listener. */
public class PaperBonusEffectsListener extends BonusEffectsListener {

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockBreakBlock(BlockBreakBlockEvent event) {
        BonusEffectsManager.bonusEffectsManager.destroyTotem(event.getBlock().getLocation());
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockDestroy(BlockDestroyEvent event) {
        BonusEffectsManager.bonusEffectsManager.destroyTotem(event.getBlock().getLocation());
    }
}
