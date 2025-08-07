package com.fadhlika.lokasi.dto;

import java.time.ZonedDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

public record PointProperties(
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssZ") ZonedDateTime timestamp,
        int altitude,
        double speed,
        int course,
        int courseAccuracy,
        int accuracy,
        int verticalAccuracy,
        List<String> motions,
        String batteryState,
        double batteryLevel,
        String deviceId,
        String ssid,
        FeatureCollection geocode,
        String rawData) {

}
