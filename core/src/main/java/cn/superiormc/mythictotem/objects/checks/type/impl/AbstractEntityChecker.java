package cn.superiormc.mythictotem.objects.checks.type.impl;

import cn.superiormc.mythictotem.MythicTotem;
import cn.superiormc.mythictotem.managers.ConfigManager;
import cn.superiormc.mythictotem.managers.ErrorManager;
import cn.superiormc.mythictotem.objects.checks.type.BlockChecker;
import cn.superiormc.mythictotem.utils.CommonUtil;
import cn.superiormc.mythictotem.utils.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.Collection;

public abstract class AbstractEntityChecker implements BlockChecker {
    
    protected Entity entity;
    
    @Override
    public Entity getEntityNeedRemove() {
        return entity;
    }
    
    /**
     * 检查实体是否匹配
     */
    protected boolean checkEntities(String materialString, Location location, String[] parts) {
        if (MythicTotem.freeVersion) {
            ErrorManager.errorManager.sendErrorMessage("§cError: Free version can not use entity as totem layout, please consider purchase premium version " +
                    "to support the plugin author.");
            return false;
        }
        try {
            // 计算检查位置和距离
            Location tempLocation = location.clone().add(0.5, 0, 0.5);
            double checkDistance = parseCheckDistance(parts);
            
            // 获取附近实体
            Collection<Entity> entities = CommonUtil.getNearbyEntity(tempLocation, checkDistance);
            
            // 调试日志
            debugLog(materialString, entities.size());
            
            // 检查每个实体
            for (Entity singleEntity : entities) {
                debugLogEntity(materialString, singleEntity);
                
                // 跳过玩家实体
                if (singleEntity instanceof Player) {
                    continue;
                }
                
                // 具体实体类型检查由子类实现
                if (isMatchingEntity(singleEntity, parts)) {
                    return true;
                }
            }
        } catch (Exception e) {
            if (ConfigManager.configManager.getBoolean("debug", false)) {
                e.printStackTrace();
            }
        }
        return false;
    }
    
    /**
     * 解析检查距离
     */
    protected double parseCheckDistance(String[] parts) {
        double checkDistance = 0.5;
        if (parts.length >= getMinPartsLengthForDistance()) {
            try {
                checkDistance = Double.parseDouble(parts[parts.length - 1]);
            } catch (NumberFormatException ignored) {
                // 使用默认距离
            }
        }
        return checkDistance;
    }
    
    /**
     * 检查材料字符串格式是否有效
     */
    protected boolean isValidMaterialFormat(String[] parts, int minParts) {
        if (parts.length < minParts) {
            TextUtil.sendMessage(null, TextUtil.pluginPrefix() + " §cError: Your " + getCheckerName() + " material does not meet" +
                    " the format claimed in plugin Wiki!");
            return false;
        }
        return true;
    }
    
    /**
     * 调试日志：显示查找的实体数量
     */
    protected void debugLog(String materialString, int entityCount) {
        if (ConfigManager.configManager.getBoolean("debug", false)) {
            TextUtil.sendMessage(null, TextUtil.pluginPrefix() + " §fShould be: " +
                    materialString + ", find entities amount: " + entityCount + ".");
        }
    }
    
    /**
     * 调试日志：显示单个实体信息
     */
    protected void debugLogEntity(String materialString, Entity entity) {
        if (ConfigManager.configManager.getBoolean("debug", false)) {
            TextUtil.sendMessage(null, TextUtil.pluginPrefix() + " §fShould be: " +
                    materialString + ", find entity: " + entity.getType() + ".");
        }
    }
    
    /**
     * 检查单个实体是否匹配
     */
    protected abstract boolean isMatchingEntity(Entity entity, String[] parts);
    
    /**
     * 获取距离参数所需的最小部分数量
     */
    protected abstract int getMinPartsLengthForDistance();
    
    /**
     * 获取检查器名称（用于错误信息）
     */
    protected abstract String getCheckerName();
}