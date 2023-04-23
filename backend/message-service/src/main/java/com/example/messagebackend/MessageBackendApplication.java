package com.example.messagebackend;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.InputStream;

@SpringBootApplication
public class MessageBackendApplication {

    public static void main(String[] args) throws IOException {
        ClassPathResource resource = new ClassPathResource("service_account.json");
        InputStream inputStream = resource.getInputStream();
        FirebaseOptions options = new FirebaseOptions.Builder()
                .setCredentials(GoogleCredentials.fromStream(inputStream))
                .build();
        FirebaseApp.initializeApp(options);

        SpringApplication.run(MessageBackendApplication.class, args);
    }

}
