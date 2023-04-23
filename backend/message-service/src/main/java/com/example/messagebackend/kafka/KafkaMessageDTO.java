package com.example.messagebackend.kafka;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class KafkaMessageDTO {

    private Date date;

    private MessageType type;

    private String payload;

    public KafkaMessageDTO(MessageType type, String payload) {
        this.type = type;
        this.payload = payload;
        date = new Date();
    }

}
