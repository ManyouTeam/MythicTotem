package cn.superiormc.mythictotem.managers;

import cn.superiormc.mythictotem.utils.TextUtil;
import cn.superiormc.mythictotem.MythicTotem;
import cn.superiormc.mythictotem.objects.checks.type.BlockChecker;
import cn.superiormc.mythictotem.objects.checks.type.impl.*;

import java.util.HashMap;
import java.util.Map;

public class BlockCheckManager {

    public static BlockCheckManager blockCheckManager;
    private final Map<String, BlockChecker> checkers;

    public BlockCheckManager() {
        blockCheckManager = this;
        this.checkers = new HashMap<>();
        registerDefaultCheckers();
    }

    private void registerDefaultCheckers() {
        registerChecker(new MinecraftBlockChecker());
        registerChecker(new ItemsAdderBlockChecker());
        registerChecker(new ItemsAdderFurnitureChecker());
        registerChecker(new ItemsAdderMobChecker());
        registerChecker(new OraxenBlockChecker());
        registerChecker(new OraxenFurnitureChecker());
        registerChecker(new MMOItemsBlockChecker());
        registerChecker(new NexoBlockChecker());
        registerChecker(new CraftEngineBlockChecker());
    }

    public void registerChecker(BlockChecker checker) {
        checkers.put(checker.getClass().getSimpleName(), checker);
        TextUtil.sendMessage(null, TextUtil.pluginPrefix() + " §fLoaded block checker: " + checker.getClass().getSimpleName() + "!");
    }

    public BlockChecker getChecker(String name) {
        return checkers.get(name);
    }

    public BlockChecker getSuitableChecker(String materialString) {
        for (BlockChecker checker : checkers.values()) {
            if (checker.canCheck(materialString)) {
                return checker;
            }
        }
        return null;
    }
}