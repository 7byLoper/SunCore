package ru.loper.suncore.api.config;

import lombok.Getter;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;

public class ConfigsManager {
    private final Plugin plugin;
    private final HashMap<String, CustomConfig> customConfigs = new HashMap<>();
    @Getter
    private boolean breakBlockStats;

    public ConfigsManager(Plugin plugin) {
        this.plugin = plugin;
        addCustomConfig(new CustomConfig("messages"));
        loadValues();
    }

    public void loadValues() {
        breakBlockStats = plugin.getConfig().getBoolean("break_blocks_stats", false);
    }

    public void addCustomConfig(CustomConfig config) {
        customConfigs.put(config.getName(), config);
        plugin.getLogger().info("Конфиг " + config.getName() + " инициализирован");
    }

    public CustomConfig getCustomConfig(String name) {
        return customConfigs.get(name);
    }

    public void reloadAll() {
        if (customConfigs.isEmpty()) return;
        customConfigs.values().forEach(CustomConfig::reloadConfig);
        loadValues();
    }

    public void saveAll() {
        if (customConfigs.isEmpty()) return;
        customConfigs.values().forEach(CustomConfig::saveConfig);
    }
}
