package com.fadhlika.lokasi.config;

import com.fadhlika.lokasi.service.JwtAuthService;
import com.fadhlika.lokasi.service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.io.IOException;

public class SessionAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
    private static final Logger logger = LoggerFactory.getLogger(SessionAuthenticationSuccessHandler.class);

    private final JwtAuthService jwtAuthService;

    public SessionAuthenticationSuccessHandler(JwtAuthService jwtAuthService) {
        this.jwtAuthService = jwtAuthService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        String token = jwtAuthService.generateToken(authentication.getName());
        response.addCookie(new Cookie("token", token));
    }
}
