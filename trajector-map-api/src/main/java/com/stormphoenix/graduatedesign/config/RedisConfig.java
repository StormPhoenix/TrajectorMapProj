package com.stormphoenix.graduatedesign.config;

import com.stormphoenix.graduatedesign.redis.RedisKeyTimeRegion;
import com.stormphoenix.graduatedesign.redis.RedisValueTrajectorPathList;
import com.stormphoenix.graduatedesign.redis.RedisObjectSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * Created by Developer on 18-5-25.
 */
@Configuration
public class RedisConfig {
    @Bean
    JedisConnectionFactory jedisConnectionFactory() {
        return new JedisConnectionFactory();
    }

    @Bean
    public RedisTemplate<RedisKeyTimeRegion, RedisValueTrajectorPathList> redisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<RedisKeyTimeRegion, RedisValueTrajectorPathList> template = new RedisTemplate();
        template.setConnectionFactory(factory);
//        template.setConnectionFactory(jedisConnectionFactory());
        template.setKeySerializer(new RedisObjectSerializer());
        template.setValueSerializer(new RedisObjectSerializer());
        return template;
    }
}
