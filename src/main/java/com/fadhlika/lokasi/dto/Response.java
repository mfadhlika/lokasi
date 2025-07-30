package com.fadhlika.lokasi.dto;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class Response<T> extends ResponseEntity<com.fadhlika.lokasi.dto.Response.Body<T>> {
    static class Body<T> {
        public final String message;

        public final T data;

        public Body(String message, T data) {
            this.message = message;
            this.data = data;
        }
    }

    public Response() {
        super(new Body<T>("", null), HttpStatus.OK);
    }

    public Response(T data, String message, HttpStatus status) {
        super(new Body<T>(message, data), status);
    }

    public Response(String message, HttpStatus status) {
        super(new Body<T>(message, null), status);
    }

    public Response(T data, HttpStatus status) {
        super(new Body<T>("", data), status);
    }

    public Response(HttpStatus status) {
        super(new Body<T>("", null), status);
    }

    public Response(T data) {
        super(new Body<T>("", data), HttpStatus.OK);
    }
}
