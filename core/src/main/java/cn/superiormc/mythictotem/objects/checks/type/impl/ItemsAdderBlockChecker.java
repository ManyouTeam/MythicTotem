package cn.superiormc.mythictotem.objects.checks.type.impl;

import cn.superiormc.mythictotem.managers.ConfigManager;
import cn.superiormc.mythictotem.objects.checks.type.BlockChecker;
import cn.superiormc.mythictotem.utils.TextUtil;
import dev.lone.itemsadder.api.CustomBlock;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;

public class ItemsAdderBlockChecker implements BlockChecker {

    @Override
    public boolean canCheck(String materialString) {
        return materialString.startsWith("itemsadder:");
    }

    @Override
    public boolean check(Block block, String materialString, Location location, int id) {
        String[] parts = materialString.split(":");
        try {
            if (parts.length != 3) {
                Bukkit.getConsoleSender().sendMessage(TextUtil.pluginPrefix() + " §cError: Your itemsadder material does not meet" +
                        " the format claimed in plugin Wiki!");
                return false;
            }
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
}