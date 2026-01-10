package cn.superiormc.mythictotem.objects.checks.type.impl;

import cn.superiormc.mythictotem.managers.ConfigManager;
import cn.superiormc.mythictotem.managers.ErrorManager;
import cn.superiormc.mythictotem.objects.checks.type.BlockChecker;
import cn.superiormc.mythictotem.utils.CommonUtil;
import io.th0rgal.oraxen.api.OraxenBlocks;
import io.th0rgal.oraxen.mechanics.Mechanic;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;

public class OraxenBlockChecker extends BlockChecker {

    @Override
    public boolean check(Block block, String materialString, Location location, int id) {
        if (!CommonUtil.checkPluginLoad("Oraxen")) {
            ErrorManager.errorManager.sendErrorMessage("§cError: Oraxen is not loaded but you are using block from it as totem layout!");
            return false;
        }
        String[] parts = materialString.split(":");
        if (!isValidMaterialFormat(parts, 2)) {
            return false;
        }
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

    @Override
    protected String getCheckerName() {
        return "oraxen";
    }
}