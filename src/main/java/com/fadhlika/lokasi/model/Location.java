/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.fadhlika.lokasi.model;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;

import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author fadhl
 */
public class Location {

    private final int R = 6371 * 1000;

    public enum BatteryState {
        UNKNOWN(0),
        UNPLUGGED(1),
        CHARGING(2),
        FULL(3);

        @JsonValue
        public final int value;

        BatteryState(int value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return switch (this.value) {
                case 0 ->
                    "unknown";
                case 1 ->
                    "unplugged";
                case 2 ->
                    "charging";
                case 3 ->
                    "full";
                default ->
                    "unknown";
            };
        }
    }

    private int userId;
    private String deviceId;
    private Geometry geometry;
    private ZonedDateTime timestamp;
    private int altitude;
    private int course;
    private int courseAccuracy;
    private double speed;
    private int accuracy;
    private int verticalAccuracy;
    private List<String> motions;
    private BatteryState batteryState;
    private double battery;
    private String ssid;
    private int importId;
    private String rawData;
    private ZonedDateTime createdAt;

    public Location() {
        this.createdAt = ZonedDateTime.now(ZoneOffset.UTC);
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setGeometry(Geometry geometry) {
        this.geometry = geometry;
    }

    public void setGeometry(Double latitude, Double longitude) {
        GeometryFactory gf = new GeometryFactory();
        this.geometry = gf.createPoint(new Coordinate(latitude, longitude));
    }

    public Geometry getGeometry() {
        return geometry;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public int getAltitude() {
        return altitude;
    }

    public void setAltitude(int altitude) {
        this.altitude = altitude;
    }

    public int getCourse() {
        return course;
    }

    public void setCourse(int course) {
        this.course = course;
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public int getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(int accuracy) {
        this.accuracy = accuracy;
    }

    public int getVerticalAccuracy() {
        return verticalAccuracy;
    }

    public void setVerticalAccuracy(int verticalAccuracy) {
        this.verticalAccuracy = verticalAccuracy;
    }

    public List<String> getMotions() {
        return motions;
    }

    public void setMotions(List<String> motions) {
        this.motions = motions;
    }

    public BatteryState getBatteryState() {
        return batteryState;
    }

    public void setBatteryState(int bs) {
        this.batteryState = switch (bs) {
            case 0 ->
                BatteryState.UNKNOWN;
            case 1 ->
                BatteryState.UNPLUGGED;
            case 2 ->
                BatteryState.CHARGING;
            case 3 ->
                BatteryState.FULL;
            default ->
                throw new IllegalStateException("Unexpected value: " + bs);
        };
    }

    public void setBatteryState(String bs) {
        if (bs == null) {
            this.batteryState = BatteryState.UNKNOWN;
            return;
        }

        this.batteryState = switch (bs) {
            case "unknown" ->
                BatteryState.UNKNOWN;
            case "unplugged" ->
                BatteryState.UNPLUGGED;
            case "charging" ->
                BatteryState.CHARGING;
            case "full" ->
                BatteryState.FULL;
            default ->
                throw new IllegalStateException("Unexpected value: " + bs);
        };
    }

    public double getBattery() {
        return battery;
    }

    public void setBattery(double battery) {
        this.battery = battery;
    }

    public String getSsid() {
        return ssid;
    }

    public void setSsid(String ssid) {
        this.ssid = ssid;
    }

    public String getRawData() {
        return rawData;
    }

    public void setRawData(Object rawData) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        this.rawData = mapper.writeValueAsString(rawData);
    }

    public void setRawData(String rawData) {
        this.rawData = rawData;
    }

    public ZonedDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(ZonedDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public ZonedDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(ZonedDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public int getImportId() {
        return importId;
    }

    public void setImportId(int importId) {
        this.importId = importId;
    }

    public int getCourseAccuracy() {
        return courseAccuracy;
    }

    public void setCourseAccuracy(int courseAccuracy) {
        this.courseAccuracy = courseAccuracy;
    }

    public double getDistanceInMeters(Location location) {
        Double distance = Math.abs(this.geometry.distance(location.geometry));

        return distance * Math.PI / 180 * R;
    }
}
