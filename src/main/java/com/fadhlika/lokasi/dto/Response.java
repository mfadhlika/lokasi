package com.fadhlika.lokasi.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

public class Response<T> {
    public final String message;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public final T data;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public final List<String> errors;

    public Response(T data, String message, List<String> errors) {
        this.data = data;
        this.message = message;
        this.errors = errors;
    }

    public Response(T data) {
        this(data, "", null);
    }

    public Response(String message) {
        this(null, message, null);
    }
}
