package cn.superiormc.mythictotem.objects.checks.type.impl;

import cn.superiormc.mythictotem.managers.ConfigManager;
import cn.superiormc.mythictotem.managers.ErrorManager;
import cn.superiormc.mythictotem.objects.checks.type.BlockChecker;
import cn.superiormc.mythictotem.utils.CommonUtil;
import net.momirealms.craftengine.bukkit.api.CraftEngineBlocks;
import net.momirealms.craftengine.core.block.CustomBlock;
import net.momirealms.craftengine.core.block.ImmutableBlockState;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;

public class CraftEngineBlockChecker extends BlockChecker {

    @Override
    public boolean check(Block block, String materialString, Location location, int id) {
        if (!CommonUtil.checkPluginLoad("CraftEngine")) {
            ErrorManager.errorManager.sendErrorMessage("§cError: CraftEngine is not loaded but you are using block from it as totem layout!");
            return false;
        }
        String[] parts = materialString.split(":");
        if (!isValidMaterialFormat(parts, 3)) {
            return false;
        }
        try {
            ImmutableBlockState craftEngineBlock = CraftEngineBlocks.getCustomBlockState(block);
            if (craftEngineBlock == null) {
                return false;
            }
            CustomBlock customBlock = craftEngineBlock.owner().value();
            String expectedId = parts[1] + ":" + parts[2];
            return customBlock != null && expectedId.equals(customBlock.id().toString());
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
        return "craftengine";
    }
}