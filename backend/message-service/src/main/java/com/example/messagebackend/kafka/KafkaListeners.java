package com.example.messagebackend.kafka;

import com.example.messagebackend.gcm.GcmService;
import com.example.messagebackend.logs.RedisLogService;
import com.google.gson.Gson;
import lombok.AllArgsConstructor;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Objects;

import static com.example.messagebackend.Constants.FROM_CLIENT_TO_MESSAGE_SERVICE_TOPIC;
import static com.example.messagebackend.kafka.MessageType.SEND_PUSH_NOTIFICATION;

@Component
@AllArgsConstructor
public class KafkaListeners {

    private final RedisLogService logService;

    private final GcmService gcmService;

    @KafkaListener(
            topics = FROM_CLIENT_TO_MESSAGE_SERVICE_TOPIC,
            groupId = "groupId"
    )
    void listener(String data) {
        System.out.printf("Kafka listener received: %s%n", data);
        Gson gson = new Gson();

        KafkaMessageDTO command = gson.fromJson(data, KafkaMessageDTO.class);
        consumeClientMessage(command);
    }

    private void consumeClientMessage(KafkaMessageDTO command) {
        try {
            logService.saveLog(command);

            if (Objects.equals(SEND_PUSH_NOTIFICATION, command.getType())) {
                JSONObject jsonObject = new JSONObject(command.getPayload());
                JSONArray deviceTokensArray = jsonObject.getJSONArray("device_tokens");

                String[] deviceTokens = new String[deviceTokensArray.length()];
                for (int i = 0; i < deviceTokensArray.length(); i++) {
                    deviceTokens[i] = deviceTokensArray.getString(i);
                }
                String title = jsonObject.getString("title");
                String body = jsonObject.getString("body");

                for (String token: deviceTokens) {
                    gcmService.sendNotification(token, title, body);
                }
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}
