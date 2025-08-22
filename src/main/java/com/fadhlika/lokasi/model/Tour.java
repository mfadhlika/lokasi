package com.fadhlika.lokasi.model;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public record Tour(
        int id,
        int userId,
        UUID uuid,
        String label,
        ZonedDateTime from,
        ZonedDateTime to,
        ZonedDateTime createdAt) {
    public Tour(
            int userId,
            String label,
            String from,
            String to) {
        this(0, userId, UUID.randomUUID(), label,
                LocalDateTime.parse(from, DateTimeFormatter.ISO_LOCAL_DATE_TIME).atZone(ZoneOffset.UTC),
                LocalDateTime.parse(to, DateTimeFormatter.ISO_LOCAL_DATE_TIME).atZone(ZoneOffset.UTC),
                ZonedDateTime.now(ZoneOffset.UTC));
    }

}
