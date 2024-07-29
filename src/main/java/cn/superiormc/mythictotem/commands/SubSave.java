package cn.superiormc.mythictotem.commands;

import cn.superiormc.mythictotem.configs.Messages;
import cn.superiormc.mythictotem.managers.SavedItemManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SubSave {

    public static void SubSaveCommand(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Messages.GetMessages("in-game"));
        } else if (sender.hasPermission("mythictotem.admin")) {
            if (args.length == 2) {
                SavedItemManager.SaveMainHandItem((Player) sender, args[1]);
                sender.sendMessage(Messages.GetMessages("saved"));
            }
            else {
                sender.sendMessage(Messages.GetMessages("error-args"));
            }
        } else {
            sender.sendMessage(Messages.GetMessages("error-miss-permission"));
        }
    }
}
