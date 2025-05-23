package ru.loper.suncore.utils;

import net.md_5.bungee.api.ChatColor;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class Colorize {
    private static final Pattern HEX_COLOR_PATTERN = Pattern.compile("&#([a-fA-F0-9]{6})");
    private static final char COLOR_CHAR = '&';
    private static final String HEX_PREFIX = "&x";

    private Colorize() {
    }

    public static String parse(String message) {
        if (message == null) {
            return null;
        }

        Matcher matcher = HEX_COLOR_PATTERN.matcher(message);
        StringBuilder builder = new StringBuilder();

        while (matcher.find()) {
            String hexColor = matcher.group(1);
            matcher.appendReplacement(builder, ChatColor.of("#" + hexColor).toString());
        }
        matcher.appendTail(builder);

        return ChatColor.translateAlternateColorCodes(COLOR_CHAR, builder.toString());
    }

    public static List<String> parse(List<String> messages) {
        if (messages == null) {
            return null;
        }

        List<String> mutableCopy = new ArrayList<>(messages);
        mutableCopy.replaceAll(Colorize::parse);
        return mutableCopy;
    }

    public static String convertHexToMinecraftColor(String hex) {
        if (hex == null) {
            throw new IllegalArgumentException("Hex-код цвета не может быть null");
        }

        String cleanHex = hex.startsWith("#") ? hex.substring(1) : hex;

        if (cleanHex.length() != 6) {
            throw new IllegalArgumentException("Неверный формат hex-кода цвета. Ожидается 6 символов, получено: " + cleanHex.length());
        }

        StringBuilder result = new StringBuilder(HEX_PREFIX);
        for (char c : cleanHex.toCharArray()) {
            result.append(COLOR_CHAR).append(c);
        }

        return result.toString();
    }

    public static String generateGradientString(String text, String startColorHex, String endColorHex) {
        if (text == null || startColorHex == null || endColorHex == null) {
            throw new IllegalArgumentException("Аргументы не могут быть null");
        }

        String cleanStart = startColorHex.startsWith("#") ? startColorHex.substring(1) : startColorHex;
        String cleanEnd = endColorHex.startsWith("#") ? endColorHex.substring(1) : endColorHex;

        if (cleanStart.length() != 6 || cleanEnd.length() != 6) {
            throw new IllegalArgumentException("Неверный формат HEX-кода цвета. Ожидается 6 символов.");
        }

        if (text.isEmpty()) {
            return text;
        }

        StringBuilder gradientString = new StringBuilder(text.length() * 15);
        int steps = text.length() - 1;

        for (int i = 0; i < text.length(); i++) {
            char currentChar = text.charAt(i);
            double ratio = steps > 0 ? (double) i / steps : 0;
            String interpolatedColorHex = interpolateColor(cleanStart, cleanEnd, ratio);
            gradientString.append(convertHexToMinecraftColor(interpolatedColorHex)).append(currentChar);
        }

        return gradientString.toString();
    }

    private static String interpolateColor(String startColorHex, String endColorHex, double ratio) {
        int startColor = Integer.parseInt(startColorHex, 16);
        int endColor = Integer.parseInt(endColorHex, 16);

        int red = interpolateComponent(startColor, endColor, 16, ratio);
        int green = interpolateComponent(startColor, endColor, 8, ratio);
        int blue = interpolateComponent(startColor, endColor, 0, ratio);

        return String.format("%02x%02x%02x", red, green, blue);
    }

    private static int interpolateComponent(int startColor, int endColor, int shift, double ratio) {
        int startComponent = (startColor >> shift) & 0xFF;
        int endComponent = (endColor >> shift) & 0xFF;
        return (int) (startComponent + ratio * (endComponent - startComponent));
    }
}