package com.example.messagebackend.logs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@RedisHash("redis_logs")
public class RedisLog {

    @Id
    private String id;

    private Date date;

    private String title;

    private String message;

    private String payload;

}
