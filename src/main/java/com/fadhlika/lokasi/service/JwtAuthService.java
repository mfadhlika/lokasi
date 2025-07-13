/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.fadhlika.lokasi.service;

import java.time.Instant;
import java.util.Date;
import java.util.UUID;

import com.auth0.jwt.JWTCreator;
import com.fadhlika.lokasi.repository.UserRepository;
import com.fadhlika.lokasi.util.RandomStringGenerator;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fadhlika.lokasi.model.User;

/**
 * @author fadhl
 */
@Service
public class JwtAuthService {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.refresh-secret}")
    private String jwtRefreshSecret;

    @Value("${jwt.expiry}")
    private Long jwtExpiry;

    @Value("${jwt.refresh-expiry}")
    private Long jwtRefreshExpiry;

    private final PasswordEncoder passwordEncoder;

    private final UserRepository userRepository;

    public JwtAuthService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    private String getJwtSecret() {
        if(jwtSecret == null || jwtSecret.isBlank()) {
            jwtSecret = RandomStringGenerator.generate(16);
        }

        return jwtSecret;
    }

    private String getJwtRefreshSecret() {
        if(jwtRefreshSecret == null || jwtRefreshSecret.isBlank()) {
            jwtRefreshSecret = RandomStringGenerator.generate(16);
        }
        return jwtRefreshSecret;
    }

    public String generateAccessToken(String username, Instant expiresAt, Boolean withId) {
        Algorithm algorithm = Algorithm.HMAC256(getJwtSecret());

        JWTCreator.Builder builder = JWT.create().withSubject(username);
        if (expiresAt != null) {
            builder = builder.withExpiresAt(expiresAt);
        }
        if (withId) {
            builder = builder.withJWTId(UUID.randomUUID().toString());
        }
        return builder.sign(algorithm);
    }

    public String generateAccessToken(String username) {
        Date expiresAt = new Date();
        return generateAccessToken(username, expiresAt.toInstant().plusSeconds(jwtExpiry), false);
    }

    public String generateRefreshToken(String username) {
        Algorithm algorithm = Algorithm.HMAC256(getJwtRefreshSecret());
        return JWT.create().withJWTId(UUID.randomUUID().toString()).withSubject(username).withExpiresAt(Instant.now().plusSeconds(jwtRefreshExpiry)).sign(algorithm);
    }

    public DecodedJWT decodeAccessToken(String token) {
        Algorithm algorithm = Algorithm.HMAC256(getJwtSecret());
        return JWT.require(algorithm).build().verify(token);
    }

    public DecodedJWT decodeRefreshToken(String token) {
        Algorithm algorithm = Algorithm.HMAC256(getJwtRefreshSecret());
        return JWT.require(algorithm).build().verify(token);
    }

    public Boolean isAccessTokenValid(String token, String username) {
        DecodedJWT decodedJWT = decodeAccessToken(token);
        return isValid(decodedJWT, username);
    }

    public Boolean isRefreshTokenValid(String token, String username) {
        DecodedJWT decodedJWT = decodeRefreshToken(token);
        return isValid(decodedJWT, username);
    }

    private Boolean isValid(DecodedJWT decodedJWT, String username) {
        return decodedJWT.getSubject().equals(username) && decodedJWT.getExpiresAt().after(new Date());
    }

    public Boolean validateCredentials(String username, String password) {
        User user = userRepository.getUser(username);
        return passwordEncoder.matches(password, user.getPassword());
    }
}
