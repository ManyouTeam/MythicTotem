package cn.superiormc.mythictotem.managers;

import cn.superiormc.mythictotem.MythicTotem;
import cn.superiormc.mythictotem.utils.CommonUtil;
import dev.lone.itemsadder.api.CustomBlock;
import dev.lone.itemsadder.api.CustomFurniture;
import dev.lone.itemsadder.api.CustomMob;
import io.th0rgal.oraxen.api.OraxenBlocks;
import io.th0rgal.oraxen.api.OraxenFurniture;
import io.th0rgal.oraxen.mechanics.provided.gameplay.block.BlockMechanic;
import io.th0rgal.oraxen.mechanics.provided.gameplay.furniture.FurnitureMechanic;
import io.th0rgal.oraxen.mechanics.provided.gameplay.noteblock.NoteBlockMechanic;
import io.th0rgal.oraxen.mechanics.provided.gameplay.stringblock.StringBlockMechanic;
import net.Indyuce.mmoitems.MMOItems;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Optional;

public class MaterialManager {

    private final String materialString;

    private final Location location;

    private Block block;

    private Entity entity;

    private int id;

    public MaterialManager(@NotNull String materialString, @NotNull Location location, @NotNull int id) {
        this.materialString = materialString;
        this.location = location;
        this.id = id;
    }
    public boolean checkMaterial(){
        if (materialString.equals("none")) {
            if (MythicTotem.instance.getConfig().getBoolean("debug")) {
                Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[MythicTotem] §fSkipped none block.");
            }
            return true;
        } else if (materialString.startsWith("minecraft:")) {
            try {
                Material material = Material.getMaterial(materialString.split(":")[1].toUpperCase());
                EntityType entityType = EntityType.fromName(materialString.split(":")[1].toUpperCase());
                if (material != null) {
                    this.block = location.getBlock();
                    if (MythicTotem.instance.getConfig().getBoolean("debug")) {
                        Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[MythicTotem] §fShould be: " +
                                materialString + ", real block: " + block.getType().name() + ", location: " + location + ", ID: " + id + ".");
                    }
                    return material == block.getType();
                } else if (!MythicTotem.freeVersion && entityType != null) {
                    Location tempLocation = location.clone().add(0.5, 0, 0.5);
                    Collection<Entity> entities = CommonUtil.getNearbyEntity(tempLocation, 1);
                    if (MythicTotem.instance.getConfig().getBoolean("debug")) {
                        Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[MythicTotem] §fShould be: " +
                                materialString + ", find entities amount: " + entities.size() + ".");
                    }
                    for (Entity singleEntity: entities) {
                        if (MythicTotem.instance.getConfig().getBoolean("debug")) {
                            Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[MythicTotem] §fShould be: " +
                                    materialString + ", find entity: " + singleEntity.getType() + ".");
                        }
                        if (singleEntity.getType() == entityType) {
                            this.entity = singleEntity;
                            return true;
                        }
                    }
                    return false;
                }
                return false;
            } catch (IllegalArgumentException | NullPointerException ignored) {
            }
        } else if (materialString.startsWith("itemsadder:")) {
            try {
                this.block = location.getBlock();
                if (materialString.split(":").length != 3) {
                    Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[MythicTotem] §cError: Your itemsadder material does not meet" +
                            " the format claimed in plugin Wiki!");
                    return false;
                }
                CustomBlock iaBlock = CustomBlock.byAlreadyPlaced(block);
                if (iaBlock == null) {
                    return false;
                }
                return (materialString.split(":")[1] + ":" + materialString.split(":")[2]).
                        equals(iaBlock.getNamespacedID());
            } catch (NullPointerException ignored) {
            }
        } else if (materialString.startsWith("itemsadder_furniture:") && !MythicTotem.freeVersion) {
            try {
                this.block = location.getBlock();
                if (materialString.split(":").length != 3) {
                    Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[MythicTotem] §cError: Your itemsadder_furniture material does not meet" +
                            " the format claimed in plugin Wiki!");
                    return false;
                }
                Location tempLocation = location.clone().add(0.5, 0, 0.5);
                Collection<Entity> entities = CommonUtil.getNearbyEntity(tempLocation, 1);
                if (MythicTotem.instance.getConfig().getBoolean("debug")) {
                    Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[MythicTotem] §fShould be: " +
                            materialString + ", find entities amount: " + entities.size() + ".");
                }
                for (Entity singleEntity : entities) {
                    if (MythicTotem.instance.getConfig().getBoolean("debug")) {
                        Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[MythicTotem] §fShould be: " +
                                materialString + ", find entity: " + singleEntity.getType() + ".");
                    }
                    this.entity = singleEntity;
                    CustomFurniture iaEntity = CustomFurniture.byAlreadySpawned(singleEntity);
                    if (iaEntity == null) {
                        continue;
                    }
                    if ((materialString.split(":")[1] + ":" + materialString.split(":")[2]).
                            equals(iaEntity.getNamespacedID())) {
                        return true;
                    }
                }
                return false;
            } catch (NullPointerException ignored) {
            }
        } else if (materialString.startsWith("itemsadder_mob:") && !MythicTotem.freeVersion) {
            try {
                this.block = location.getBlock();
                if (materialString.split(":").length != 3) {
                    Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[MythicTotem] §cError: Your itemsadder_mob material does not meet" +
                            " the format claimed in plugin Wiki!");
                    return false;
                }
                Location tempLocation = location.clone().add(0.5, 0, 0.5);
                Collection<Entity> entities = CommonUtil.getNearbyEntity(tempLocation, 1);
                if (MythicTotem.instance.getConfig().getBoolean("debug")) {
                    Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[MythicTotem] §fShould be: " +
                            materialString + ", find entities amount: " + entities.size() + ".");
                }
                for (Entity singleEntity : entities) {
                    if (MythicTotem.instance.getConfig().getBoolean("debug")) {
                        Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[MythicTotem] §fShould be: " +
                                materialString + ", find entity: " + singleEntity.getType() + ".");
                    }
                    if (singleEntity instanceof ArmorStand) {
                        this.entity = singleEntity;
                        CustomMob iaEntity = CustomMob.byAlreadySpawned(singleEntity);
                        if (iaEntity == null) {
                            continue;
                        }
                        if ((materialString.split(":")[1] + ":" + materialString.split(":")[2]).
                                equals(iaEntity.getNamespacedID())) {
                            return true;
                        }
                    }
                }
                return false;
            } catch (NullPointerException ignored) {
            }
        } else if (materialString.startsWith("oraxen:")) {
            try {
                this.block = location.getBlock();
                NoteBlockMechanic tempVal1 = OraxenBlocks.getNoteBlockMechanic(block);
                StringBlockMechanic tempVal2 = OraxenBlocks.getStringMechanic(block);
                BlockMechanic tempVal3 = OraxenBlocks.getBlockMechanic(block);
                if (tempVal3 != null && (materialString.split(":")[1]).equals(tempVal3.getItemID())) {
                    return true;
                }
                else if (tempVal2 != null && (materialString.split(":")[1]).equals(tempVal2.getItemID())) {
                    return true;
                }
                else if (tempVal1 != null && (materialString.split(":")[1]).equals(tempVal1.getItemID())) {
                    return true;
                }
            } catch (NullPointerException ignored) {
            }
        } else if (materialString.startsWith("oraxen_furniture:") && !MythicTotem.freeVersion) {
            try {
                this.block = location.getBlock();
                if (materialString.split(":").length != 2) {
                    Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[MythicTotem] §cError: Your oraxen_furniture material does not meet" +
                            " the format claimed in plugin Wiki!");
                    return false;
                }
                Location tempLocation = location.clone().add(0.5, 0, 0.5);
                Collection<Entity> entities = CommonUtil.getNearbyEntity(tempLocation, 1);
                if (MythicTotem.instance.getConfig().getBoolean("debug")) {
                    Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[MythicTotem] §fShould be: " +
                            materialString + ", find entities amount: " + entities.size() + ".");
                }
                for (Entity singleEntity : entities) {
                    if (MythicTotem.instance.getConfig().getBoolean("debug")) {
                        Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[MythicTotem] §fShould be: " +
                                materialString + ", find entity: " + singleEntity.getType() + ".");
                    }
                    this.entity = singleEntity;
                    FurnitureMechanic furnitureMechanic = OraxenFurniture.getFurnitureMechanic(singleEntity);
                    if (furnitureMechanic == null) {
                        continue;
                    }
                    if (materialString.split(":")[1].equals(furnitureMechanic.getItemID())) {
                        return true;
                    }
                }
                return false;
            } catch (NullPointerException ignored) {
            }
        } else if (materialString.startsWith("mmoitems:")) {
            this.block = location.getBlock();
            Optional<net.Indyuce.mmoitems.api.block.CustomBlock> opt = MMOItems.plugin.getCustomBlocks().
                    getFromBlock(block.getBlockData());
            return opt.filter(customBlock -> customBlock.getId() == Integer.parseInt(materialString.split(":")[1])).isPresent();
        } else {
            this.block = location.getBlock();
            try {
                return (Material.getMaterial(materialString.split(":")[1].toUpperCase()) == block.getType());
            } catch (IllegalArgumentException | NullPointerException ignored) {
            }
        }
        return false;
    }

    public Entity getEntityNeedRemove() {
        return entity;
    }
}
