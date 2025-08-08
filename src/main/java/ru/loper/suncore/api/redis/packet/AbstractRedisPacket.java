package ru.loper.suncore.api.redis.packet;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

public abstract class AbstractRedisPacket implements RedisPacket {
    @Getter
    @Setter(AccessLevel.PROTECTED)
    private String source;

    public AbstractRedisPacket(String source) {
        this.source = source;
        if (source != null) {
            read();
        }
    }
}
