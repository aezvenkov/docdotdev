package com.example.clientbackend.kafka;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class KafkaMessage {

    private Date date;

    private MessageType type;

    private String payload;

    public KafkaMessage(MessageType type, String payload) {
        this.type = type;
        this.payload = payload;
        date = new Date();
    }

}
