package ru.loper.suncore.utils;

import net.md_5.bungee.api.ChatColor;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Colorize {
    private static final Pattern pattern = Pattern.compile("&#[a-fA-F0-9]{6}");

    public static String parse(String message) {
        if (message == null) return null;

        Matcher matcher = pattern.matcher(message);

        while (matcher.find()) {
            String color = message.substring(matcher.start() + 1, matcher.end());
            message = message.replace("&" + color, ChatColor.of(color) + "");
            matcher = pattern.matcher(message);
        }

        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public static List<String> parse(List<String> messages) {
        messages.replaceAll(Colorize::parse);
        return messages;
    }
}
