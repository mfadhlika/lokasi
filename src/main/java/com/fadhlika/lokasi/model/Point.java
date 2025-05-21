/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.fadhlika.lokasi.model;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.PrecisionModel;

/**
 *
 * @author fadhl
 */
public class Point {

    private int id;
    private int userId;
    private double speed;
    private int accuracy;
    private int altitude;
    private int courseOfDegree;
    private org.locationtech.jts.geom.Point point;
    private int radius;
    private int verticalAccuracy;
    private double pressure;
    private String poi;
    private String ssid;
    private String bssid;
    private LocalDateTime timestamp;
    private LocalDateTime createdAt;

    public Point() {

    }

    public Point(int userId, double longitude, double latitude, LocalDateTime timestamp) {
        this.userId = userId;
        GeometryFactory gf = new GeometryFactory();
        this.point = gf.createPoint(new Coordinate(longitude, latitude));
        this.timestamp = timestamp;
        this.createdAt = LocalDateTime.now(ZoneOffset.UTC);
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

    public int getAltitude() {
        return altitude;
    }

    public void setAltitude(int altitude) {
        this.altitude = altitude;
    }

    public int getCourseOfDegree() {
        return courseOfDegree;
    }

    public void setCourseOfDegree(int courseOfDegree) {
        this.courseOfDegree = courseOfDegree;
    }

    public org.locationtech.jts.geom.Point getPoint() {
        return point;
    }

    public Double getLongitude() {
        return point.getX();
    }

    public Double getLatitude() {
        return point.getY();
    }

    public void setPoint(org.locationtech.jts.geom.Point point) {
        this.point = point;
    }

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    public int getVerticalAccuracy() {
        return verticalAccuracy;
    }

    public void setVerticalAccuracy(int verticalAccuracy) {
        this.verticalAccuracy = verticalAccuracy;
    }

    public double getPressure() {
        return pressure;
    }

    public void setPressure(double pressure) {
        this.pressure = pressure;
    }

    public String getPoi() {
        return poi;
    }

    public void setPoi(String poi) {
        this.poi = poi;
    }

    public String getSsid() {
        return ssid;
    }

    public void setSsid(String ssid) {
        this.ssid = ssid;
    }

    public String getBssid() {
        return bssid;
    }

    public void setBssid(String bssid) {
        this.bssid = bssid;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
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

}
