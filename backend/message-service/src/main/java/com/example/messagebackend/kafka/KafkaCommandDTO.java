package com.example.messagebackend.kafka;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class KafkaCommandDTO {

    private Date date;

    private CommandType type;

    private String payload;

    public KafkaCommandDTO(CommandType type, String payload) {
        this.type = type;
        this.payload = payload;
        date = new Date();
    }

}
