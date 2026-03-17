package cn.superiormc.mythictotem.commands;

import cn.superiormc.mythictotem.listeners.BlockIdInspectListener;
import cn.superiormc.mythictotem.managers.LanguageManager;
import org.bukkit.entity.Player;

public class SubBlockId extends AbstractCommand {

    public SubBlockId() {
        this.id = "blockid";
        this.onlyInGame = true;
        this.requiredPermission = "mythictotem.admin";
        this.requiredArgLength = new Integer[]{1};
    }

    @Override
    public void executeCommandInGame(String[] args, Player player) {
        BlockIdInspectListener.startInspect(player);
        LanguageManager.languageManager.sendStringText(player, "block-id-prompt");
    }
}
