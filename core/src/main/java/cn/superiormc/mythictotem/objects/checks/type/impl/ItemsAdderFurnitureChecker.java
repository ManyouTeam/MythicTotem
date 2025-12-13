package cn.superiormc.mythictotem.objects.checks.type.impl;

import dev.lone.itemsadder.api.CustomFurniture;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;

public class ItemsAdderFurnitureChecker extends AbstractEntityChecker {

    @Override
    public boolean canCheck(String materialString) {
        return materialString.startsWith("itemsadder_furniture:");
    }

    @Override
    public boolean check(Block block, String materialString, Location location, int id) {
        String[] parts = materialString.split(":");
        if (!isValidMaterialFormat(parts, 3)) {
            return false;
        }
        return checkEntities(materialString, location, parts);
    }

    @Override
    protected boolean isMatchingEntity(Entity entity, String[] parts) {
        this.entity = entity;
        CustomFurniture iaEntity = CustomFurniture.byAlreadySpawned(entity);
        return iaEntity != null && (parts[1] + ":" + parts[2]).equals(iaEntity.getNamespacedID());
    }

    @Override
    protected int getMinPartsLengthForDistance() {
        return 4;
    }

    @Override
    protected String getCheckerName() {
        return "itemsadder_furniture";
    }
}