package cn.superiormc.mythictotem.managers;

import cn.superiormc.mythictotem.MythicTotem;
import cn.superiormc.mythictotem.utils.*;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

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
            } else if (singleAction.startsWith("effect: ") && player != null) {
                try {
                    if (PotionEffectType.getByName(singleAction.substring(8).split(";;")[0].toUpperCase()) == null) {
                        MythicTotem.checkError("§x§9§8§F§B§9§8[MythicTotem] §cError: Can not found potion effect: " +
                                singleAction.split(";;")[0] + ".");
                    }
                    PotionEffect effect = new PotionEffect(PotionEffectType.getByName(singleAction.split(";;")[0].toUpperCase()),
                            Integer.parseInt(singleAction.substring(8).split(";;")[2]),
                            Integer.parseInt(singleAction.substring(8).split(";;")[1]) - 1,
                            true,
                            true,
                            true);
                    player.addPotionEffect(effect);
                }
                catch (ArrayIndexOutOfBoundsException e) {
                    MythicTotem.checkError("§x§9§8§F§B§9§8[MythicTotem] §cError: Your effect action in totem configs can not being correctly load.");
                }
            } else if (singleAction.startsWith("teleport: ") && player != null) {
                try {
                    if (singleAction.split(";;").length == 4) {
                        Location loc = new Location(Bukkit.getWorld(singleAction.substring(10).split(";;")[0]),
                                Double.parseDouble(singleAction.substring(10).split(";;")[1]),
                                Double.parseDouble(singleAction.substring(10).split(";;")[2]),
                                Double.parseDouble(singleAction.substring(10).split(";;")[3]),
                                player.getLocation().getYaw(),
                                player.getLocation().getPitch());
                        player.teleport(loc);
                    }
                    else if (singleAction.split(";;").length == 6) {
                        Location loc = new Location(Bukkit.getWorld(singleAction.split(";;")[0]),
                                Double.parseDouble(singleAction.substring(10).split(";;")[1]),
                                Double.parseDouble(singleAction.substring(10).split(";;")[2]),
                                Double.parseDouble(singleAction.substring(10).split(";;")[3]),
                                Float.parseFloat(singleAction.substring(10).split(";;")[4]),
                                Float.parseFloat(singleAction.substring(10).split(";;")[5]));
                        player.teleport(loc);
                    }
                    else {
                        MythicTotem.checkError("§x§9§8§F§B§9§8[MythicTotem] §cError: Your teleport action in totem configs can not being correctly load.");
                    }
                }
                catch (ArrayIndexOutOfBoundsException e) {
                    MythicTotem.checkError("§x§9§8§F§B§9§8[MythicTotem] §cError: Your teleport action in totem configs can not being correctly load.");
                }
            } else if (CheckPluginLoad.DoIt("MythicMobs") && singleAction.startsWith("mythicmobs_spawn: ")) {
                Bukkit.getScheduler().runTask(MythicTotem.instance, () -> {
                    try {
                        if (singleAction.substring(18).split(";;").length == 1) {
                            MythicMobsSpawn.DoIt(block.getLocation(),
                                    singleAction.substring(18).split(";;")[0],
                                    1);
                        }
                        else if (singleAction.substring(18).split(";;").length == 2) {
                            MythicMobsSpawn.DoIt(block.getLocation(),
                                    singleAction.substring(18).split(";;")[0],
                                    Integer.parseInt(singleAction.substring(18).split(";;")[1]));
                        }
                        else if (singleAction.substring(18).split(";;").length == 5) {
                            World world = Bukkit.getWorld(singleAction.substring(18).split(";;")[1]);
                            Location location = new Location(world,
                                    Double.parseDouble(singleAction.substring(18).split(";;")[2]),
                                    Double.parseDouble(singleAction.substring(18).split(";;")[3]),
                                    Double.parseDouble(singleAction.substring(18).split(";;")[4])
                                    );
                            MythicMobsSpawn.DoIt(location,
                                    singleAction.substring(18).split(";;")[0],
                                    1);
                        }
                        else if (singleAction.substring(18).split(";;").length == 6) {
                            World world = Bukkit.getWorld(singleAction.substring(18).split(";;")[2]);
                            Location location = new Location(world,
                                    Double.parseDouble(singleAction.substring(18).split(";;")[3]),
                                    Double.parseDouble(singleAction.substring(18).split(";;")[4]),
                                    Double.parseDouble(singleAction.substring(18).split(";;")[5])
                            );
                            MythicMobsSpawn.DoIt(location,
                                    singleAction.substring(18).split(";;")[0],
                                    Integer.parseInt(singleAction.substring(18).split(";;")[1]));
                        }
                        else {
                            MythicTotem.checkError("§x§9§8§F§B§9§8[MythicTotem] §cError: Your mythicmobs_spawn action in totem configs can not being correctly load.");
                        }
                    }
                    catch (ArrayIndexOutOfBoundsException e) {
                        MythicTotem.checkError("§x§9§8§F§B§9§8[MythicTotem] §cError: Your mythicmobs_spawn action in totem configs can not being correctly load.");
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
