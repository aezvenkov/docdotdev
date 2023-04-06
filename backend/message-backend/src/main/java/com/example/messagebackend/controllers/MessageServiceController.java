package com.example.messagebackend.controllers;

import com.example.messagebackend.logs.RedisLog;
import com.example.messagebackend.logs.RedisLogService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping(path = "/api/v1/message")
@AllArgsConstructor
public class MessageServiceController {

    private final RedisLogService logService;

    @GetMapping(path = "logs")
    public Flux<RedisLog> getAllLogs() {
        return logService.getAllLogs();
    }

}
