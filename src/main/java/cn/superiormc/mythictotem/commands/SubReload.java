package cn.superiormc.mythictotem.commands;

import cn.superiormc.mythictotem.MythicTotem;
import cn.superiormc.mythictotem.configs.Messages;
import cn.superiormc.mythictotem.configs.TotemConfigs;
import cn.superiormc.mythictotem.managers.SavedItemManager;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

public class SubReload {

    public static void SubReloadCommand(CommandSender sender)
    {
        if (sender.hasPermission("mythictotem.admin")) {
            MythicTotem.getTotemMaterial.clear();
            MythicTotem.getTotemMap.clear();
            MythicTotem.getCheckingPlayer.clear();
            MythicTotem.getCheckingBlock.clear();
            MythicTotem.threeDtotemAmount = 0;
            MythicTotem.instance.reloadConfig();
            TotemConfigs.initTotemConfigs();
            SavedItemManager.ReadSavedItems();
            sender.sendMessage(Messages.GetMessages("plugin-reloaded"));
        }
        else {
            sender.sendMessage(Messages.GetMessages("error-miss-permission"));
        }
    }

}
