package com.fadhlika.lokasi.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;
import java.util.List;

public record Properties(
        LocalDateTime timestamp,
        int altitude,
        double speed,
        int course,
        int accuracy,
        int verticalAccuracy,
        List<String> motions,
        String batteryState,
        double batteryLevel,
        String deviceId,
        String ssid
) {

}