package com.fadhlika.lokasi.dto;

import java.time.ZonedDateTime;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

public record TripProperties(
                int id,
                String title,
                @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssZ") ZonedDateTime startAt,
                @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssZ") ZonedDateTime endAt,
                UUID uuid,
                @JsonProperty("public") Boolean isPublic,
                String publicUrl) {
}
