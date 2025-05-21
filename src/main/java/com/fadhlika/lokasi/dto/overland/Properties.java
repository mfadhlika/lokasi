package com.fadhlika.lokasi.dto.overland;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class Properties {
    private LocalDateTime timestamp;
    private int altitude;
    private int speed;
    private int horizontalAccuracy;
    private int verticalAccuracy;
    private List<String> motion;
    private boolean pauses;
    private String activity;
    private int desiredAccuracy;
    private int deferred;
    private String significantChange;
    private int locationsInPayload;
    private String batteryState;
    private double batteryLevel;
    private String deviceId;
    private String wifi;

    @JsonProperty("timestamp")
    public LocalDateTime getTimestamp() {
        return this.timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    @JsonProperty("altitude")
    public int getAltitude() {
        return this.altitude;
    }

    public void setAltitude(int altitude) {
        this.altitude = altitude;
    }

    @JsonProperty("speed")
    public int getSpeed() {
        return this.speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    @JsonProperty("horizontal_accuracy")
    public int getHorizontalAccuracy() {
        return this.horizontalAccuracy;
    }

    public void setHorizontalAccuracy(int horizontalAccuracy) {
        this.horizontalAccuracy = horizontalAccuracy;
    }

    @JsonProperty("vertical_accuracy")
    public int getVerticalAccuracy() {
        return this.verticalAccuracy;
    }

    public void setVerticalAccuracy(int verticalAccuracy) {
        this.verticalAccuracy = verticalAccuracy;
    }

    @JsonProperty("motion")
    public List<String> getMotion() {
        return this.motion;
    }

    public void setMotion(List<String> motion) {
        this.motion = motion;
    }

    @JsonProperty("pauses")
    public boolean getPauses() {
        return this.pauses;
    }

    public void setPauses(boolean pauses) {
        this.pauses = pauses;
    }

    @JsonProperty("activity")
    public String getActivity() {
        return this.activity;
    }

    public void setActivity(String activity) {
        this.activity = activity;
    }

    @JsonProperty("desired_accuracy")
    public int getDesiredAccuracy() {
        return this.desiredAccuracy;
    }

    public void setDesiredAccuracy(int desiredAccuracy) {
        this.desiredAccuracy = desiredAccuracy;
    }

    @JsonProperty("deferred")
    public int getDeferred() {
        return this.deferred;
    }

    public void setDeferred(int deferred) {
        this.deferred = deferred;
    }

    @JsonProperty("significant_change")
    public String getSignificantChange() {
        return this.significantChange;
    }

    public void setSignificantChange(String significantChange) {
        this.significantChange = significantChange;
    }

    @JsonProperty("locations_in_payload")
    public int getLocationsInPayload() {
        return this.locationsInPayload;
    }

    public void setLocationsInPayload(int locationsInPayload) {
        this.locationsInPayload = locationsInPayload;
    }

    @JsonProperty("battery_state")
    public String getBatteryState() {
        return this.batteryState;
    }

    public void setBatteryState(String batteryState) {
        this.batteryState = batteryState;
    }

    @JsonProperty("battery_level")
    public double getBatteryLevel() {
        return this.batteryLevel;
    }

    public void setBatteryLevel(double batteryLevel) {
        this.batteryLevel = batteryLevel;
    }

    @JsonProperty("device_id")
    public String getDeviceId() {
        return this.deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    @JsonProperty("wifi")
    public String getWifi() {
        return this.wifi;
    }

    public void setWifi(String wifi) {
        this.wifi = wifi;
    }
}
