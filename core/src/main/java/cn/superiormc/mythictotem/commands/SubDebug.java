package cn.superiormc.mythictotem.commands;

import cn.superiormc.mythictotem.managers.ConfigManager;
import cn.superiormc.mythictotem.managers.LanguageManager;
import cn.superiormc.mythictotem.managers.TotemDebugManager;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class SubDebug extends AbstractCommand {

    public SubDebug() {
        this.id = "debug";
        this.onlyInGame = true;
        this.requiredPermission = "mythictotem.admin";
        this.requiredArgLength = new Integer[]{2};
    }

    @Override
    public void executeCommandInGame(String[] args, Player player) {
        if (args[1].equalsIgnoreCase("off")) {
            TotemDebugManager.totemDebugManager.stopDebug(player);
            LanguageManager.languageManager.sendStringText(player, "totem-debug-stop");
            return;
        }

        if (!ConfigManager.configManager.getTotems().containsKey(args[1])) {
            LanguageManager.languageManager.sendStringText(player, "totem-debug-not-found", "totem", args[1]);
            return;
        }

        TotemDebugManager.totemDebugManager.startDebug(player, args[1]);
        LanguageManager.languageManager.sendStringText(player, "totem-debug-start", "totem", args[1]);
    }

    @Override
    public List<String> getTabResult(String[] args, Player player) {
        List<String> result = new ArrayList<>();
        if (args.length != 2) {
            return result;
        }

        result.add("off");
        result.addAll(ConfigManager.configManager.getTotems().keySet());
        return result;
    }
}
