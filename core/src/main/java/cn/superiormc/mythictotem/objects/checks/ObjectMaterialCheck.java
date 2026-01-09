package cn.superiormc.mythictotem.objects.checks;

import cn.superiormc.mythictotem.MythicTotem;
import cn.superiormc.mythictotem.managers.BlockCheckManager;
import cn.superiormc.mythictotem.managers.ConfigManager;
import cn.superiormc.mythictotem.objects.checks.type.BlockChecker;
import cn.superiormc.mythictotem.objects.checks.type.impl.AbstractEntityChecker;
import cn.superiormc.mythictotem.objects.checks.type.impl.MinecraftBlockChecker;
import cn.superiormc.mythictotem.utils.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class ObjectMaterialCheck {

    public static Collection<String> loggedMaterials = new ArrayList<>();

    private static final Set<String> freeVersionThirdPartyBlocks = new HashSet<>();

    private static final int FREE_VERSION_MAX_THIRD_PARTY_BLOCKS = 3;

    private final String materialString;

    private final Location location;

    private final int id;

    private Entity entity;

    public ObjectMaterialCheck(@NotNull String materialString, @NotNull Location location, int id) {
        this.materialString = materialString;
        this.location = location;
        this.id = id;
    }

    public boolean checkMaterial() {
        if (materialString.equals("none")) {
            if (ConfigManager.configManager.getBoolean("debug", false)) {
                TextUtil.sendMessage(null, TextUtil.pluginPrefix() + " §fSkipped none block.");
            }
            return true;
        }

        BlockChecker checker = BlockCheckManager.blockCheckManager.getSuitableChecker(materialString);
        if (checker != null) {
            if (MythicTotem.freeVersion && !(checker instanceof AbstractEntityChecker)) {
                String baseMaterialType = extractBaseMaterialType(materialString);

                if (!freeVersionThirdPartyBlocks.contains(baseMaterialType)) {
                    if (freeVersionThirdPartyBlocks.size() >= FREE_VERSION_MAX_THIRD_PARTY_BLOCKS) {
                        TextUtil.sendMessage(null, TextUtil.pluginPrefix() + " §cError: Free version only supports up to " +
                                FREE_VERSION_MAX_THIRD_PARTY_BLOCKS + " different third-party plugin blocks!");
                        TextUtil.sendMessage(null, TextUtil.pluginPrefix() + " §cCurrent blocks: " + freeVersionThirdPartyBlocks);
                        return false;
                    }
                    freeVersionThirdPartyBlocks.add(baseMaterialType);
                }
            }
            
            boolean result = checker.check(location.getBlock(), materialString, location, id);
            this.entity = checker.getEntityNeedRemove();
            return result;
        }

        // 默认检查逻辑
        try {
            Block block = location.getBlock();
            String[] parts = materialString.split(":");
            return parts.length > 1 && block.getType() == Material.getMaterial(parts[1].toUpperCase());
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 提取基础方块类型，去除距离等参数
     * 例如：nexo:1:0.5 -> nexo:1
     */
    private String extractBaseMaterialType(String materialString) {
        String[] parts = materialString.split(":");
        if (parts.length <= 2) {
            return materialString;
        }
        
        // 检查最后一部分是否为数字（距离参数）
        try {
            Double.parseDouble(parts[parts.length - 1]);
            // 如果是数字，返回前面的部分
            return String.join(":", java.util.Arrays.copyOfRange(parts, 0, parts.length - 1));
        } catch (NumberFormatException e) {
            // 如果不是数字，返回完整字符串
            return materialString;
        }
    }

    public Entity getEntityNeedRemove() {
        return entity;
    }
}