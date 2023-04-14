package com.example.clientbackend;

public class Constants {

    /**
     * Email sender
     */
    public static final String EMAIL_FROM = "no-reply@company.com";
    public static final String MAIL_SENDER_USERNAME = "noreply.service182@gmail.com";
    public static final String MAIL_SENDER_HOST = "smtp.gmail.com";
    public static final Integer MAIL_SENDER_PORT = 587;
    public static final String MAIL_SENDER_PASSWORD = "unabzcfflvdpuyqz";
    public static final String TOKEN_CONFIRM_LINK_FORMAT = "%s:%s/api/v1/registration/confirm?token=%s";
    public static final String SERVICE_PORT = "8081";

    /**
     * Caching
     */
    public static final String USERS_COLLECTION = "usersCollection";

    /**
     * Kafka
     */
    public static final String FROM_CLIENT_TO_MESSAGE_SERVICE_TOPIC = "FROM_CLIENT_TO_MESSAGE_SERVICE_TOPIC";
}
