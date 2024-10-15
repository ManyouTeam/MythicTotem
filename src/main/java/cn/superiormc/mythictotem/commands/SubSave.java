package cn.superiormc.mythictotem.commands;

import cn.superiormc.mythictotem.managers.ItemManager;
import cn.superiormc.mythictotem.managers.LanguageManager;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class SubSave extends AbstractCommand {

    public SubSave() {
        this.id = "save";
        this.requiredPermission =  "mythictotem.admin";
        this.onlyInGame = true;
        this.requiredArgLength = new Integer[]{2};
        this.premiumOnly = true;
    }

    @Override
    public void executeCommandInGame(String[] args, Player player) {
        ItemManager.itemManager.saveMainHandItem(player, args[1]);
        LanguageManager.languageManager.sendStringText(player, "plugin.saved");
    }

    @Override
    public List<String> getTabResult(String[] args) {
        List<String> tempVal1 = new ArrayList<>();
        switch (args.length) {
            case 2:
                tempVal1.add(LanguageManager.languageManager.getStringText("command-tab.type-item-id"));
                break;
        }
        return tempVal1;
    }
}
