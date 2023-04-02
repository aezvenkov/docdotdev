package com.example.clientbackend.config;

import com.example.clientbackend.appuser.model.AppUserRole;
import com.example.clientbackend.token.jwt.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutHandler;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;

    private final AuthenticationProvider authenticationProvider;

    private final LogoutHandler logoutHandler;

    private static final String API_PREFIX = "/api/v1";


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf()
                    .disable()
                .authorizeHttpRequests()
                .requestMatchers(API_PREFIX + "/auth/**")
                    .permitAll()
                .requestMatchers(API_PREFIX + "/management/**")
                    .hasAuthority(AppUserRole.ADMIN.name())
                .requestMatchers(API_PREFIX + "/self_management/**")
                    .hasAnyAuthority(AppUserRole.ADMIN.name(), AppUserRole.USER.name())
                .anyRequest()
                    .authenticated()
                .and()
                .sessionManagement()
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authenticationProvider(authenticationProvider)
                    .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .logout()
                    .logoutUrl("/api/v1/authentication/logout")
                .addLogoutHandler(logoutHandler)
                    .logoutSuccessHandler((request, response, authentication) -> SecurityContextHolder.clearContext());

        return http.build();
    }
}
