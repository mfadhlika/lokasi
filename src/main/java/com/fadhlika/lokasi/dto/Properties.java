package com.fadhlika.lokasi.dto;

import java.time.ZonedDateTime;
import java.util.List;

public record Properties(ZonedDateTime timestamp, int altitude, double speed, int course, int courseAccuracy, int accuracy,
        int verticalAccuracy, List<String> motions, String batteryState, double batteryLevel,
        String deviceId, String ssid, String rawData) {

}
