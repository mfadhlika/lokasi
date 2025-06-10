package com.fadhlika.lokasi.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

public class LoginResponse extends Response {
    public String accessToken;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public String refreshToken = null;

    public LoginResponse(String accessToken) {
        super("success");
        this.accessToken = accessToken;
    }

    public LoginResponse(String accessToken, String refreshToken) {
        super("success");
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}
