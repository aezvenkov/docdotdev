package com.example.clientbackend.kafka;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class KafkaCommand {

    private Date date;

    private CommandType type;

    private String payload;

    public KafkaCommand(CommandType type, String payload) {
        this.type = type;
        this.payload = payload;
        date = new Date();
    }

}
