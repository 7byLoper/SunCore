package ru.loper.suncore.commands.core.impl;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import ru.loper.suncore.api.command.SubCommand;
import ru.loper.suncore.api.config.CustomConfig;
import ru.loper.suncore.api.items.ItemBuilder;
import ru.loper.suncore.config.CoreConfigManager;
import ru.loper.suncore.utils.Colorize;

import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

public class GiveSubCommand implements SubCommand {
    private final CustomConfig itemsConfig;

    public GiveSubCommand(CoreConfigManager configManager) {
        itemsConfig = configManager.getItemsConfig();
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(Colorize.parse("&c ▶ &fИспользование: /suncore give [custom item] [player] [amount]"));
            return;
        }
        ConfigurationSection itemsSection = getItemsSection();
        if (itemsSection == null) {
            sender.sendMessage(Colorize.parse("&c ▶ &fОшибка получения предметов из конфига"));
            return;
        }

        ConfigurationSection itemSection = itemsSection.getConfigurationSection(args[1]);
        if (itemSection == null) {
            sender.sendMessage(Colorize.parse("&c ▶ &fДанного предмета не существует"));
            return;
        }

        Player player = resolveTargetPlayer(sender, args);
        if (player == null) return;

        int amount = resolveAmount(args);
        if (amount <= 0) {
            sender.sendMessage(Colorize.parse("&c ▶ &fНекорректное количество предметов"));
        }

        ItemBuilder builder = ItemBuilder.fromConfig(itemSection);
        player.getInventory().addItem(builder.amount(amount).build());
        sender.sendMessage(Colorize.parse(String.format(
                "&a ▶ &fВыдан предмет &e%s &fигроку &e%s &fв количестве &e%d",
                itemSection.getName(), player.getName(), amount
        )));

    }

    private Player resolveTargetPlayer(CommandSender sender, String[] args) {
        if (args.length < 3) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(Colorize.parse("&cДанная команда доступна только игрокам"));
                return null;
            }
            return (Player) sender;
        }

        Player player = Bukkit.getPlayer(args[2]);
        if (player == null) {
            sender.sendMessage(Colorize.parse("&c ▶ &fУказанный игрок не найден или не в сети"));
            return null;
        }
        return player;
    }

    private int resolveAmount(String[] args) {
        if (args.length < 4) return 1;

        try {
            return Math.max(1, Integer.parseInt(args[3]));
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private ConfigurationSection getItemsSection() {
        return itemsConfig.getConfig().getConfigurationSection("items");
    }


    @Override
    public List<String> onTabCompleter(CommandSender sender, String[] args) {
        if (args.length == 2) {
            return getItemsSection().getKeys(false).stream()
                    .filter(s -> s.toLowerCase().startsWith(args[1].toLowerCase()))
                    .toList();
        }

        if (args.length == 3) {
            return Bukkit.getOnlinePlayers().stream()
                    .map(HumanEntity::getName)
                    .filter(s -> s.toLowerCase().startsWith(args[2].toLowerCase()))
                    .toList();
        }

        if (args.length == 4) {
            return Stream.of("1", "8", "16", "32", "64")
                    .filter(s -> s.toLowerCase().startsWith(args[3].toLowerCase()))
                    .toList();
        }

        return Collections.emptyList();
    }
}
