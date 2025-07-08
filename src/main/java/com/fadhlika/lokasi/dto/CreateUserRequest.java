package com.fadhlika.lokasi.dto;

import com.fadhlika.lokasi.exception.BadRequestException;

public record CreateUserRequest(String username, String password) {

    public void validate() {

        if (username == null || password == null || username.isBlank() || password.isBlank()) {
            throw new BadRequestException("username or password can't be empty");
        }
    }

}
