package cn.superiormc.mythictotem.listeners;

import cn.superiormc.mythictotem.managers.BlockCheckManager;
import cn.superiormc.mythictotem.managers.LanguageManager;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class BlockIdInspectListener implements Listener {

    private static final Set<UUID> waitingPlayers = ConcurrentHashMap.newKeySet();

    public static void startInspect(Player player) {
        waitingPlayers.add(player.getUniqueId());
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onInspect(PlayerInteractEvent event) {
        if (!EquipmentSlot.HAND.equals(event.getHand())) {
            return;
        }
        if (!waitingPlayers.contains(event.getPlayer().getUniqueId())) {
            return;
        }
        if (event.getAction() != Action.LEFT_CLICK_BLOCK && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        Block clickedBlock = event.getClickedBlock();
        if (clickedBlock == null) {
            return;
        }

        waitingPlayers.remove(event.getPlayer().getUniqueId());
        event.setCancelled(true);

        String blockId = BlockCheckManager.blockCheckManager.getBlockId(clickedBlock);
        if (blockId == null) {
            LanguageManager.languageManager.sendStringText(event.getPlayer(), "block-id-failed");
            return;
        }

        LanguageManager.languageManager.sendStringText(
                event.getPlayer(),
                "block-id-result",
                "block_id",
                blockId,
                "world",
                clickedBlock.getWorld().getName(),
                "x",
                String.valueOf(clickedBlock.getX()),
                "y",
                String.valueOf(clickedBlock.getY()),
                "z",
                String.valueOf(clickedBlock.getZ())
        );
    }
}
