package com.example.clientbackend.config;

import com.example.clientbackend.appuser.AppUserRepository;
import com.example.clientbackend.appuser.AppUserService;
import com.example.clientbackend.appuser.model.AppUser;
import com.example.clientbackend.appuser.model.AppUserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

@Configuration
@RequiredArgsConstructor
public class ApplicationConfig {

    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    private final AppUserService appUserService;

    private final AppUserRepository repository;

    @Bean
    public UserDetailsService userDetailsService() {
        return username -> appUserService.getUserByEmail(username).orElseThrow(() ->
                new UsernameNotFoundException("User not found"));
    }

//    @Override
//    @Bean
//    protected UserDetailsService userDetailsService() {
//        AppUser adminUser = new AppUser(
//                "admin",
//                "admin",
//                "admin@admin.com",
//                bCryptPasswordEncoder.encode("secret"),
//                AppUserRole.ADMIN
//        );
//        adminUser.setEnabled(true);
//        appUserRepository.save(adminUser);
//
//        return new InMemoryUserDetailsManager(adminUser);
//    }

    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider() {
        DaoAuthenticationProvider provider =
                new DaoAuthenticationProvider();
        provider.setPasswordEncoder(bCryptPasswordEncoder);
        provider.setUserDetailsService(appUserService);
        return provider;
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService());
        authProvider.setPasswordEncoder(bCryptPasswordEncoder);
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

}
