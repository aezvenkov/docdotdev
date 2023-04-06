package com.example.messagebackend.config;

import com.example.messagebackend.logs.RedisLog;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisOperations;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {
    @Bean
    ReactiveRedisOperations<String, RedisLog> redisOperations(ReactiveRedisConnectionFactory factory) {
        Jackson2JsonRedisSerializer<RedisLog> serializer = new Jackson2JsonRedisSerializer<>(RedisLog.class);

        RedisSerializationContext.RedisSerializationContextBuilder<String, RedisLog> builder =
                RedisSerializationContext.newSerializationContext(new StringRedisSerializer());
        RedisSerializationContext<String, RedisLog> context = builder.value(serializer).build();

        return new ReactiveRedisTemplate<>(factory, context);
    }

}
