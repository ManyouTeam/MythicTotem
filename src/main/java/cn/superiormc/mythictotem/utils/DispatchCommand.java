package cn.superiormc.mythictotem.utils;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class DispatchCommand {

    public static void DoIt(String command){
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
    }

    public static void DoIt(Player player, String command){
        Bukkit.dispatchCommand(player, command);
    }
}
