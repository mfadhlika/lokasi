package com.fadhlika.lokasi.controller.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.fadhlika.lokasi.dto.Auth;
import com.fadhlika.lokasi.dto.LoginRequest;
import com.fadhlika.lokasi.dto.Response;
import com.fadhlika.lokasi.service.JwtAuthService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

@RestController
public class AuthController {
    @Value("${jwt.refresh-expiry}")
    private Long jwtRefreshExpiry;

    @Autowired
    private JwtAuthService jwtAuthService;

    @PostMapping("/api/v1/login")
    public Response<Auth> login(@RequestBody LoginRequest loginRequest, HttpServletResponse response) {
        Auth auth = jwtAuthService.login(loginRequest.username(), loginRequest.password());

        Cookie cookie = new Cookie("refreshToken", auth.refreshToken()) {
            {
                setHttpOnly(true);
                setSecure(true);
                setMaxAge(jwtRefreshExpiry.intValue());
                setAttribute("SameSite", "None");
            }
        };

        response.addCookie(cookie);

        return new Response<>(new Auth(auth.accessToken(), auth.refreshToken()));
    }

    @DeleteMapping("/api/v1/logout")
    public Response<Void> logout(HttpServletResponse response) {

        Cookie cookie = new Cookie("refreshToken", null) {
            {
                setHttpOnly(true);
                setSecure(true);
                setMaxAge(0);
                setAttribute("SameSite", "None");
            }
        };

        response.addCookie(cookie);

        return new Response<>("logged out");
    }

    @GetMapping("/api/v1/auth/refresh")
    public Response<Auth> refresh(@CookieValue String refreshToken) {
        String accessToken = jwtAuthService.refreshAccessToken(refreshToken);

        return new Response<>(new Auth(accessToken, refreshToken));
    }
}
