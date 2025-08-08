package ru.loper.suncore.config;

import lombok.Getter;
import org.bukkit.configuration.ConfigurationSection;

@Getter
public class RedisConfig {
    private final String host;
    private final int port;
    private final String password;
    private final long timeout;

    public RedisConfig(ConfigurationSection section) {
        host = section.getString("host", "127.0.0.1");
        port = section.getInt("port", 6379);
        password = section.getString("password", "");
        timeout = section.getLong("timeout", 3000);
    }
}
