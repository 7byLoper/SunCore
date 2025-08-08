package ru.loper.suncore.api.redis;

import io.lettuce.core.pubsub.RedisPubSubListener;

public abstract class RedisMessageListener implements RedisPubSubListener<String, String> {

    @Override
    public void message(String s, String k1, String s2) {
        //empty
    }

    @Override
    public void subscribed(String s, long l) {
        //empty
    }

    @Override
    public void psubscribed(String s, long l) {
        //empty
    }

    @Override
    public void unsubscribed(String s, long l) {
        //empty
    }

    @Override
    public void punsubscribed(String s, long l) {
        //empty
    }
}
