package com.example.clientbackend.config;

import com.example.clientbackend.appuser.AppUserRepository;
import com.example.clientbackend.appuser.AppUserService;
import com.example.clientbackend.appuser.model.AppUser;
import com.example.clientbackend.appuser.model.AppUserRole;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

@Configuration
@AllArgsConstructor
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private static final String API_PREFIX = "/api/v*";

    private final AppUserService appUserService;

    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    private final AppUserRepository appUserRepository;


    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider() {
        DaoAuthenticationProvider provider =
                new DaoAuthenticationProvider();
        provider.setPasswordEncoder(bCryptPasswordEncoder);
        provider.setUserDetailsService(appUserService);
        return provider;
    }

    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(daoAuthenticationProvider());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http
                .csrf().disable()
                .authorizeRequests()
                .antMatchers(API_PREFIX + "/registration/**", "/api/login/**", "/api/token_controller/**", "/", "index", "/css/*", "/js/*").permitAll()
                .antMatchers(API_PREFIX + "/management/**").hasAuthority(AppUserRole.ADMIN.name())
                .antMatchers(API_PREFIX + "/self_management/**").hasAnyAuthority(AppUserRole.ADMIN.name(), AppUserRole.USER.name())
                .anyRequest().authenticated();
    }

    @Override
    @Bean
    protected UserDetailsService userDetailsService() {
        AppUser adminUser = new AppUser(
                "admin",
                "admin",
                "admin@admin.com",
                bCryptPasswordEncoder.encode("secret"),
                AppUserRole.ADMIN
        );
        adminUser.setEnabled(true);
        appUserRepository.save(adminUser);

        return new InMemoryUserDetailsManager(adminUser);
    }

}
