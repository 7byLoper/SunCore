package ru.loper.suncore.config;

import lombok.Getter;
import ru.loper.suncore.SunCore;

import java.util.HashMap;

public class ConfigsManager {
    private final static SunCore plugin = SunCore.getInstance();
    private final static HashMap<String, CustomConfig> customConfigs = new HashMap<>();
    @Getter
    private static boolean breakBlockStats;

    public void init() {
        addCustomConfig(new CustomConfig("messages"));
        loadValues();
    }

    public static void loadValues(){
        breakBlockStats = plugin.getConfig().getBoolean("break_blocks_stats",false);
    }

    public static void addCustomConfig(CustomConfig config) {
        customConfigs.put(config.getName(), config);
        plugin.getLogger().info("Конфиг " + config.getName() + " инициализирован");
    }

    public static void reloadAll() {
        if (customConfigs.isEmpty()) return;
        customConfigs.values().forEach(CustomConfig::reloadConfig);
        loadValues();
    }

    public static void saveAll() {
        if (customConfigs.isEmpty()) return;
        customConfigs.values().forEach(CustomConfig::saveConfig);
    }
}
