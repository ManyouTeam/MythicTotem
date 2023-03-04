package cn.superiormc.mythictotem.utils;

import dev.lone.itemsadder.api.CustomBlock;
import org.bukkit.Location;
import org.bukkit.Material;

public class RemoveBlock {

    public static void DoIt(Location loc){
        loc.getBlock().setType(Material.AIR);
        CustomBlock.remove(loc);
    }
}
