/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.fadhlika.kelana.model;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;

import com.fadhlika.kelana.dto.FeatureCollection;
import com.fadhlika.kelana.util.GeometryDeserializer;
import com.fadhlika.kelana.util.GeometrySerializer;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.core.JsonProcessingException;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.annotation.JsonDeserialize;
import tools.jackson.databind.annotation.JsonSerialize;

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

    private int id;
    private int userId;
    private String deviceId;
    @JsonSerialize(using = GeometrySerializer.class)
    @JsonDeserialize(using = GeometryDeserializer.class)
    private Geometry geometry;
    private ZonedDateTime timestamp;
    private Integer altitude;
    private Integer course;
    private Integer courseAccuracy;
    private Double speed;
    private Integer accuracy;
    private Integer verticalAccuracy;
    private List<String> motions;
    private BatteryState batteryState = BatteryState.UNKNOWN;
    private Double battery;
    private Double pressure;
    private String ssid;
    private Integer importId;
    private String rawData;
    private ZonedDateTime createdAt;
    private FeatureCollection geocode;

    public Location(Geometry point) {
        this.geometry = point;
        this.createdAt = ZonedDateTime.now(ZoneOffset.UTC);
    }

    public Location(double lat, double lon) {
        GeometryFactory gf = new GeometryFactory();
        this.geometry = gf.createPoint(new Coordinate(lon, lat));
        this.createdAt = ZonedDateTime.now(ZoneOffset.UTC);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public Geometry getGeometry() {
        return geometry;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public Integer getAltitude() {
        return altitude;
    }

    public void setAltitude(int altitude) {
        this.altitude = altitude;
    }

    public Integer getCourse() {
        return course;
    }

    public void setCourse(int course) {
        this.course = course;
    }

    public Double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public Integer getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(int accuracy) {
        this.accuracy = accuracy;
    }

    public Integer getVerticalAccuracy() {
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

    public Double getBattery() {
        return battery;
    }

    public void setBattery(double battery) {
        this.battery = battery;
    }

    public Double getPressure() {
        return pressure;
    }

    public void setPressure(Double pressure) {
        this.pressure = pressure;
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

    public Integer getImportId() {
        return importId;
    }

    public void setImportId(int importId) {
        this.importId = importId;
    }

    public Integer getCourseAccuracy() {
        return courseAccuracy;
    }

    public void setCourseAccuracy(int courseAccuracy) {
        this.courseAccuracy = courseAccuracy;
    }

    public double getDistanceInMeters(Location location) {
        Double distance = Math.abs(this.geometry.distance(location.geometry));

        return distance * Math.PI / 180 * R;
    }

    public FeatureCollection getGeocode() {
        return geocode;
    }

    public void setGeocode(FeatureCollection featureCollection) {
        this.geocode = featureCollection;
    }
}
