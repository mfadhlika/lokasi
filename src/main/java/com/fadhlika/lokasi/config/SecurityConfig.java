package com.fadhlika.lokasi.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.fadhlika.lokasi.service.IntegrationService;
import com.fadhlika.lokasi.service.JwtAuthService;
import com.fadhlika.lokasi.service.UserService;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private static final Logger logger = LoggerFactory.getLogger(SecurityConfig.class);

    private final JwtAuthFilter jwtAuthFilter;

    private final OwntracksAuthFilter owntracksAuthFilter;

    private final OverlandAuthFilter overlandAuthFilter;

    @Autowired
    public SecurityConfig(JwtAuthService jwtAuthService, UserService userService, IntegrationService integrationService) {
        this.jwtAuthFilter = new JwtAuthFilter(jwtAuthService, userService);
        this.owntracksAuthFilter = new OwntracksAuthFilter(userService, integrationService);
        this.overlandAuthFilter = new OverlandAuthFilter(userService, integrationService);
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain owntracksSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher("/api/owntracks")
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth
                        -> auth.anyRequest().authenticated()
                )
                .sessionManagement(session
                        -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .addFilterBefore(owntracksAuthFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public SecurityFilterChain overlandSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher("/api/overland")
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth
                        -> auth.anyRequest().authenticated()
                )
                .sessionManagement(session
                        -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .addFilterBefore(overlandAuthFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public SecurityFilterChain jwtSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher("/api/v1/**")
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth
                        -> auth.requestMatchers("/api/v1/login").permitAll()
                        .requestMatchers("/api/v1/auth/refresh").permitAll()
                        .requestMatchers("/api/v1/logout").permitAll()
                        .requestMatchers("/api/v1/**").authenticated()
                )
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
