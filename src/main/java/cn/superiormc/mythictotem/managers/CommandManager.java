package cn.superiormc.mythictotem.managers;

import cn.superiormc.mythictotem.commands.*;
import org.bukkit.Bukkit;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class CommandManager {

    public static CommandManager commandManager;

    private final Map<String, AbstractCommand> registeredCommands = new HashMap<>();

    public CommandManager(){
        commandManager = this;
        registerBukkitCommands();
        registerObjectCommand();
    }

    private void registerBukkitCommands(){
        Objects.requireNonNull(Bukkit.getPluginCommand("mythictotem")).setExecutor(new MainCommand());
        Objects.requireNonNull(Bukkit.getPluginCommand("mythictotem")).setTabCompleter(new MainCommandTab());
    }

    private void registerObjectCommand() {
        registerNewSubCommand(new SubGenerateItemFormat());
        registerNewSubCommand(new SubHelp());
        registerNewSubCommand(new SubList());
        registerNewSubCommand(new SubSave());
        registerNewSubCommand(new SubReload());
        registerNewSubCommand(new SubGiveSaveItem());
    }

    public Map<String, AbstractCommand> getSubCommandsMap() {
        return registeredCommands;
    }

    public void registerNewSubCommand(AbstractCommand command) {
        registeredCommands.put(command.getId(), command);
    }

}
