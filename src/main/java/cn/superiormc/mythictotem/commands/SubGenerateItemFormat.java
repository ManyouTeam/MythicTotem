package cn.superiormc.mythictotem.commands;

import cn.superiormc.mythictotem.MythicTotem;
import cn.superiormc.mythictotem.configs.Messages;
import cn.superiormc.mythictotem.methods.DebuildItem;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class SubGenerateItemFormat {

    public static void SubGenerateItemFormatCommand(CommandSender sender) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Messages.GetMessages("in-game"));
            return;
        }
        Player player = (Player) sender;
        YamlConfiguration itemConfig = new YamlConfiguration();
        DebuildItem.debuildItem(player.getInventory().getItemInMainHand(), itemConfig);
        String yaml = itemConfig.saveToString();
        Bukkit.getScheduler().runTaskAsynchronously(MythicTotem.instance,() -> {
            Path path = new File(MythicTotem.instance.getDataFolder(), "generated-item-format.yml").toPath();
            try {
                Files.write(path, yaml.getBytes(StandardCharsets.UTF_8));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        Messages.GetMessages("generated");
    }
}
