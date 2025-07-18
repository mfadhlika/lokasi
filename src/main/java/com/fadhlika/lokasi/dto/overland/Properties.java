package com.fadhlika.lokasi.dto.overland;

import java.time.ZonedDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Properties(
        @JsonProperty("timestamp")
        ZonedDateTime timestamp,
        @JsonProperty("altitude")
        int altitude,
        @JsonProperty("speed")
        int speed,
        @JsonProperty("course")
        int course,
        @JsonProperty("horizontal_accuracy")
        int horizontalAccuracy,
        @JsonProperty("vertical_accuracy")
        int verticalAccuracy,
        @JsonProperty("speed_accuracy")
        int speedAccuracy,
        @JsonProperty("course_accuracy")
        int courseAccuracy,
        @JsonProperty("motion")
        List<String> motion,
        @JsonProperty("battery_state")
        String batteryState,
        @JsonProperty("battery_level")
        double batteryLevel,
        @JsonProperty("device_id")
        String deviceId,
        @JsonProperty("unique_id")
        String uniqueId,
        @JsonProperty("wifi")
        String wifi,
        // Debug
        @JsonProperty("pauses")
        boolean pauses,
        @JsonProperty("activity")
        String activity,
        @JsonProperty("desired_accuracy")
        int desiredAccuracy,
        @JsonProperty("deferred")
        int deferred,
        @JsonProperty("significant_change")
        String significantChange,
        @JsonProperty("locations_in_payload")
        int locationsInPayload
        ) {

}
