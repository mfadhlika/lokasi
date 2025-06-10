package com.fadhlika.lokasi.controller.api;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.fadhlika.lokasi.dto.LoginRequest;
import com.fadhlika.lokasi.dto.LoginResponse;
import com.fadhlika.lokasi.dto.Response;
import com.fadhlika.lokasi.model.User;
import com.fadhlika.lokasi.service.JwtAuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;

@RestController
public class AuthController {
    private final JwtAuthService jwtAuthService;

    @Autowired
    public AuthController(JwtAuthService jwtAuthService) {
        this.jwtAuthService = jwtAuthService;
    }

    @PostMapping("/api/v1/login")
    public Response login(@RequestBody LoginRequest loginRequest, HttpServletResponse response) {
        if (!jwtAuthService.validateCredentials(loginRequest.username(), loginRequest.password())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }

        String accessToken = jwtAuthService.generateAccessToken(loginRequest.username());
        String refreshToken = jwtAuthService.generateRefreshToken(loginRequest.username());

        Cookie cookie = new Cookie("refreshToken", refreshToken) {{
            setHttpOnly(true);
            setSecure(true);
            setMaxAge(86400);
            setAttribute("SameSite", "None");
        }};

        response.addCookie(cookie);

        return new LoginResponse(accessToken);
    }

    @GetMapping("/api/v1/auth/refresh")
    public Response refresh(@CookieValue(name = "refreshToken") String refreshToken) {
        DecodedJWT decodedJWT = jwtAuthService.decodeRefreshToken(refreshToken);

        if (!jwtAuthService.isRefreshTokenValid(refreshToken, decodedJWT.getSubject())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }

        String accessToken = jwtAuthService.generateAccessToken(decodedJWT.getSubject());

        return new LoginResponse(accessToken);
    }
}
