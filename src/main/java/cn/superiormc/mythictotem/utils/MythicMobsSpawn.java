package cn.superiormc.mythictotem.utils;

import io.lumine.mythic.api.mobs.MythicMob;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.mobs.ActiveMob;
import org.bukkit.Location;
import org.bukkit.block.Block;

public class MythicMobsSpawn {

    public static void DoIt(Block block, String mobID, int level) {
        MythicMob mob = MythicBukkit.inst().getMobManager().getMythicMob(mobID).orElse(null);
        if (mob != null) {
            Location spawnLocation = block.getLocation();
            ActiveMob am = mob.spawn(BukkitAdapter.adapt(spawnLocation), level);
        }
    }

}
