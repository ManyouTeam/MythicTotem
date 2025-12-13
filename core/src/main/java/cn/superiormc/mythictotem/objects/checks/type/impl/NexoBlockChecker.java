package cn.superiormc.mythictotem.objects.checks.type.impl;

import cn.superiormc.mythictotem.managers.ConfigManager;
import cn.superiormc.mythictotem.objects.checks.type.BlockChecker;
import com.nexomc.nexo.api.NexoBlocks;
import com.nexomc.nexo.mechanics.custom_block.CustomBlockMechanic;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;

public class NexoBlockChecker implements BlockChecker {

    @Override
    public boolean canCheck(String materialString) {
        return materialString.startsWith("nexo:");
    }

    @Override
    public boolean check(Block block, String materialString, Location location, int id) {

        String[] parts = materialString.split(":");
        try {
            CustomBlockMechanic nexoBlock = NexoBlocks.customBlockMechanic(block);
            return nexoBlock != null && parts[1].equals(nexoBlock.getItemID());
        } catch (Exception e) {
            if (ConfigManager.configManager.getBoolean("debug", false)) {
                e.printStackTrace();
            }
            return false;
        }
    }

    @Override
    public Entity getEntityNeedRemove() {
        return null;
    }
}