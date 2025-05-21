package com.fadhlika.lokasi.dto;

public class LoginResponse extends Response {
    public String token;

    public LoginResponse(String token) {
        super("success");
        this.token = token;
    }
}
