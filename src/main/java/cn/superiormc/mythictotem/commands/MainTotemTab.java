package cn.superiormc.mythictotem.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.List;

public class MainTotemTab implements TabCompleter {
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1)
        {
            if(sender.hasPermission("mythictotem.admin")) {
                List<String> strings = new ArrayList();
                strings.add("reload");
                strings.add("help");
                strings.add("list");
                return strings;
            }
            else{
                List<String> strings = new ArrayList();
                strings.add("help");
                return strings;
            }
        }
        return null;
    }

}
