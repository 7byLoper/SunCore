package ru.loper.suncore.config;

import lombok.Getter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.Plugin;
import ru.loper.suncore.api.config.ConfigManager;
import ru.loper.suncore.api.config.CustomConfig;
import ru.loper.suncore.api.database.DataBaseManager;

@Getter
public class CoreConfigManager extends ConfigManager {
    private boolean itemsTagHider;
    private String noPermissionMessage;
    private DataBaseManager dataBaseManager;

    public CoreConfigManager(Plugin plugin) {
        super(plugin);
    }

    @Override
    public void loadConfigs() {
        plugin.saveDefaultConfig();
        addCustomConfig(new CustomConfig("messages.yml", plugin));
        addCustomConfig(new CustomConfig("items.yml", plugin));
    }

    @Override
    public void loadValues() {
        itemsTagHider = plugin.getConfig().getBoolean("items_tag_hider", true);
        noPermissionMessage = getMessagesConfig().configMessage("no_permission");

        ConfigurationSection databaseSection = plugin.getConfig().getConfigurationSection("database");
        if (databaseSection != null) {
            dataBaseManager = new DataBaseManager(databaseSection, plugin);
        }
    }

    public CustomConfig getMessagesConfig() {
        return getCustomConfig("messages.yml");
    }

    public CustomConfig getItemsConfig() {
        return getCustomConfig("items.yml");
    }
}
