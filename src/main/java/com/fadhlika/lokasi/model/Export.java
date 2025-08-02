package com.fadhlika.lokasi.model;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.time.ZonedDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;

public record Export(int id,
        @JsonIgnore int userId,
        String filename,
        ZonedDateTime startAt,
        ZonedDateTime endAt,
        @JsonIgnore InputStream content,
        boolean done,
        ZonedDateTime createdAt) {

    public Export(int userId,
            String filename,
            ZonedDateTime startAt,
            ZonedDateTime endAt) {
        this(0, userId, filename, startAt, endAt, new ByteArrayInputStream(new byte[] {}), false, ZonedDateTime.now());
    }

    public Export(int userId,
            ZonedDateTime startAt,
            ZonedDateTime endAt) {
        this(0, userId, "", startAt, endAt, new ByteArrayInputStream(new byte[] {}), false, ZonedDateTime.now());
    }
}
