package cn.superiormc.mythictotem.objects.checks.type.impl;

import cn.superiormc.mythictotem.managers.ConfigManager;
import cn.superiormc.mythictotem.objects.checks.type.BlockChecker;
import io.th0rgal.oraxen.api.OraxenBlocks;
import io.th0rgal.oraxen.mechanics.Mechanic;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;

public class OraxenBlockChecker implements BlockChecker {

    @Override
    public boolean canCheck(String materialString) {
        return materialString.startsWith("oraxen:");
    }

    @Override
    public boolean check(Block block, String materialString, Location location, int id) {
        String[] parts = materialString.split(":");
        try {
            Mechanic oraxenBlock = OraxenBlocks.getOraxenBlock(block.getBlockData());
            return oraxenBlock != null && parts[1].equals(oraxenBlock.getItemID());
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