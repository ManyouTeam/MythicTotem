package cn.superiormc.mythictotem.managers;

import cn.superiormc.mythictotem.MythicTotem;
import dev.lone.itemsadder.api.CustomBlock;
import dev.lone.itemsadder.api.CustomFurniture;
import io.th0rgal.oraxen.api.OraxenBlocks;
import net.Indyuce.mmoitems.MMOItems;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class MaterialManager {

    private String materialString;

    private Location location;

    private Block block;

    public MaterialManager(@NotNull String materialString, @NotNull Location location) {
        this.materialString = materialString;
        this.location = location;
    }
    public boolean CheckMaterial(){
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
                                materialString + ", real block: " + block.getType() + ".");
                    }
                    return material == block.getType();
                } else if (!MythicTotem.freeVersion && entityType != null) {
                    Future<Boolean> result = Bukkit.getScheduler().callSyncMethod(MythicTotem.instance, () -> {
                        World world = location.getWorld();
                        Location tempLocation = location.clone().add(0.5, 0, 0.5);
                        Collection<Entity> entities = world.getNearbyEntities(tempLocation, 1, 1, 1);
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
                                singleEntity.remove();
                                return true;
                            }
                        }
                        return false;
                    });
                    try {
                        return result.get();
                    } catch (InterruptedException | ExecutionException e) {
                        return false;
                    }
                }
                return false;
            } catch (IllegalArgumentException | NullPointerException ignored) {
            }
        } else if (materialString.startsWith("itemsadder:")) {
            try {
                this.block = location.getBlock();
                return (materialString.split(":")[1] + ":" + materialString.split(":")[2]).equals(CustomBlock.byAlreadyPlaced(block).getNamespacedID());
            } catch (NullPointerException ignored) {
            }
        } else if (materialString.startsWith("oraxen:")) {
            try {
                this.block = location.getBlock();
                if (OraxenBlocks.getNoteBlockMechanic(block).getItemID() == null){
                    return (materialString.split(":")[1]).equals(OraxenBlocks.getStringMechanic(block).getItemID());
                }
                else if (OraxenBlocks.getStringMechanic(block).getItemID() == null) {
                    return (materialString.split(":")[1]).equals(OraxenBlocks.getNoteBlockMechanic(block).getItemID());
                }
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
}
