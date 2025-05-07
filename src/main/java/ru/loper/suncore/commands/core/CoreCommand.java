package ru.loper.suncore.commands.core;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permission;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.loper.suncore.api.command.SunCommandExecutor;
import ru.loper.suncore.commands.core.impl.GiveSubCommand;
import ru.loper.suncore.utils.PluginConfigManager;

import java.util.Collections;
import java.util.List;

public class CoreCommand extends SunCommandExecutor {

    private final PluginConfigManager configManager;

    public CoreCommand(PluginConfigManager configManager) {
        this.configManager = configManager;
        addSubCommand(new GiveSubCommand(configManager), new Permission("suncore.give"), "give");
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 0) {
            return true;
        }

        SubCommandWrapper subCommand = getCommandByLabel(args[0]);
        if (subCommand == null) {
            return true;
        }

        if (!sender.hasPermission(subCommand.getPermission())) {
            sender.sendMessage(configManager.getNoPermissionMessage());
            return true;
        }

        subCommand.getCommand().onCommand(sender, args);
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 0) {
            return Collections.emptyList();
        }

        if (args.length == 1) {
            return getFilteredSubCommandAliases(args[0]);
        }

        SubCommandWrapper subCommand = getCommandByLabel(args[0]);
        return subCommand == null || !sender.hasPermission(subCommand.getPermission())
                ? Collections.emptyList()
                : subCommand.getCommand().onTabCompleter(sender, args);
    }
}