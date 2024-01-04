package cn.superiormc.mythictotem.configs;

import cn.superiormc.mythictotem.MythicTotem;
import cn.superiormc.mythictotem.utils.ColorParser;
import cn.superiormc.mythictotem.utils.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class Messages {

    private static YamlConfiguration messageFile;

    private static YamlConfiguration tempMessageFile;

    private static File file;

    private static File tempFile;

    public static void initLanguage() {
        file = new File(MythicTotem.instance.getDataFolder(), "message.yml");
        if (!file.exists()){
            Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[MythicTotem] §cWe can not found your message file, " +
                    "please try restart your server!");
        }
        else {
            messageFile = YamlConfiguration.loadConfiguration(file);
        }
        InputStream is = MythicTotem.instance.getResource("message.yml");
        if (is == null) {
            return;
        }
        tempFile = new File(MythicTotem.instance.getDataFolder(), "tempMessage.yml");
        try {
            Files.copy(is, tempFile.toPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
        tempMessageFile = YamlConfiguration.loadConfiguration(tempFile);
        tempFile.delete();
    }

    public static String GetMessages(String path) {
        if (messageFile.getString(path) == null) {
            if (tempMessageFile.getString(path) == null) {
                return "§cCan not found language key: " + path + "!";
            }
            Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[MythicTotem] §cUpdated your language file, added " +
                    "new language key and it's default value: " + path + "!");
            messageFile.set(path, tempMessageFile.getString(path));
            try {
                messageFile.save(file);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return TextUtil.parse(tempMessageFile.getString(path));
        }
        return TextUtil.parse(messageFile.getString(path));
    }
}
