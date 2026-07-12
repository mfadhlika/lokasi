/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.fadhlika.kelana.model;

import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

/**
 *
 * @author fadhl
 */
public class User implements UserDetails {

    public enum Role implements GrantedAuthority {
        ROLE_USER("USER"),
        ROLE_ADMIN("ADMIN");

        private final String value;

        private Role(String value) {
            this.value = value;
        }

        @Override
        public String getAuthority() {
            return this.name();
        }

        public String getValue() {
            return value;
        }
    }

    public class Extra {

    }

    private int id;
    private String username;
    private String password;
    private ZonedDateTime createdAt;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
        this.createdAt = ZonedDateTime.now();
    }

    public User(int id, String username, String password) {
        this.id = id;
        this.username = username;
        this.password = password;
    }

    public User(int id, String username, String password, ZonedDateTime createdAt) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.createdAt = createdAt;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    public void setPassword(String username) {
        this.username = username;
    }

    @Override
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public ZonedDateTime getCreatedAt() {
        return createdAt;
    }

    public int getId() {
        return id;
    }
}
