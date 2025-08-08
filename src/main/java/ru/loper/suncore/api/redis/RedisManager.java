package ru.loper.suncore.api.redis;

import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.pubsub.StatefulRedisPubSubConnection;
import lombok.Getter;
import ru.loper.suncore.config.RedisConfig;

import java.util.HashSet;
import java.util.Set;

@Getter
public class RedisManager {
    private static final Set<RedisManager> instances = new HashSet<>();

    private final RedisClient redisClient;
    private final StatefulRedisConnection<String, String> connection;
    private final StatefulRedisPubSubConnection<String, String> pubSubConnection;

    public RedisManager(RedisConfig config, RedisMessageListener listener, String... channels) {
        String redisPassword = config.getPassword().isEmpty() ? "" : config.getPassword() + "@";
        String redisUrl = "redis://" + redisPassword + config.getHost() + ":" + config.getPort();

        redisClient = RedisClient.create(redisUrl);
        connection = redisClient.connect();
        pubSubConnection = redisClient.connectPubSub();

        pubSubConnection.addListener(listener);

        pubSubConnection.async().subscribe(channels);
        instances.add(this);
    }

    public void onDisable() {
        if (redisClient != null) {
            redisClient.shutdown();
        }

        instances.remove(this);
    }

    public void sendCommand(String channel, String message) {
        connection.sync().publish(channel, message);
    }

    public static void disableCore() {
        instances.forEach(RedisManager::onDisable);
    }
}
