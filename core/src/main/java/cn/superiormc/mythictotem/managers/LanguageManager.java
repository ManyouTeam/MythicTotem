package cn.superiormc.mythictotem.managers;

import cn.superiormc.mythictotem.MythicTotem;
import cn.superiormc.mythictotem.utils.TextUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class LanguageManager {

    public static LanguageManager languageManager;

    private String serverLanguage = null;

    private final Map<String, YamlConfiguration> languageFiles = new HashMap<>();

    private final Map<String, YamlConfiguration> bundledFiles = new HashMap<>();

    private YamlConfiguration tempMessageFile;

    public LanguageManager() {
        languageManager = this;
        initLanguages();
    }

    public String getPlayerLanguage(Player player) {
        if (player == null) {
            return serverLanguage.toLowerCase();
        }
        try {
            if (ConfigManager.configManager.getBoolean("config-files.per-player-language", true) && !MythicTotem.freeVersion) {
                return player.getLocale().toLowerCase();
            } else {
                return serverLanguage.toLowerCase();
            }
        } catch (NoSuchMethodError | NoClassDefFoundError e) {
            return serverLanguage.toLowerCase();
        }
    }

    private void initLanguages() {
        File langFolder = new File(MythicTotem.instance.getDataFolder(), "languages");
        if (!langFolder.exists()) {
            langFolder.mkdirs();
        }

        serverLanguage = ConfigManager.configManager.getString("config-files.language", "en-US");
        tempMessageFile = loadInternalLanguage(serverLanguage);
        if (tempMessageFile == null) {
            tempMessageFile = new YamlConfiguration();
        }
        YamlConfiguration fullLanguageFile = loadInternalLanguage("en_US");
        if (fullLanguageFile != null) {
            mergeMissingKeys(tempMessageFile, fullLanguageFile);
        }

        File[] files = langFolder.listFiles((dir, name) -> name.endsWith(".yml"));
        if (files != null) {
            for (File file : files) {
                String lang = file.getName().replace(".yml", "");
                YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
                languageFiles.put(lang.toLowerCase(), config);
                TextUtil.sendMessage(null, TextUtil.pluginPrefix() + " §fLoaded language: " + lang + ".yml!");
            }
        }

        if (!languageFiles.containsKey("en_US".toLowerCase())) {
            languageFiles.put("en_US".toLowerCase(), tempMessageFile);
        }
        if (!languageFiles.containsKey(serverLanguage.toLowerCase())) {
            ErrorManager.errorManager.sendErrorMessage("§cError: Can not found language file: " + serverLanguage + ".yml at languages folder!");
            serverLanguage = "en_US";
        }
        TextUtil.sendMessage(null, TextUtil.pluginPrefix() + " §fDefault language being set to: " + serverLanguage + ".yml!");
    }

    private String getMessage(Player player, String key, String... args) {
        String lang = getPlayerLanguage(player);

        YamlConfiguration config = languageFiles.getOrDefault(lang, languageFiles.getOrDefault(serverLanguage.toLowerCase(), tempMessageFile));
        String text = config.getString(key);

        if (text == null) {
            String bundled = bundledLanguage(lang).getString(key);
            if (bundled != null || tempMessageFile.getString(key) != null) {
                text = bundled != null ? bundled : tempMessageFile.getString(key);
                config.set(key, text);
                saveLanguageFile(lang, config);
                TextUtil.sendMessage(null, TextUtil.pluginPrefix() + " §cAdded new language key: " + key + " for " + lang);
            } else {
                if (args.length == 0) {
                    text = "§cLanguage key not found: " + key;
                } else {
                    text = args[0];
                }
            }
            if (text == null) {
                text = "§cLanguage key not found: " + key;
            }
        }

        for (int i = 1 ; i < args.length ; i += 2) {
            String var = "{" + args[i] + "}";
            if (args[i + 1] == null) {
                text = text.replace(var, "");
            } else {
                text = text.replace(var, args[i + 1]);
            }
        }
        text = text.replace("{plugin_folder}", String.valueOf(MythicTotem.instance.getDataFolder()));
        return text;
    }

    private void saveLanguageFile(String lang, YamlConfiguration config) {
        File file = new File(MythicTotem.instance.getDataFolder(), "languages/" + lang + ".yml");
        try {
            config.save(file);
        } catch (IOException e) {
            ErrorManager.errorManager.sendErrorMessage("§cError: Could not save language file " + file.getName() + ": " + e.getMessage());
        }
    }

    public void sendStringText(CommandSender sender, String... args) {
        if (sender instanceof Player player) {
            sendStringText(player, args);
        } else {
            sendStringText(null, args);
        }
    }

    public void sendStringText(String... args) {
        sendStringText(null, args);
    }

    public void sendStringText(Player player, String... args) {
        if (args.length == 0) {
            return;
        }
        String text = getMessage(player, args[0]);

        // 替换变量 {key}
        for (int i = 1; i < args.length; i += 2) {
            String var = "{" + args[i] + "}";
            text = text.replace(var, i + 1 < args.length ? (args[i + 1] == null ? "" : args[i + 1]) : "");
        }

        if (!text.isEmpty()) {
            TextUtil.sendMessage(player, text);
        }
    }

    public String getStringText(Player player, String path, String... args) {
        return getMessage(player, path, args);
    }

    public List<String> getStringListText(Player player, String path) {
        String lang = getPlayerLanguage(player);
        YamlConfiguration config = languageFiles.getOrDefault(lang, tempMessageFile);

        List<String> list = config.getStringList(path);
        if (list.isEmpty()) {
            List<String> temp = bundledLanguage(lang).getStringList(path);
            if (temp.isEmpty()) {
                temp = tempMessageFile.getStringList(path);
            }
            if (!temp.isEmpty()) {
                config.set(path, temp);
                saveLanguageFile(lang, config);
                return temp;
            } else {
                temp.add("§cLanguage key not found: " + path);
                return temp;
            }
        }
        return list;
    }

    private YamlConfiguration bundledLanguage(String language) {
        return bundledFiles.computeIfAbsent(language, key -> {
            String[] parts = key.replace('-', '_').split("_", 2);
            String resource = parts.length == 2 ? parts[0] + "_" + parts[1].toUpperCase(Locale.ROOT) : key;
            YamlConfiguration loaded = loadInternalLanguage(resource);
            return loaded == null ? new YamlConfiguration() : loaded;
        });
    }

    private YamlConfiguration loadInternalLanguage(String language) {
        if (language == null) {
            return null;
        }

        String resourceName = language.replace('-', '_');
        InputStream inputStream = MythicTotem.instance.getResource("languages/" + resourceName + ".yml");
        if (inputStream == null) {
            return null;
        }

        try (InputStreamReader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8)) {
            return YamlConfiguration.loadConfiguration(reader);
        } catch (IOException e) {
            ErrorManager.errorManager.sendErrorMessage("§cError: Could not load internal language " + language + ": " + e.getMessage());
            return null;
        }
    }

    private void mergeMissingKeys(YamlConfiguration target, YamlConfiguration source) {
        for (String key : source.getKeys(true)) {
            if (source.isConfigurationSection(key)) {
                continue;
            }
            if (target.get(key) == null) {
                target.set(key, source.get(key));
            }
        }
    }
}