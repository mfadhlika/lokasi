/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.fadhlika.lokasi.service;

import java.time.Instant;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fadhlika.lokasi.model.User;

/**
 *
 * @author fadhl
 */
@Service
public class JwtAuthService {

    @Value("${jwt.secret}")
    private String jwtSecret;

    public String generateToken(String username) {
        Algorithm algorithm = Algorithm.HMAC256(jwtSecret);
        return JWT.create().withSubject(username).withExpiresAt(Instant.now().plusSeconds(604800)).sign(algorithm);
    }

    public DecodedJWT decode(String token) {
        Algorithm algorithm = Algorithm.HMAC256(jwtSecret);
        return JWT.require(algorithm).build().verify(token);
    }

    public Boolean isValid(String token, String username) {
        DecodedJWT decodedJWT = decode(token);
        return decodedJWT.getSubject().equals(username) && decodedJWT.getExpiresAt().after(new Date());
    }
}
