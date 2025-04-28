package ru.loper.suncore.utils;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.loper.suncore.SunCore;

public class Placeholder extends PlaceholderExpansion {

    private final SunCore plugin;

    public Placeholder(SunCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "suncore";
    }

    @Override
    public @NotNull String getAuthor() {
        return "Loper";
    }

    @Override
    public @NotNull String getVersion() {
        return plugin.getDescription().getVersion();
    }

    @Override
    public @Nullable String onPlaceholderRequest(Player player, @NotNull String params) {
        String[] args = params.split("_");
        if (args.length == 0) return "?";
        //%suncore_break_blocks%
        if (args[0].equalsIgnoreCase("break")) {
            if (args.length < 2 || !args[1].equalsIgnoreCase("blocks")) return "?";
            if (player == null) return "Player not found";
            return String.valueOf(plugin.breakBlocksData.getBlocks(player.getName()));
        }

        return "?";
    }
}
