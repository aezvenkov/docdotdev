package com.example.messagebackend.logs;

import com.example.messagebackend.kafka.KafkaCommandDTO;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisOperations;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.UUID;

@Service
@AllArgsConstructor
public class RedisLogService {

    private final ReactiveRedisOperations<String, RedisLog> coffeeOps;

    private final ReactiveRedisConnectionFactory factory;


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

    public void saveLog(KafkaCommandDTO commandDTO) {
        String id = UUID.randomUUID().toString();

        factory.getReactiveConnection().serverCommands().flushAll().thenMany(
                        Flux.just(commandDTO)
                                .map(command -> new RedisLog(id, null, null, null, command.getPayload()))
                                .flatMap(coffee -> coffeeOps.opsForValue().set(coffee.getId(), coffee)))
                .thenMany(coffeeOps.keys("*")
                        .flatMap(coffeeOps.opsForValue()::get))
                .subscribe(System.out::println);
    }

}
