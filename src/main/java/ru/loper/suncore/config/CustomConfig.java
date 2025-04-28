package ru.loper.suncore.config;

import lombok.Getter;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import ru.loper.suncore.SunCore;
import ru.loper.suncore.utils.Colorize;

import java.io.File;
import java.io.IOException;

@Getter
public class CustomConfig {
    private final FileConfiguration config;
    private final File file;
    private final String name;
    private final boolean dataConfig;


    public CustomConfig(String name) {
        this(name, false, SunCore.getInstance());
    }

    public CustomConfig(String name, boolean data) {
        this(name, data, SunCore.getInstance());
    }

    public CustomConfig(@NonNull String name, boolean dataConfig, @NonNull Plugin plugin) {
        this.name = name.replace(".yml", "");
        name = name.endsWith(".yml") ? name : name + ".yml";
        this.dataConfig = dataConfig;
        file = new File(plugin.getDataFolder(), name);
        if (!file.exists()) {
            try {
                if (!dataConfig) plugin.saveResource(name, true);
                else {
                    if (file.createNewFile()) {
                        Bukkit.getLogger().info("Конфиг " + file.getName() + " успешно подгружен");
                    }
                }
            } catch (IOException | IllegalArgumentException e) {
                Bukkit.getLogger().severe("Ошибка загрузки конфига. " + e.getMessage());
            }
        }
        config = YamlConfiguration.loadConfiguration(file);
    }

    public CustomConfig(@NonNull File file) {
        dataConfig = false;
        name = file.getName();
        this.file = file;
        config = YamlConfiguration.loadConfiguration(file);
    }

    public void saveConfig() {
        try {
            config.save(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void reloadConfig() {
        try {
            config.load(file);
        } catch (IOException | org.bukkit.configuration.InvalidConfigurationException e) {
            Bukkit.getLogger().warning(name + " не найден!");
        }
    }

    public String configMessage(String path) {
        String message = config.getString(path, "unknown");
        return Colorize.parse(message);
    }

}
