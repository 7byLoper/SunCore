package ru.loper.suncore.database;

import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import ru.loper.suncore.api.database.DataBaseManager;
import ru.loper.suncore.api.database.DataBaseType;

import java.sql.*;
import java.util.UUID;
import java.util.logging.Level;

@RequiredArgsConstructor
public class BlockBreakDataBase {
    private final DataBaseManager dataBaseManager;
    private static final Object LOCK = new Object();

    public void createTable() {
        String createTableSql = dataBaseManager.getSqlByDataType(
                """
                        CREATE TABLE IF NOT EXISTS break_blocks (
                            uuid VARCHAR(36) PRIMARY KEY,
                            blocks INTEGER NOT NULL DEFAULT 0
                        )
                        """,
                """
                        CREATE TABLE IF NOT EXISTS break_blocks (
                            uuid TEXT PRIMARY KEY,
                            blocks INTEGER NOT NULL DEFAULT 0
                        )
                        """);

        try (Connection connection = dataBaseManager.getConnection();
             Statement statement = connection.createStatement()) {

            if (dataBaseManager.getDataType() == DataBaseType.SQLITE) {
                statement.execute("PRAGMA journal_mode=WAL;");
                statement.execute("PRAGMA busy_timeout=5000;");
            }

            statement.executeUpdate(createTableSql);

        } catch (SQLException exception) {
            Bukkit.getLogger().log(Level.SEVERE,
                    "Ошибка при создании таблицы для `break_blocks`", exception);
        }
    }

    public int getBreakBlocks(UUID uuid) {
        String sql = "SELECT blocks FROM break_blocks WHERE uuid = ?";

        try (Connection connection = dataBaseManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, uuid.toString());
            try (ResultSet rs = preparedStatement.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("blocks");
                }
            }

        } catch (SQLException exception) {
            Bukkit.getLogger().log(Level.SEVERE,
                    "Ошибка при получении сломанных блоков у игрока " + uuid, exception);
        }

        return 0;
    }

    public void setBreakBlocks(UUID player, int blocks) {
        synchronized (LOCK) {
            String sql = dataBaseManager.getSqlByDataType(
                    """
                            INSERT INTO break_blocks (uuid, blocks)
                            VALUES (?, ?)
                            ON DUPLICATE KEY UPDATE blocks = ?
                            """,
                    """
                            INSERT OR REPLACE INTO break_blocks (uuid, blocks)
                            VALUES (?, ?)
                            """
            );

            try (Connection conn = dataBaseManager.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {

                stmt.setString(1, player.toString());
                stmt.setInt(2, blocks);
                if (dataBaseManager.getDataType() == DataBaseType.MYSQL) {
                    stmt.setInt(3, blocks);
                }

                stmt.executeUpdate();
            } catch (SQLException e) {
                Bukkit.getLogger().log(Level.SEVERE,
                        "Ошибка при установке сломанных блоков для игрока " + player, e);
            }
        }
    }

    public void addBreakBlocks(UUID player, int blocks) {
        synchronized (LOCK) {
            String sql = dataBaseManager.getSqlByDataType(
                    """
                            INSERT INTO break_blocks (uuid, blocks)
                            VALUES (?, ?)
                            ON DUPLICATE KEY UPDATE blocks = blocks + ?
                            """,
                    """
                            INSERT INTO break_blocks (uuid, blocks)
                            VALUES (?, ?)
                            ON CONFLICT(uuid) DO UPDATE SET blocks = blocks + ?
                            """
            );

            try (Connection conn = dataBaseManager.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {

                stmt.setString(1, player.toString());
                stmt.setInt(2, blocks);
                stmt.setInt(3, blocks);

                stmt.executeUpdate();
            } catch (SQLException e) {
                Bukkit.getLogger().log(Level.SEVERE,
                        "Ошибка при добавлении сломанных блоков игроку " + player, e);
            }
        }
    }
}