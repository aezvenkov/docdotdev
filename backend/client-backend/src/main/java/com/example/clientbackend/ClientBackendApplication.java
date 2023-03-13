package com.example.clientbackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class })
public class ClientBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(ClientBackendApplication.class, args);
    }

}
