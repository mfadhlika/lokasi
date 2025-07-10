/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.fadhlika.lokasi.config;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fadhlika.lokasi.model.Integration;
import com.fadhlika.lokasi.model.User;
import com.fadhlika.lokasi.service.IntegrationService;
import com.fadhlika.lokasi.service.UserService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * @author fadhl
 */
public class OverlandAuthFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(OverlandAuthFilter.class);

    private final UserService userService;

    private final IntegrationService integrationService;

    public OverlandAuthFilter(UserService userService, IntegrationService integrationService) {
        this.userService = userService;
        this.integrationService = integrationService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization");
        String apiKey = null;

        if (StringUtils.hasText(authHeader) && authHeader.startsWith("Bearer ")) {
            apiKey = authHeader.substring(7);
        }

        if (apiKey != null) {
            try {
                Integration integration = integrationService.getIntegrationByOverlandApiKey(apiKey);
                User user = userService.getUserByUserId(integration.userId());

                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            } catch (Exception _) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }
        }

        filterChain.doFilter(request, response);
    }
}
