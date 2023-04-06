package com.example.clientbackend.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

import static com.example.clientbackend.Constants.FROM_CLIENT_TO_MESSAGE_SERVICE_TOPIC;


@Configuration
public class KafkaTopicConfig {
    @Bean
    public NewTopic docServiceTopic() {
        return TopicBuilder.name(FROM_CLIENT_TO_MESSAGE_SERVICE_TOPIC)
                .build();
    }
}
