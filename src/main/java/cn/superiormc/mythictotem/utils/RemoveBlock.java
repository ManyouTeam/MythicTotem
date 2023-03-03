package cn.superiormc.mythictotem.utils;

import org.bukkit.Location;
import org.bukkit.Material;

public class RemoveBlock {

    public static void DoIt(Location loc){
        loc.getBlock().setType(Material.AIR);
    }
}
