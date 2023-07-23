package cn.superiormc.mythictotem.managers;

import cn.superiormc.mythictotem.MythicTotem;
import cn.superiormc.mythictotem.configs.Messages;
import cn.superiormc.mythictotem.utils.*;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ActionManager {

    private final List<String> action;

    private final Player player;

    private final Block block;

    private final PlacedBlockCheckManager totem;

    private final Location startLocation;

    public ActionManager(Location startLocation, PlacedBlockCheckManager totem, List<String> action, Player player, Block block) {
        this.action = action;
        this.player = player;
        this.block = block;
        this.totem = totem;
        this.startLocation = startLocation;
    }

    public void CheckAction(){
        for(String singleAction : action) {
            if (singleAction.startsWith("none")) {
                return;
            } else if (singleAction.startsWith("message: ") && player != null) {
                player.sendMessage(ReplacePlaceholder(ColorParser.parse(singleAction.substring(9))));
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
                    DispatchCommand.DoIt(ReplacePlaceholder(singleAction.substring(17)));
                });
            } else if (singleAction.startsWith("player_command: ") && player != null) {
                Bukkit.getScheduler().runTask(MythicTotem.instance, () -> {
                    DispatchCommand.DoIt(player, ReplacePlaceholder(singleAction.substring(16)));
                });
            }
        }
    }
    private String ReplacePlaceholder(String str){
        if (Objects.nonNull(player)) {
                str = str.replace("%world%", block.getWorld().getName())
                        .replace("%block_x%", String.valueOf(block.getX()))
                        .replace("%block_y%", String.valueOf(block.getY()))
                        .replace("%block_z%", String.valueOf(block.getZ()))
                        .replace("%player_x%", String.valueOf(player.getLocation().getX()))
                        .replace("%player_y%", String.valueOf(player.getLocation().getY()))
                        .replace("%player_z%", String.valueOf(player.getLocation().getZ()))
                        .replace("%player_pitch%", String.valueOf(player.getLocation().getPitch()))
                        .replace("%player_yaw%", String.valueOf(player.getLocation().getYaw()))
                        .replace("%player%", player.getName())
                        .replace("%totem_start_x%", String.valueOf(startLocation.getX()))
                        .replace("%totem_start_y%", String.valueOf(startLocation.getY()))
                        .replace("%totem_start_z%", String.valueOf(startLocation.getZ()))
                        .replace("%totem_column%", String.valueOf(totem.GetColumn()))
                        .replace("%totem_raw%", String.valueOf(totem.GetRow()));
            if (CheckPluginLoad.DoIt("PlaceholderAPI")) {
                str = PlaceholderAPI.setPlaceholders(player, str);
            }
            return str;
        }
        else {
            Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[MythicTotem] §cError: Some triggers can not get the player, " +
                    "please don't use placeholder that related to player!");
            return str.replace("%world%", block.getWorld().getName())
                    .replace("%block_x%", String.valueOf(block.getX()))
                    .replace("%block_y%", String.valueOf(block.getY()))
                    .replace("%block_z%", String.valueOf(block.getZ()))
                    .replace("%totem_start_x%", String.valueOf(startLocation.getX()))
                    .replace("%totem_start_y%", String.valueOf(startLocation.getY()))
                    .replace("%totem_start_z%", String.valueOf(startLocation.getZ()))
                    .replace("%totem_column%", String.valueOf(totem.GetColumn()))
                    .replace("%totem_raw%", String.valueOf(totem.GetRow()));
        }
    }
}
