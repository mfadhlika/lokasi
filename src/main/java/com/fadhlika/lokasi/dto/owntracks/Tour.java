package com.fadhlika.lokasi.dto.owntracks;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public record Tour(
        String label,
        String from,
        String to,
        UUID uuid,
        String url) {
    public Tour(
            String label,
            ZonedDateTime from,
            ZonedDateTime to,
            UUID uuid,
            String url) {
        this(label, from.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                to.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME), uuid, url);
    }
}
