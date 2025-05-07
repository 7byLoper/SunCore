package ru.loper.suncore.utils;

import lombok.Getter;
import org.bukkit.plugin.Plugin;
import ru.loper.suncore.api.config.ConfigManager;
import ru.loper.suncore.api.config.CustomConfig;

@Getter
public class PluginConfigManager extends ConfigManager {
    private boolean breakBlockStats;
    private String noPermissionMessage;

    public PluginConfigManager(Plugin plugin) {
        super(plugin);
    }

    @Override
    public void loadConfigs() {
        addCustomConfig(new CustomConfig("messages", plugin));
        addCustomConfig(new CustomConfig("items", plugin));
    }

    @Override
    public void loadValues() {
        breakBlockStats = plugin.getConfig().getBoolean("break_blocks_stats", false);
        noPermissionMessage = getCustomConfig("messages").configMessage("no_permission");
    }
}
