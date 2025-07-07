package ru.loper.suncore.commands.core.impl;

import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import ru.loper.suncore.api.command.SubCommand;
import ru.loper.suncore.api.config.CustomConfig;
import ru.loper.suncore.api.items.ItemBuilder;
import ru.loper.suncore.config.CoreConfigManager;
import ru.loper.suncore.utils.Colorize;

import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

public class SaveSubCommand implements SubCommand {
    private final CustomConfig itemsConfig;

    public SaveSubCommand(CoreConfigManager configManager) {
        itemsConfig = configManager.getItemsConfig();
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) return;
        if (args.length != 2) {
            sender.sendMessage(Colorize.parse("&c ▶ &fИспользование: /suncore save [название]"));
            return;
        }

        ItemStack item = player.getInventory().getItemInMainHand();
        if (item.getType().equals(Material.AIR)) {
            sender.sendMessage(Colorize.parse("&c ▶ &fВы не можете сохранить воздух]"));
            return;
        }

        ConfigurationSection section = itemsConfig.getConfig().createSection("items." + args[1]);
        ItemBuilder itemBuilder = new ItemBuilder(item);
        itemBuilder.save(section);
        itemsConfig.saveConfig();
    }

    @Override
    public List<String> onTabCompleter(CommandSender sender, String[] args) {
        if (args.length == 2) {
            return Stream.of("<название>")
                    .filter(s -> s.toLowerCase().startsWith(args[1].toLowerCase()))
                    .toList();
        }
        return Collections.emptyList();
    }
}
