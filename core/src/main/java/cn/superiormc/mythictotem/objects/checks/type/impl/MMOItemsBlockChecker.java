package cn.superiormc.mythictotem.objects.checks.type.impl;

import cn.superiormc.mythictotem.managers.ConfigManager;
import cn.superiormc.mythictotem.objects.checks.type.BlockChecker;
import net.Indyuce.mmoitems.MMOItems;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;

import java.util.Optional;

public class MMOItemsBlockChecker implements BlockChecker {

    @Override
    public boolean canCheck(String materialString) {
        return materialString.startsWith("mmoitems:");
    }

    @Override
    public boolean check(Block block, String materialString, Location location, int id) {
        String[] parts = materialString.split(":");
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
}