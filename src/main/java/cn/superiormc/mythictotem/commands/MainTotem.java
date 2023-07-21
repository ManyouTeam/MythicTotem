package cn.superiormc.mythictotem.commands;

import cn.superiormc.mythictotem.configs.Messages;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class MainTotem implements CommandExecutor {

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            command.setUsage(null);
            sender.sendMessage(Messages.GetMessages("error-args"));
            return false;
        } else {
            SendCommandArg(sender, command, label, args);
            return true;
        }
    }

    public void SendCommandArg(CommandSender sender, Command command, String label, String[] args){
        if (args[0].equals("help")) {
            SubHelp.SubHelpCommand(sender);
        }
        else if (args[0].equals("list")) {
            SubList.SubListCommand(sender);
        }
        else if (args[0].equals("reload")) {
            SubReload.SubReloadCommand(sender);
        }
        else if (args[0].equals("save")) {
            SubSave.SubSaveCommand(sender, args);
        }
        else
        {
            sender.sendMessage(Messages.GetMessages("error-args"));
        }
    }

}
