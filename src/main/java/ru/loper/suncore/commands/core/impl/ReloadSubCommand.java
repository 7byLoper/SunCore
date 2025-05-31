package ru.loper.suncore.commands.core.impl;

import lombok.RequiredArgsConstructor;
import org.bukkit.command.CommandSender;
import ru.loper.suncore.api.command.SubCommand;
import ru.loper.suncore.utils.Colorize;
import ru.loper.suncore.config.PluginConfigManager;

import java.util.List;

@RequiredArgsConstructor
public class ReloadSubCommand implements SubCommand {
    private final PluginConfigManager configManager;

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        long start = System.currentTimeMillis();
        configManager.reloadAll();
        long stop = System.currentTimeMillis();
        sender.sendMessage(Colorize.parse("&a ▶ &fКонфигурация успешно перезагрузилась за %d мс".formatted(stop - start)));
    }

    @Override
    public List<String> onTabCompleter(CommandSender sender, String[] args) {
        return List.of();
    }
}
