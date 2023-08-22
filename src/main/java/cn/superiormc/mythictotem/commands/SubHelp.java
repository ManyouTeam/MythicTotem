package cn.superiormc.mythictotem.commands;

import cn.superiormc.mythictotem.configs.Messages;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;

public class SubHelp {

    public static void SubHelpCommand(CommandSender sender) {
        if (!(sender instanceof Player)){
            sender.sendMessage(Messages.GetMessages("help-main-console"));
        } else if (sender.hasPermission("mythictotem.admin")) {
            sender.sendMessage(Messages.GetMessages("help-main-admin"));
        } else {
            sender.sendMessage(Messages.GetMessages("help-main"));
        }
    }
}
