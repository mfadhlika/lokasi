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
import com.fadhlika.lokasi.service.dto.Auth;
import com.fadhlika.lokasi.util.RandomStringGenerator;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fadhlika.lokasi.exception.UnauthorizedException;
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

    public Auth login(String username, String password) {
        try {
            User user = userRepository.getUser(username);
            if (!validateCredentials(user.getUsername(), password)) {
                throw new UnauthorizedException("invalid username/password");
            }

            String accessToken = generateAccessToken(user);
            String refreshToken = generateRefreshToken(user);

            return new Auth(accessToken, refreshToken);
        } catch (EmptyResultDataAccessException ex) {
            throw new UnauthorizedException("invalid username/password");
        }
    }

    public String refreshAccessToken(String refreshToken) {
        try {
            DecodedJWT decodedJWT = decodeRefreshToken(refreshToken);

            User user = userRepository.getUser(Integer.parseInt(decodedJWT.getSubject()));

            if (!isRefreshTokenValid(refreshToken, decodedJWT.getSubject())) {
                throw new UnauthorizedException("refresh token is not valid");
            }

            return generateAccessToken(user);
        } catch (EmptyResultDataAccessException | TokenExpiredException | SignatureVerificationException ex) {
            throw new UnauthorizedException("refresh token is not valid");
        }
    }

    private String getJwtSecret() {
        if (jwtSecret == null || jwtSecret.isBlank()) {
            jwtSecret = RandomStringGenerator.generate(16);
        }

        return jwtSecret;
    }

    private String getJwtRefreshSecret() {
        if (jwtRefreshSecret == null || jwtRefreshSecret.isBlank()) {
            jwtRefreshSecret = RandomStringGenerator.generate(16);
        }
        return jwtRefreshSecret;
    }

    public String generateAccessToken(User user, Instant expiresAt, Boolean withId) {
        Algorithm algorithm = Algorithm.HMAC256(getJwtSecret());

        JWTCreator.Builder builder = JWT.create()
                .withSubject(String.format("%d", user.getId()))
                .withClaim("username", user.getUsername());
        if (expiresAt != null) {
            builder = builder.withExpiresAt(expiresAt);
        }
        if (withId) {
            builder = builder.withJWTId(UUID.randomUUID().toString());
        }
        return builder.sign(algorithm);
    }

    public String generateAccessToken(User user) {
        Date expiresAt = new Date();
        return generateAccessToken(user, expiresAt.toInstant().plusSeconds(jwtExpiry), false);
    }

    public String generateRefreshToken(User user) {
        Algorithm algorithm = Algorithm.HMAC256(getJwtRefreshSecret());
        return JWT.create().withJWTId(UUID.randomUUID().toString())
                .withSubject(String.format("%d", user.getId()))
                .withClaim("username", user.getUsername())
                .withExpiresAt(Instant.now().plusSeconds(jwtRefreshExpiry)).sign(algorithm);
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

    private Boolean isValid(DecodedJWT decodedJWT, String subject) {
        return decodedJWT.getSubject().equals(subject) && decodedJWT.getExpiresAt().after(new Date());
    }

    public Boolean validateCredentials(String username, String password) {
        User user = userRepository.getUser(username);
        return passwordEncoder.matches(password, user.getPassword());
    }
}
