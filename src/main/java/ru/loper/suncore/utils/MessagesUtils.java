package ru.loper.suncore.utils;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

public class MessagesUtils {

    private MessagesUtils() {
    }

    public static void sendMessage(Player player, String message) {
        player.sendMessage(Colorize.parse(message));
    }

    public static void sendMessage(String player, String message) {
        Player p = Bukkit.getPlayerExact(player);
        if (p == null)
            return;
        p.sendMessage(Colorize.parse(message));
    }

    public static void sendMessage(CommandSender player, String message) {
        player.sendMessage(Colorize.parse(message));
    }

    public static void sendTitle(Player player, String title, String subtitle, int fadeIn, int stay, int fadeOut) {
        player.sendTitle(Colorize.parse(title),
                Colorize.parse(subtitle), fadeIn, stay, fadeOut);
    }

    public static void sendTitle(String player, String title, String subtitle, int fadeIn, int stay, int fadeOut) {
        Player p = Bukkit.getPlayerExact(player);
        if (p == null)
            return;
        p.sendTitle(Colorize.parse(title),
                Colorize.parse(subtitle), fadeIn, stay, fadeOut);
    }

    public static void sendActionBar(Player player, String message) {
        player.sendActionBar(Colorize.parse(message));
    }

    public static void sendActionBar(String player, String message) {
        Player p = Bukkit.getPlayerExact(player);
        if (p == null)
            return;
        p.sendActionBar(Colorize.parse(message));
    }

    public static void broadcast(String message) {
        Bukkit.broadcastMessage(Colorize.parse(message));
    }

    public static void broadcast(List<String> messages) {
        messages.forEach(MessagesUtils::broadcast);
    }

    public static void broadcast(String... messages) {
        Arrays.stream(messages).forEach(MessagesUtils::broadcast);
    }

}
