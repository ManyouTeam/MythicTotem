package cn.superiormc.mythictotem.listeners;

import cn.superiormc.mythictotem.gui.inv.TotemInfoGUI;
import cn.superiormc.mythictotem.managers.BonusEffectsManager;
import cn.superiormc.mythictotem.managers.ConfigManager;
import cn.superiormc.mythictotem.objects.effect.ObjectAuraSkillsEffect;
import cn.superiormc.mythictotem.objects.singlethings.BonusTotemData;
import cn.superiormc.mythictotem.utils.CommonUtil;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class BonusEffectsListener implements Listener {

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        BonusEffectsManager.bonusEffectsManager.destroyTotem(block.getLocation());
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onBlockExplode(BlockExplodeEvent event) {
        for (Block block : event.blockList()) {
            BonusEffectsManager.bonusEffectsManager.destroyTotem(block.getLocation());
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onEntityExplode(EntityExplodeEvent event) {
        for (Block block : event.blockList()) {
            BonusEffectsManager.bonusEffectsManager.destroyTotem(block.getLocation());
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onPistonExtend(BlockPistonExtendEvent event) {
        for (Block block : event.getBlocks()) {
            BonusEffectsManager.bonusEffectsManager.destroyTotem(block.getLocation());
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onPistonRetract(BlockPistonRetractEvent event) {
        for (Block block : event.getBlocks()) {
            BonusEffectsManager.bonusEffectsManager.destroyTotem(block.getLocation());
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onEntityGrief(EntityChangeBlockEvent event) {
        Block block = event.getBlock();
        BonusEffectsManager.bonusEffectsManager.destroyTotem(block.getLocation());
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (CommonUtil.checkPluginLoad("AuraSkills")) {
            ObjectAuraSkillsEffect.removePlayerStat(event.getPlayer(), 1);
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        BonusEffectsManager.bonusEffectsManager.removePlayer(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onPlayerClick(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getClickedBlock() != null
                && BonusEffectsManager.bonusEffectsManager != null
                && ConfigManager.configManager.getBoolean("bonus-effects.gui.enabled", true)) {
            BonusTotemData data = BonusEffectsManager.bonusEffectsManager.getBonusTotemAt(event.getClickedBlock().getLocation());
            if (data != null) {
                TotemInfoGUI.openGUI(event.getPlayer(), data);
            }
        }
    }
}
