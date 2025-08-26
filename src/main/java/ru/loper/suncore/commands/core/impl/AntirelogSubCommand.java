package ru.loper.suncore.commands.core.impl;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.loper.suncore.api.command.SubCommand;
import ru.loper.suncore.api.hook.AntiRelogHook;
import ru.loper.suncore.utils.Colorize;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AntirelogSubCommand implements SubCommand {

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        if (!AntiRelogHook.isHooked()) {
            sender.sendMessage(Colorize.parse("&#FF0000▶ &fAntiRelog не подключен!"));
            return;
        }

        if (args.length == 1) {
            sendHelp(sender);
            return;
        }

        switch (args[1].toLowerCase()) {
            case "check" -> handleCheck(sender, args);
            case "start" -> handleStart(sender, args);
            case "stop" -> handleStop(sender, args);
            case "status" -> handleStatus(sender);
            case "list" -> handleList(sender);
            default -> sendHelp(sender);
        }
    }

    private void handleCheck(CommandSender sender, String[] args) {
        if (args.length < 3) {
            sender.sendMessage(Colorize.parse("&#FF0000▶ &fИспользование: &7/suncore antirelog check <игрок>"));
            return;
        }

        Player target = Bukkit.getPlayer(args[2]);
        if (target == null) {
            sender.sendMessage(Colorize.parse("&#FF0000▶ &fИгрок не найден!"));
            return;
        }

        boolean inPvp = AntiRelogHook.isPvp(target);
        boolean inSilentPvp = AntiRelogHook.isSilentPvp(target);
        int time = AntiRelogHook.getTimeRemainingInPvP(target);
        int silentTime = AntiRelogHook.getTimeRemainingInPvPSilent(target);

        sender.sendMessage(Colorize.parse("&#05A3FE▶ &fСтатус PvP для &e" + target.getName()));
        sender.sendMessage(Colorize.parse("&7 • Обычный PvP: " + (inPvp ? "&aДа &7(" + time + "s)" : "&cНет")));
        sender.sendMessage(Colorize.parse("&7 • Тихий PvP: " + (inSilentPvp ? "&aДа &7(" + silentTime + "s)" : "&cНет")));
        sender.sendMessage(Colorize.parse("&7 • Обход: " + (AntiRelogHook.hasBypassPermission(target) ? "&aДа" : "&cНет")));
    }

    private void handleStart(CommandSender sender, String[] args) {
        if (args.length < 3) {
            sender.sendMessage(Colorize.parse("&#FF0000▶ &fИспользование: /suncore antirelog start <игрок> [время]"));
            return;
        }

        Player target = Bukkit.getPlayer(args[2]);
        if (target == null) {
            sender.sendMessage(Colorize.parse("&#FF0000▶ &fИгрок не найден!"));
            return;
        }

        int time = AntiRelogHook.getPvpTime();
        if (args.length >= 4) {
            try {
                time = Integer.parseInt(args[3]);
            } catch (NumberFormatException e) {
                sender.sendMessage(Colorize.parse("&#FF0000▶ &fНеверное время!"));
                return;
            }
        }

        AntiRelogHook.startPvp(target);
        AntiRelogHook.updatePvpTime(target, time);

        sender.sendMessage(Colorize.parse("&#05A3FE▶ &fPvP запущен для &e" + target.getName() + " &fна &e" + time + "s"));
        if (sender != target) {
            target.sendMessage(Colorize.parse("&#05A3FE▶ &fPvP режим активирован на &e" + time + "s"));
        }
    }

    private void handleStop(CommandSender sender, String[] args) {
        if (args.length < 3) {
            sender.sendMessage(Colorize.parse("&#FF0000▶ &fИспользование: /suncore antirelog stop <игрок>"));
            return;
        }

        Player target = Bukkit.getPlayer(args[2]);
        if (target == null) {
            sender.sendMessage(Colorize.parse("&#FF0000▶ &fИгрок не найден!"));
            return;
        }

        AntiRelogHook.stopPvp(target);
        sender.sendMessage(Colorize.parse("&#05A3FE▶ &fPvP остановлен для &e" + target.getName()));
        if (sender != target) {
            target.sendMessage(Colorize.parse("&#05A3FE▶ &fPvP режим деактивирован"));
        }
    }

    private void handleStatus(CommandSender sender) {
        sender.sendMessage(Colorize.parse("&#05A3FE▶ &fСтатус AntiRelog хука:"));
        sender.sendMessage(Colorize.parse("&7 • Подключен: " + (AntiRelogHook.isHooked() ? "&aДа" : "&cНет")));
        if (AntiRelogHook.isHooked()) {
            sender.sendMessage(Colorize.parse("&7 • PvP время: &e" + AntiRelogHook.getPvpTime() + "s"));
            sender.sendMessage(Colorize.parse("&7 • Отключение PowerUps: " + (AntiRelogHook.isDisablePowerups() ? "&aДа" : "&cНет")));
            sender.sendMessage(Colorize.parse("&7 • Блокировка команд: " + (AntiRelogHook.isDisableCommandsInPvp() ? "&aДа" : "&cНет")));
        }
    }

    private void handleList(CommandSender sender) {
        List<Player> pvpPlayers = Bukkit.getOnlinePlayers().stream()
                .filter(AntiRelogHook::isPvp)
                .collect(Collectors.toList());

        List<Player> silentPvpPlayers = Bukkit.getOnlinePlayers().stream()
                .filter(AntiRelogHook::isSilentPvp)
                .collect(Collectors.toList());

        sender.sendMessage(Colorize.parse("&#05A3FE▶ &fИгроки в PvP:"));
        if (pvpPlayers.isEmpty() && silentPvpPlayers.isEmpty()) {
            sender.sendMessage(Colorize.parse("&7 • Нет активных PvP игроков"));
            return;
        }

        if (!pvpPlayers.isEmpty()) {
            sender.sendMessage(Colorize.parse("&7 • Обычный PvP:"));
            for (Player player : pvpPlayers) {
                int time = AntiRelogHook.getTimeRemainingInPvP(player);
                sender.sendMessage(Colorize.parse("&7   - &e" + player.getName() + " &7(" + time + "s)"));
            }
        }

        if (!silentPvpPlayers.isEmpty()) {
            sender.sendMessage(Colorize.parse("&7 • Тихий PvP:"));
            for (Player player : silentPvpPlayers) {
                int time = AntiRelogHook.getTimeRemainingInPvPSilent(player);
                sender.sendMessage(Colorize.parse("&7   - &e" + player.getName() + " &7(" + time + "s)"));
            }
        }
    }

    private void sendHelp(CommandSender sender) {
        sender.sendMessage(Colorize.parse("&#05A3FE▶ &fAntiRelog управление:"));
        sender.sendMessage(Colorize.parse("&7 • &f/suncore antirelog check <игрок> &8- Проверить статус"));
        sender.sendMessage(Colorize.parse("&7 • &f/suncore antirelog start <игрок> [время] &8- Запустить PvP"));
        sender.sendMessage(Colorize.parse("&7 • &f/suncore antirelog stop <игрок> &8- Остановить PvP"));
        sender.sendMessage(Colorize.parse("&7 • &f/suncore antirelog status &8- Статус хука"));
        sender.sendMessage(Colorize.parse("&7 • &f/suncore antirelog list &8- Список игроков в PvP"));
    }

    @Override
    public List<String> onTabCompleter(CommandSender sender, String[] args) {
        if (!AntiRelogHook.isHooked()) {
            return Collections.emptyList();
        }

        if (args.length == 2) {
            return Stream.of("check", "start", "stop", "status", "list")
                    .filter(s -> s.toLowerCase().startsWith(args[1].toLowerCase()))
                    .collect(Collectors.toList());
        }

        if (args.length == 3) {
            if (Arrays.asList("check", "start", "stop").contains(args[1].toLowerCase())) {
                return Bukkit.getOnlinePlayers().stream()
                        .map(Player::getName)
                        .filter(name -> name.toLowerCase().startsWith(args[2].toLowerCase()))
                        .collect(Collectors.toList());
            }
        }

        if (args.length == 4 && "start".equalsIgnoreCase(args[1])) {
            return Stream.of(String.valueOf(AntiRelogHook.getPvpTime()))
                    .filter(s -> s.toLowerCase().startsWith(args[3].toLowerCase()))
                    .collect(Collectors.toList());
        }

        return Collections.emptyList();
    }
}