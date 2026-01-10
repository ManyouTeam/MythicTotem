package cn.superiormc.mythictotem.objects.checks.type.impl;

import cn.superiormc.mythictotem.managers.ConfigManager;
import cn.superiormc.mythictotem.managers.ErrorManager;
import cn.superiormc.mythictotem.objects.checks.type.BlockChecker;
import cn.superiormc.mythictotem.utils.CommonUtil;
import com.nexomc.nexo.api.NexoBlocks;
import com.nexomc.nexo.mechanics.custom_block.CustomBlockMechanic;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;

public class NexoBlockChecker extends BlockChecker {

    @Override
    public boolean check(Block block, String materialString, Location location, int id) {
        if (!CommonUtil.checkPluginLoad("Nexo")) {
            ErrorManager.errorManager.sendErrorMessage("§cError: Nexo is not loaded but you are using block from it as totem layout!");
            return false;
        }
        String[] parts = materialString.split(":");
        if (!isValidMaterialFormat(parts, 2)) {
            return false;
        }
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

    @Override
    protected String getCheckerName() {
        return "nexo";
    }
}