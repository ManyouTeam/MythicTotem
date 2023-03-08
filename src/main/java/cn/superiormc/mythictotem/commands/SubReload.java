package cn.superiormc.mythictotem.commands;

import cn.superiormc.mythictotem.MythicTotem;
import cn.superiormc.mythictotem.configs.Messages;
import cn.superiormc.mythictotem.configs.TotemConfigs;
import org.bukkit.command.CommandSender;

public class SubReload {

    public static void SubReloadCommand(CommandSender sender)
    {
        if(sender.hasPermission("mythictotem.admin")) {
            MythicTotem.getTotemMaterial.clear();
            MythicTotem.getTotemMap.clear();
            MythicTotem.instance.reloadConfig();
            TotemConfigs.GetTotemConfigs();
            sender.sendMessage(Messages.GetMessages("plugin-reloaded"));
        }
        else{
            sender.sendMessage(Messages.GetMessages("error-miss-permission"));
        }
    }

}
