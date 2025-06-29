package cn.superiormc.mythictotem.commands;

import cn.superiormc.mythictotem.managers.ConfigManager;
import cn.superiormc.mythictotem.managers.LanguageManager;
import org.bukkit.entity.Player;

public class SubList extends AbstractCommand {

    public SubList() {
        this.id = "list";
        this.onlyInGame = false;
        this.requiredPermission = "mythictotem.admin";
        this.requiredArgLength = new Integer[]{1};
    }

    @Override
    public void executeCommandInGame(String[] args, Player player) {
        LanguageManager.languageManager.sendStringText(player, "list-head");
        for (String totemID : ConfigManager.configManager.getTotems().keySet()) {
            LanguageManager.languageManager.sendStringText(player, "list-content", "totem", totemID);
        }
    }

    @Override
    public void executeCommandInConsole(String[] args) {
        LanguageManager.languageManager.sendStringText("list-head");
        for (String totemID : ConfigManager.configManager.getTotems().keySet()) {
            LanguageManager.languageManager.sendStringText("list-content", "totem", totemID);
        }
    }

}
