package ru.loper.suncore.api.database;

import lombok.Getter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@Getter
public class DataBaseManager {
    private final String userName, password, host, url, table;
    private final int port;
    private final DataBaseType dataType;
    private final Plugin plugin;

    public DataBaseManager(@NotNull ConfigurationSection databaseSection, Plugin plugin) {
        userName = databaseSection.getString("username", "");
        password = databaseSection.getString("password", "");
        host = databaseSection.getString("host", "");
        port = databaseSection.getInt("port");
        table = databaseSection.getString("name", "");
        dataType = getDataTypeFromString(databaseSection.getString("data_type", "MYSQL"));
        this.plugin = plugin;
        url = getUrl();
    }

    private String getUrl() {
        switch (dataType) {
            case MYSQL -> {
                return "jdbc:mysql://" + host + ":" + port + "/" + table + "?useSSL=false&autoReconnect=true";
            }
            case SQLITE -> {
                File databaseFile = new File(plugin.getDataFolder(), table + ".db");
                return "jdbc:sqlite:" + databaseFile.getAbsolutePath();
            }
        }

        return "";
    }

    private DataBaseType getDataTypeFromString(@NotNull String type) {
        try {
            return DataBaseType.valueOf(type.toUpperCase());
        } catch (IllegalArgumentException e) {
            return DataBaseType.SQLITE;
        }
    }

    public Connection getConnection() throws SQLException {
        switch (dataType) {
            case MYSQL -> {
                return DriverManager.getConnection(url, userName, password);
            }
            case SQLITE -> {
                return DriverManager.getConnection(url);
            }
        }

        return null;
    }

    public String getSqlByDataType(String mysql, String sqlite) {
        if (getDataType().equals(DataBaseType.MYSQL)) {
            return mysql;
        }

        return sqlite;
    }
}
