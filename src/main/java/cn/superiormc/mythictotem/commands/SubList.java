package cn.superiormc.mythictotem.commands;

import cn.superiormc.mythictotem.MythicTotem;
import cn.superiormc.mythictotem.configs.Messages;
import cn.superiormc.mythictotem.configs.TotemConfigs;
import org.bukkit.command.CommandSender;

public class SubList {

    public static void SubListCommand(CommandSender sender) {
        if (sender.hasPermission("mythictotem.admin")) {
            sender.sendMessage(Messages.GetMessages("list-head"));
            for (String totemID : MythicTotem.getTotemMap.keySet()) {
                sender.sendMessage(Messages.GetMessages("list-prefix") + totemID);
            }
        }
        else {
            sender.sendMessage(Messages.GetMessages("error-miss-permission"));
        }
    }
}
