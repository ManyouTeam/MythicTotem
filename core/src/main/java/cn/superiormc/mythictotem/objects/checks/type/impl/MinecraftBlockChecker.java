package cn.superiormc.mythictotem.objects.checks.type.impl;

import cn.superiormc.mythictotem.MythicTotem;
import cn.superiormc.mythictotem.managers.ConfigManager;
import cn.superiormc.mythictotem.utils.TextUtil;
import com.google.common.base.Enums;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

public class MinecraftBlockChecker extends AbstractEntityChecker {

    @Override
    public boolean canCheck(String materialString) {
        return materialString.startsWith("minecraft:");
    }

    @Override
    public boolean check(Block block, String materialString, Location location, int id) {
        String[] parts = materialString.split(":");
        try {
            // 先检查是否为普通方块
            Material material = Material.getMaterial(parts[1].toUpperCase());
            if (material != null) {
                if (ConfigManager.configManager.getBoolean("debug", false)) {
                    Bukkit.getConsoleSender().sendMessage(TextUtil.pluginPrefix() + " §fShould be: " +
                            materialString + ", real block: " + block.getType().name() + ", location: " + location + ", ID: " + id + ".");
                }
                return material == block.getType();
            } 
            // 然后检查是否为实体
            else {
                EntityType entityType = Enums.getIfPresent(EntityType.class, parts[1].toUpperCase()).orNull();
                if (entityType != null) {
                    return checkEntities(materialString, location, parts);
                }
            }
        } catch (Exception e) {
            if (ConfigManager.configManager.getBoolean("debug", false)) {
                e.printStackTrace();
            }
        }
        return false;
    }

    @Override
    protected boolean isMatchingEntity(Entity entity, String[] parts) {
        EntityType expectedType = Enums.getIfPresent(EntityType.class, parts[1].toUpperCase()).orNull();
        if (expectedType != null && entity.getType() == expectedType) {
            this.entity = entity;
            return true;
        }
        return false;
    }

    @Override
    protected int getMinPartsLengthForDistance() {
        return 3;
    }

    @Override
    protected String getCheckerName() {
        return "minecraft";
    }
}