package ru.loper.suncore.api.redis.packet;

public interface RedisPacket {
    void read();

    void write();
}
