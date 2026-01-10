package cn.superiormc.mythictotem.objects.checks.type.impl;

import cn.superiormc.mythictotem.MythicTotem;
import cn.superiormc.mythictotem.managers.ErrorManager;
import cn.superiormc.mythictotem.utils.CommonUtil;
import dev.lone.itemsadder.api.CustomMob;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;

public class ItemsAdderMobChecker extends AbstractEntityChecker {

    @Override
    public boolean check(Block block, String materialString, Location location, int id) {
        if (!CommonUtil.checkPluginLoad("ItemsAdder")) {
            ErrorManager.errorManager.sendErrorMessage("§cError: ItemsAdder is not loaded but you are using block from it as totem layout!");
            return false;
        }
        if (MythicTotem.freeVersion) {
            return false;
        }
        String[] parts = materialString.split(":");
        if (!isValidMaterialFormat(parts, 3)) {
            return false;
        }
        return checkEntities(materialString, location, parts);
    }

    @Override
    protected boolean isMatchingEntity(Entity entity, String[] parts) {
        if (entity instanceof ArmorStand) {
            this.entity = entity;
            CustomMob iaEntity = CustomMob.byAlreadySpawned(entity);
            return iaEntity != null && (parts[1] + ":" + parts[2]).equals(iaEntity.getNamespacedID());
        }
        return false;
    }

    @Override
    protected int getMinPartsLengthForDistance() {
        return 4;
    }

    @Override
    protected String getCheckerName() {
        return "itemsadder_mob";
    }
}