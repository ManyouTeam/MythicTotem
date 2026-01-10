package cn.superiormc.mythictotem.objects.checks.type.impl;

import cn.superiormc.mythictotem.managers.ConfigManager;
import cn.superiormc.mythictotem.managers.ErrorManager;
import cn.superiormc.mythictotem.objects.checks.type.BlockChecker;
import cn.superiormc.mythictotem.utils.CommonUtil;
import net.Indyuce.mmoitems.MMOItems;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;

import java.util.Optional;

public class MMOItemsBlockChecker extends BlockChecker {

    @Override
    public boolean check(Block block, String materialString, Location location, int id) {
        if (!CommonUtil.checkPluginLoad("MMOItems")) {
            ErrorManager.errorManager.sendErrorMessage("§cError: MMOItems is not loaded but you are using block from it as totem layout!");
            return false;
        }
        String[] parts = materialString.split(":");
        if (!isValidMaterialFormat(parts, 2)) {
            return false;
        }
        try {
            Optional<net.Indyuce.mmoitems.api.block.CustomBlock> opt = MMOItems.plugin.getCustomBlocks().getFromBlock(block.getBlockData());
            return opt.filter(customBlock -> customBlock.getId() == Integer.parseInt(parts[1])).isPresent();
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
        return "mmoitems";
    }
}