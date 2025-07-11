package com.fadhlika.lokasi.controller.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.fadhlika.lokasi.dto.LoginRequest;
import com.fadhlika.lokasi.dto.LoginResponse;
import com.fadhlika.lokasi.service.JwtAuthService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

@RestController
public class AuthController {

    private final JwtAuthService jwtAuthService;

    @Autowired
    public AuthController(JwtAuthService jwtAuthService) {
        this.jwtAuthService = jwtAuthService;
    }

    @PostMapping("/api/v1/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest, HttpServletResponse response) {
        if (!jwtAuthService.validateCredentials(loginRequest.username(), loginRequest.password())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }

        String accessToken = jwtAuthService.generateAccessToken(loginRequest.username());
        String refreshToken = jwtAuthService.generateRefreshToken(loginRequest.username());

        Cookie cookie = new Cookie("refreshToken", refreshToken) {
            {
                setHttpOnly(true);
                setSecure(true);
                setMaxAge(86400);
                setAttribute("SameSite", "None");
            }
        };

        response.addCookie(cookie);

        return new ResponseEntity<>(new LoginResponse(accessToken, refreshToken), HttpStatus.OK);
    }

    @DeleteMapping("/api/v1/logout")
    public ResponseEntity<Object> logout(HttpServletResponse response) {

        Cookie cookie = new Cookie("refreshToken", null) {
            {
                setHttpOnly(true);
                setSecure(true);
                setMaxAge(0);
                setAttribute("SameSite", "None");
            }
        };

        response.addCookie(cookie);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/api/v1/auth/refresh")
    public ResponseEntity<LoginResponse> refresh(@CookieValue(name = "refreshToken") String refreshToken) {
        DecodedJWT decodedJWT = jwtAuthService.decodeRefreshToken(refreshToken);

        if (!jwtAuthService.isRefreshTokenValid(refreshToken, decodedJWT.getSubject())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }

        String accessToken = jwtAuthService.generateAccessToken(decodedJWT.getSubject());

        return new ResponseEntity<>(new LoginResponse(accessToken, refreshToken), HttpStatus.OK);
    }
}
