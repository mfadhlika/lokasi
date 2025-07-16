package com.fadhlika.lokasi.dto;

import java.time.ZonedDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

public record LineStringProperties(
        double distance,
        String distanceUnit,
        double speed,
        String speedUnit,
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssZ") ZonedDateTime startAt,
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssZ") ZonedDateTime endAt,
        List<String> motions) {

}
