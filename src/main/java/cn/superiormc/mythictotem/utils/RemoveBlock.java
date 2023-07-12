package cn.superiormc.mythictotem.utils;

import cn.superiormc.mythictotem.configs.GeneralSettingConfigs;
import dev.lone.itemsadder.api.CustomBlock;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageEvent;

public class RemoveBlock {

    public static void DoIt(Player player, Location loc){
        loc.getBlock().setType(Material.AIR);
        if (CheckPluginLoad.DoIt("ItemsAdder")) {
            CustomBlock.remove(loc);
        }
        if (GeneralSettingConfigs.GetBlockBreakEventCancel() && !loc.getBlock().getType().isAir()) {
            BlockBreakEvent bbe = new BlockBreakEvent(loc.getBlock(), player);
            bbe.setDropItems(false);
            bbe.setExpToDrop(0);
            Bukkit.getPluginManager().callEvent(bbe);
        }
        if (GeneralSettingConfigs.GetBlockDamageEventCancel() && !loc.getBlock().getType().isAir()) {
            BlockDamageEvent bde = new BlockDamageEvent(player, loc.getBlock(), null, true);
            Bukkit.getPluginManager().callEvent(bde);
        }
    }
}
