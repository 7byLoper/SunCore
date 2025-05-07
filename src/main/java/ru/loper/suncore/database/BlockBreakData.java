package ru.loper.suncore.database;

import ru.loper.suncore.SunCore;

import java.sql.*;

public class BlockBreakData {

    private final SunCore plugin;
    private Connection connection;

    public BlockBreakData(SunCore plugin) {
        this.plugin = plugin;
    }

    public void connectToDatabase() {
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:plugins/" + plugin.getName() + "/blocks_break.db");
            plugin.getLogger().info("Подключение к базе данных SQLite выполнено!");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void createTable() {
        String sunsSQL = "CREATE TABLE IF NOT EXISTS blocks_break (" +
                "player TEXT PRIMARY KEY," +
                "amount INTEGER DEFAULT 0" +
                ");";

        try (PreparedStatement stmt = connection.prepareStatement(sunsSQL)) {
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void closeDatabase() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                plugin.getLogger().info("Подключение к базе данных закрыто.");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public int getBlocks(String player) {
        String sql = "SELECT amount FROM blocks_break WHERE player = ?;";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, player.toLowerCase());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("amount");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return 0;
    }

    public void setBlocks(String player, int blocks) {
        String sql = "INSERT INTO blocks_break (player, amount) VALUES (?, ?) " +
                "ON CONFLICT(player) DO UPDATE SET amount = ?;";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, player.toLowerCase());
            stmt.setInt(2, blocks);
            stmt.setInt(3, blocks);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void removeBlocks(String player, int amount) {
        setBlocks(player, Math.max((getBlocks(player) - amount), 0));
    }

    public void addBlocks(String player, int amount) {
        setBlocks(player, getBlocks(player) + amount);
    }
}
