package cn.superiormc.mythictotem.objects.checks.type.impl;

import cn.superiormc.mythictotem.MythicTotem;
import cn.superiormc.mythictotem.managers.ConfigManager;
import cn.superiormc.mythictotem.objects.checks.type.BlockChecker;
import cn.superiormc.mythictotem.utils.TextUtil;
import net.momirealms.craftengine.bukkit.api.CraftEngineBlocks;
import net.momirealms.craftengine.core.block.CustomBlock;
import net.momirealms.craftengine.core.block.ImmutableBlockState;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;

public class CraftEngineBlockChecker implements BlockChecker {

    @Override
    public boolean canCheck(String materialString) {
        return materialString.startsWith("craftengine:");
    }

    @Override
    public boolean check(Block block, String materialString, Location location, int id) {
        String[] parts = materialString.split(":");
        try {
            if (parts.length != 3) {
                Bukkit.getConsoleSender().sendMessage(TextUtil.pluginPrefix() + " §cError: Your craftengine material does not meet" +
                        " the format claimed in plugin Wiki!");
                return false;
            }
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
}