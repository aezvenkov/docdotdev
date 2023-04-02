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
     * JWT authentication
     */
    public static final String PARAM_USERNAME = "username";
    public static final String PARAM_PASSWORD = "password";
    public static final String ALGORITHM_SECRET = "secret";
    public static final String CLAIM_ROLES = "roles";
    public static final String HEADER_BEARER = "Bearer ";
}
