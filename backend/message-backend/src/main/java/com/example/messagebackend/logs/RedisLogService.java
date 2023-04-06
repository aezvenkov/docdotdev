package com.example.messagebackend.logs;

import jakarta.annotation.PostConstruct;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisOperations;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.UUID;

@Service
public class RedisLogService {

    private final ReactiveRedisOperations<String, RedisLog> coffeeOps;
    private final ReactiveRedisConnectionFactory factory;

    public RedisLogService(ReactiveRedisOperations<String, RedisLog> coffeeOps, ReactiveRedisConnectionFactory factory) {
        this.coffeeOps = coffeeOps;
        this.factory = factory;
    }

    public Flux<RedisLog> getAllLogs() {
        return coffeeOps.keys("*").flatMap(coffeeOps.opsForValue()::get);
    }

    @PostConstruct
    public void loadData() {
        factory.getReactiveConnection().serverCommands().flushAll().thenMany(
                        Flux.just("Jet Black Redis", "Darth Redis", "Black Alert Redis")
                                .map(name -> new RedisLog(UUID.randomUUID().toString(), null, name, null, null))
                                .flatMap(coffee -> coffeeOps.opsForValue().set(coffee.getId(), coffee)))
                .thenMany(coffeeOps.keys("*")
                        .flatMap(coffeeOps.opsForValue()::get))
                .subscribe(System.out::println);
    }

}
