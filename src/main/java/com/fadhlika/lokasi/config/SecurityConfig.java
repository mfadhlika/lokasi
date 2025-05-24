package com.fadhlika.lokasi.config;

import com.fadhlika.lokasi.service.JwtAuthService;
import com.fadhlika.lokasi.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private static final Logger logger = LoggerFactory.getLogger(SecurityConfig.class);

    private final JwtAuthFilter jwtAuthFilter;

    private final SessionAuthenticationSuccessHandler sessionAuthenticationSuccessHandler;

    @Autowired
    public SecurityConfig(JwtAuthService jwtAuthService, UserService userService) {
        this.jwtAuthFilter = new JwtAuthFilter(jwtAuthService, userService);
        this.sessionAuthenticationSuccessHandler = new SessionAuthenticationSuccessHandler(jwtAuthService);
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    @Order(1)
    public SecurityFilterChain basicSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher("/api/owntracks")
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth
                        -> auth.anyRequest().authenticated()
                )
                .sessionManagement(session
                        -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .httpBasic(Customizer.withDefaults());
        return http.build();
    }

    @Bean
    @Order(2)
    public SecurityFilterChain jwtSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher("/api/v1/**")
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth
                        -> auth.requestMatchers("/api/v1/auth/token").permitAll()
                        .requestMatchers("/api/v1/**").authenticated()
                )
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    @Order(3)
    public SecurityFilterChain sessionSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth
                        -> auth.anyRequest().authenticated()
                )
                .formLogin(form
                        -> form
                        .loginPage("/login").permitAll()
                        .successHandler(sessionAuthenticationSuccessHandler)
                        .defaultSuccessUrl("/map", true)
                )
                .logout(logout
                        -> logout.permitAll()
                        .logoutSuccessUrl("/")
                        .invalidateHttpSession(true)
                        .deleteCookies("SESSION", "token")
                )
                .sessionManagement(session
                        -> session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                );
        return http.build();
    }
}
