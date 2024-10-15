package cn.superiormc.mythictotem.managers;

import cn.superiormc.mythictotem.MythicTotem;
import cn.superiormc.mythictotem.utils.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

public class LanguageManager {

    public static LanguageManager languageManager;

    private YamlConfiguration messageFile;

    private YamlConfiguration tempMessageFile;

    private File file;

    private File tempFile;

    public LanguageManager() {
        languageManager = this;
        initLanguage();
    }

    public void initLanguage() {
        file = new File(MythicTotem.instance.getDataFolder() + "/languages/" + ConfigManager.configManager.getString("language", "en_US") + ".yml");
        if (!file.exists()){
            file = new File(MythicTotem.instance.getDataFolder(), "message.yml");
            if (!file.exists()) {
                Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[MythicTotem] §cWe can not found your message file, " +
                        "please try restart your server!");
            }
        }
        else {
            messageFile = YamlConfiguration.loadConfiguration(file);
        }
        InputStream is = MythicTotem.instance.getResource("languages/en_US.yml");
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

    public void sendStringText(CommandSender sender, String... args) {
        if (sender instanceof Player) {
            sendStringText((Player) sender, args);
        }
        else {
            sendStringText(args);
        }
    }

    public void sendStringText(String... args) {
        String text = this.messageFile.getString(args[0]);
        if (text == null) {
            if (this.tempMessageFile.getString(args[0]) == null) {
                Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[SpinToWin] §cCan not found language key: " + args[0] + "!");
                return;
            }
            else {
                Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[SpinToWin] §cUpdated your language file, added " +
                        "new language key and it's default value: " + args[0] + "!");
                text = this.tempMessageFile.getString(args[0]);
                messageFile.set(args[0], text);
                try {
                    messageFile.save(file);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        if (text == null) {
            return;
        }
        for (int i = 1 ; i < args.length ; i += 2) {
            String var = "%" + args[i] + "%";
            if (args[i + 1] == null) {
                text = text.replace(var, "");
            }
            else {
                text = text.replace(var, args[i + 1]);
            }
        }
        if (!text.isEmpty()) {
            Bukkit.getConsoleSender().sendMessage(TextUtil.parse(text));
        }
    }

    public void sendStringText(Player player, String... args) {
        String text = this.messageFile.getString(args[0]);
        if (text == null) {
            if (this.tempMessageFile.getString(args[0]) == null) {
                player.sendMessage("§x§9§8§F§B§9§8[SpinToWin] §cCan not found language key: " + args[0] + "!");
                return;
            }
            else {
                Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[SpinToWin] §cUpdated your language file, added " +
                        "new language key and it's default value: " + args[0] + "!");
                text = this.tempMessageFile.getString(args[0]);
                messageFile.set(args[0], text);
                try {
                    messageFile.save(file);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        if (text == null) {
            return;
        }
        for (int i = 1 ; i < args.length ; i += 2) {
            String var = "%" + args[i] + "%";
            if (args[i + 1] == null) {
                text = text.replace(var, "");
            }
            else {
                text = text.replace(var, args[i + 1]);
            }
        }
        if (!text.isEmpty()) {
            player.sendMessage(TextUtil.parse(text));
        }
    }

    public String getStringText(String... args) {
        String text = this.messageFile.getString(args[0]);
        if (text == null) {
            if (this.tempMessageFile.getString(args[0]) == null) {
                return "§cCan not found language key: " + args[0] + "!";
            }
            else {
                Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[SpinToWin] §cUpdated your language file, added " +
                        "new language key and it's default value: " + args[0] + "!");
                text = this.tempMessageFile.getString(args[0]);
                messageFile.set(args[0], text);
                try {
                    messageFile.save(file);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        if (text == null) {
            return "";
        }
        for (int i = 1 ; i < args.length ; i += 2) {
            String var = "%" + args[i] + "%";
            if (args[i + 1] == null) {
                text = text.replace(var, "");
            }
            else {
                text = text.replace(var, args[i + 1]);
            }
        }
        return TextUtil.parse(text);
    }
}
