package ru.loper.suncore.api.command;

import org.bukkit.command.CommandSender;

import java.util.List;

public interface SubCommand {
    void onCommand(CommandSender sender, String[] args);

    List<String> onTabCompleter(CommandSender sender, String[] args);
}
