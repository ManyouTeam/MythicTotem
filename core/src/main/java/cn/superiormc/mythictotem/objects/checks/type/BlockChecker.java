package cn.superiormc.mythictotem.objects.checks.type;

import cn.superiormc.mythictotem.utils.TextUtil;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;

public abstract class BlockChecker {

    public boolean canCheck(String materialString) {
        return materialString.startsWith(getCheckerName() + ":");
    }

    public abstract boolean check(Block block, String materialString, Location location, int id);

    public abstract Entity getEntityNeedRemove();

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
     * 获取检查器名称（用于错误信息）
     */
    protected abstract String getCheckerName();

}