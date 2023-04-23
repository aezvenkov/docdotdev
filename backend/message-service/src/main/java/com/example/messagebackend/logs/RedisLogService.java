package com.example.messagebackend.logs;

import com.example.messagebackend.kafka.KafkaMessageDTO;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class RedisLogService {

    private final RedisLogRepository redisLogRepository;

    public List<RedisLog> getAllLogs() {
        List<RedisLog> redisLogs = new ArrayList<>();
        redisLogRepository.findAll().forEach(redisLogs::add);

        return redisLogs;
    }

    public void saveLog(KafkaMessageDTO command) {
        String id = UUID.randomUUID().toString();
        RedisLog redisLog = new RedisLog(id, command.getDate(), command.getType().toString(), null, command.getPayload());

        redisLogRepository.save(redisLog);
    }

}
