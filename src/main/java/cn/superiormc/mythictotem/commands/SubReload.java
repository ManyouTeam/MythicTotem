package cn.superiormc.mythictotem.commands;

import cn.superiormc.mythictotem.MythicTotem;
import cn.superiormc.mythictotem.managers.ConfigManager;
import cn.superiormc.mythictotem.managers.ItemManager;
import cn.superiormc.mythictotem.managers.LanguageManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class SubReload extends AbstractCommand {

    public SubReload() {
        this.id = "reload";
        this.requiredPermission =  "mythictotem.reload";
        this.onlyInGame = false;
        this.requiredArgLength = new Integer[]{1};
    }

    @Override
    public void executeCommandInGame(String[] args, Player player) {
        MythicTotem.instance.reloadConfig();
        new ConfigManager();
        new ItemManager();
        LanguageManager.languageManager.sendStringText(player, "plugin-reloaded");
    }

    @Override
    public void executeCommandInConsole(String[] args) {
        MythicTotem.instance.reloadConfig();
        new ConfigManager();
        new ItemManager();
        LanguageManager.languageManager.sendStringText("plugin-reloaded");
    }
}
