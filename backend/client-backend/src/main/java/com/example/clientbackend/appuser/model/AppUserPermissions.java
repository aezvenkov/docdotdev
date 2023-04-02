package com.example.clientbackend.appuser.model;

public enum AppUserPermissions {
    APP_ADMIN_READ("app_admin:read"),
    APP_ADMIN_WRITE("app_admin:write"),
    APP_USER_READ("app_user:read"),
    APP_USER_WRITE("app_user:write"),
    CONFIRMATION_TOKEN_READ("confirmation_token:read"),
    CONFIRMATION_TOKEN_WRITE("confirmation_token:write");

    private final String permission;

    AppUserPermissions(String permission) {
        this.permission = permission;
    }

    public String getPermission() {
        return permission;
    }
}
