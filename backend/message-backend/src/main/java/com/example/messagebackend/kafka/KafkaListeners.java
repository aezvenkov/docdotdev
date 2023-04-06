package com.example.messagebackend.kafka;

import com.google.gson.Gson;
import lombok.AllArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import static com.example.messagebackend.Constants.FROM_CLIENT_TO_MESSAGE_SERVICE_TOPIC;

@Component
@AllArgsConstructor
public class KafkaListeners {

    @KafkaListener(
            topics = FROM_CLIENT_TO_MESSAGE_SERVICE_TOPIC,
            groupId = "groupId"
    )
    void listener(String data) {
        System.out.printf("Kafka listener received: %s%n", data);
        Gson gson = new Gson();

        KafkaCommandDTO command = gson.fromJson(data, KafkaCommandDTO.class);
        consumeClientMessage(command);
    }

    private void consumeClientMessage(KafkaCommandDTO command) {
        try {
//            if (Objects.equals(KafkaCommands.REMOVE_DOCS.string, command.command)) {
//                //passportService.deletePassport(command.id);
//            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}
