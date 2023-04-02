package com.example.clientbackend.appuser.model;

import graphql.com.google.common.collect.Sets;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Set;
import java.util.stream.Collectors;

import static com.example.clientbackend.appuser.model.AppUserPermissions.*;

public enum AppUserRole {
    USER(Sets.newHashSet(APP_USER_READ, APP_USER_WRITE,
            CONFIRMATION_TOKEN_READ, CONFIRMATION_TOKEN_WRITE)),
    ADMIN(Sets.newHashSet(APP_USER_READ, APP_USER_WRITE,
            CONFIRMATION_TOKEN_READ, CONFIRMATION_TOKEN_WRITE,
            APP_ADMIN_READ, APP_ADMIN_WRITE));

    private final Set<AppUserPermissions> permissions;

    AppUserRole(Set<AppUserPermissions> permissions) {
        this.permissions = permissions;
    }

    public Set<AppUserPermissions> getPermissions() {
        return permissions;
    }

    public Set<SimpleGrantedAuthority> getGrantedAuthorities() {
        Set<SimpleGrantedAuthority> permissions = getPermissions().stream()
                .map(permission -> new SimpleGrantedAuthority(permission.getPermission()))
                .collect(Collectors.toSet());
        permissions.add(new SimpleGrantedAuthority("ROLE_" + this.name()));
        return permissions;
    }
}
