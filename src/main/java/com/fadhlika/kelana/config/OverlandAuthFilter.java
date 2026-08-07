/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.fadhlika.kelana.config;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fadhlika.kelana.model.Integration;
import com.fadhlika.kelana.model.User;
import com.fadhlika.kelana.service.IntegrationService;
import com.fadhlika.kelana.service.UserService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * @author fadhl
 */
public class OverlandAuthFilter extends OncePerRequestFilter {

    @SuppressWarnings("unused")
    private static final Logger logger = LoggerFactory.getLogger(OverlandAuthFilter.class);

    private final UserService userService;

    private final IntegrationService integrationService;

    public OverlandAuthFilter(UserService userService, IntegrationService integrationService) {
        this.userService = userService;
        this.integrationService = integrationService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization");
        String apiKey = null;

        if (StringUtils.hasText(authHeader) && authHeader.startsWith("Bearer ")) {
            apiKey = authHeader.substring(7);
        }

        if (apiKey != null) {
            try {
                Integration integration = integrationService.getIntegrationByOverlandApiKey(apiKey);
                User user = userService.getUserByUserId(integration.userId());

                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(user, null,
                        user.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            } catch (Exception e) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }
        }

        filterChain.doFilter(request, response);
    }
}
