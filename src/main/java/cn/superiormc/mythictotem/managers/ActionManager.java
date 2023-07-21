package cn.superiormc.mythictotem.managers;

import cn.superiormc.mythictotem.MythicTotem;
import cn.superiormc.mythictotem.configs.Messages;
import cn.superiormc.mythictotem.utils.CheckPluginLoad;
import cn.superiormc.mythictotem.utils.DispatchCommand;
import cn.superiormc.mythictotem.utils.MythicMobsSpawn;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ActionManager {

    private List<String> action = new ArrayList<>();

    private Player player;

    private Block block;

    public ActionManager(List<String> action, Player player, Block block) {
        this.action = action;
        this.player = player;
        this.block = block;
    }

    public void CheckAction(){
        for(String singleAction : action) {
            if (singleAction.startsWith("none")) {
                return;
            } else if (singleAction.startsWith("message: ") && player != null) {
                player.sendMessage(Messages.GetActionMessages(singleAction.substring(9)));
            } else if (singleAction.startsWith("announcement: ")) {
                Collection<? extends Player> players = Bukkit.getOnlinePlayers();
                for (Player p : players) {
                    p.sendMessage(singleAction.substring(14));
                }
            } else if (CheckPluginLoad.DoIt("MythicMobs") && singleAction.startsWith("mythicmobs_spawn: ")) {
                Bukkit.getScheduler().runTask(MythicTotem.instance, () -> {
                    try {
                        MythicMobsSpawn.DoIt(block, singleAction.substring(18).split(";;")[0], Integer.parseInt(singleAction.substring(18).split(";;")[1]));
                    }
                    catch (ArrayIndexOutOfBoundsException e) {
                        MythicMobsSpawn.DoIt(block, singleAction.substring(18).split(";;")[0], 1);
                    }
                });
            } else if (singleAction.startsWith("console_command: ")) {
                Bukkit.getScheduler().runTask(MythicTotem.instance, () -> {
                    DispatchCommand.DoIt(ReplacePlaceholder(singleAction.substring(17), player, block));
                });
            } else if (singleAction.startsWith("player_command: ") && player != null) {
                Bukkit.getScheduler().runTask(MythicTotem.instance, () -> {
                    DispatchCommand.DoIt(player, ReplacePlaceholder(singleAction.substring(16), player, block));
                });
            }
        }
    }
    private String ReplacePlaceholder(String str, Player player, Block block){
        try {
            return str.replace("%world%", block.getWorld().getName())
                    .replace("%block_x%", String.valueOf(block.getX()))
                    .replace("%block_y%", String.valueOf(block.getY()))
                    .replace("%block_z%", String.valueOf(block.getZ()))
                    .replace("%player_x%", String.valueOf(player.getLocation().getX()))
                    .replace("%player_y%", String.valueOf(player.getLocation().getY()))
                    .replace("%player_z%", String.valueOf(player.getLocation().getZ()))
                    .replace("%player_pitch%", String.valueOf(player.getLocation().getPitch()))
                    .replace("%player_yaw%", String.valueOf(player.getLocation().getYaw()))
                    .replace("%player%", player.getName());
        }
        catch (NullPointerException ep) {
            Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[MythicTotem] §cError: Some triggers can not get the player, " +
                    "please don't use placeholder that related to player!");
            return str.replace("%world%", block.getWorld().getName())
                    .replace("%block_x%", String.valueOf(block.getX()))
                    .replace("%block_y%", String.valueOf(block.getY()))
                    .replace("%block_z%", String.valueOf(block.getZ()));
        }
    }
}
