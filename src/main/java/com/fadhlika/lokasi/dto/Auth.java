package com.fadhlika.lokasi.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

public record Auth(
        String accessToken,
        @JsonInclude(JsonInclude.Include.NON_NULL) String refreshToken) {

}
