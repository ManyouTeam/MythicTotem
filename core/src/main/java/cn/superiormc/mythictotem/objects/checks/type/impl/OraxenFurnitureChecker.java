package cn.superiormc.mythictotem.objects.checks.type.impl;

import io.th0rgal.oraxen.api.OraxenFurniture;
import io.th0rgal.oraxen.mechanics.provided.gameplay.furniture.FurnitureMechanic;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;

public class OraxenFurnitureChecker extends AbstractEntityChecker {

    @Override
    public boolean canCheck(String materialString) {
        return materialString.startsWith("oraxen_furniture:");
    }

    @Override
    public boolean check(Block block, String materialString, Location location, int id) {
        String[] parts = materialString.split(":");
        if (!isValidMaterialFormat(parts, 2)) {
            return false;
        }
        return checkEntities(materialString, location, parts);
    }

    @Override
    protected boolean isMatchingEntity(Entity entity, String[] parts) {
        this.entity = entity;
        FurnitureMechanic furnitureMechanic = OraxenFurniture.getFurnitureMechanic(entity);
        return furnitureMechanic != null && parts[1].equals(furnitureMechanic.getItemID());
    }

    @Override
    protected int getMinPartsLengthForDistance() {
        return 3;
    }

    @Override
    protected String getCheckerName() {
        return "oraxen_furniture";
    }
}