package cn.superiormc.mythictotem.objects.checks.type;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;

public interface BlockChecker {

    boolean canCheck(String materialString);

    boolean check(Block block, String materialString, Location location, int id);

    Entity getEntityNeedRemove();

}