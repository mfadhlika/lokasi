package com.fadhlika.lokasi.dto.owntracks;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public record Tour(
        UUID uuid,
        String label,
        String from,
        String to,
        String url) {
    public Tour(
            UUID uuid,
            String label,
            ZonedDateTime from,
            ZonedDateTime to,
            String url) {
        this(uuid, label, from.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                to.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME), url);
    }
}
