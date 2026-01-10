package cn.superiormc.mythictotem.objects.checks.type.impl;

import cn.superiormc.mythictotem.managers.ConfigManager;
import cn.superiormc.mythictotem.managers.ErrorManager;
import cn.superiormc.mythictotem.objects.checks.type.BlockChecker;
import cn.superiormc.mythictotem.utils.CommonUtil;
import dev.lone.itemsadder.api.CustomBlock;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;

public class ItemsAdderBlockChecker extends BlockChecker {

    @Override
    public boolean check(Block block, String materialString, Location location, int id) {
        if (!CommonUtil.checkPluginLoad("ItemsAdder")) {
            ErrorManager.errorManager.sendErrorMessage("§cError: ItemsAdder is not loaded but you are using block from it as totem layout!");
            return false;
        }
        String[] parts = materialString.split(":");
        if (!isValidMaterialFormat(parts, 3)) {
            return false;
        }
        try {
            CustomBlock iaBlock = CustomBlock.byAlreadyPlaced(block);
            if (iaBlock == null) {
                return false;
            }
            return (parts[1] + ":" + parts[2]).equals(iaBlock.getNamespacedID());
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
        return "itemsadder";
    }
}