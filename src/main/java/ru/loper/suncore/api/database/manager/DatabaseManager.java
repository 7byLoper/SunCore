package ru.loper.suncore.api.database.manager;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.Plugin;
import ru.loper.suncore.api.database.enums.DatabaseType;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public record DatabaseManager(Plugin plugin, String host, int port, String username, String password, String table,
                              String url, DatabaseType dataType) {
    public static DatabaseManager fromSection(ConfigurationSection section, Plugin plugin) {
        return fromSection(section, plugin, null);
    }

    public static DatabaseManager fromSection(ConfigurationSection section, Plugin plugin, DatabaseManager defaultManager) {
        if (section == null) {
            return defaultManager;
        }

        String host = section.getString("host", "");
        int port = section.getInt("port", 3306);

        String username = section.getString("username", "");
        String password = section.getString("password", "");
        String table = section.getString("name", "");

        DatabaseType databaseType = DatabaseType.getByName(section.getString("data_type", "MYSQL"));
        if (databaseType == null) databaseType = DatabaseType.SQLITE;

        String url = databaseType.generateUrl(host, port, new File(plugin.getDataFolder(), table + ".db").getAbsolutePath());

        return new DatabaseManager(plugin, host, port, username, password, table, url, databaseType);
    }

    public Connection getConnection() throws SQLException {
        return switch (dataType) {
            case MYSQL -> DriverManager.getConnection(url, username, password);
            case SQLITE -> DriverManager.getConnection(url);
        };
    }

    public String getSqlByDataType(String mysql, String sqlite) {
        if (dataType == DatabaseType.MYSQL) {
            return mysql;
        }

        return sqlite;
    }
}
