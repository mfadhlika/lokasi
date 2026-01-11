package com.fadhlika.lokasi.model;

import java.time.ZonedDateTime;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.util.GeometricShapeFactory;

import com.fadhlika.lokasi.dto.FeatureCollection;

public class Region {
    int id;
    int userId;
    String desc;
    Geometry geometry;
    String beaconUUID;
    Integer beaconMajor;
    Integer beaconMinor;
    String rid;
    FeatureCollection geocode;
    ZonedDateTime createdAt;

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

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public Geometry getGeometry() {
        return geometry;
    }

    public void setGeometry(Geometry geometry) {
        this.geometry = geometry;
    }

    public String getBeaconUUID() {
        return beaconUUID;
    }

    public void setBeaconUUID(String beaconUUID) {
        this.beaconUUID = beaconUUID;
    }

    public Integer getBeaconMajor() {
        return beaconMajor;
    }

    public void setBeaconMajor(Integer beaconMajor) {
        this.beaconMajor = beaconMajor;
    }

    public Integer getBeaconMinor() {
        return beaconMinor;
    }

    public void setBeaconMinor(Integer beaconMinor) {
        this.beaconMinor = beaconMinor;
    }

    public String getRid() {
        return rid;
    }

    public void setRid(String rid) {
        this.rid = rid;
    }

    public FeatureCollection getGeocode() {
        return geocode;
    }

    public void setGeocode(FeatureCollection geocode) {
        this.geocode = geocode;
    }

    public ZonedDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(ZonedDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Region(
            int id,
            int userId,
            String desc,
            Geometry geometry,
            String beaconUUID,
            Integer beaconMajor,
            Integer beaconMinor,
            String rid,
            FeatureCollection geocode,
            ZonedDateTime createdAt) {
        this.id = id;
        this.userId = userId;
        this.desc = desc;
        this.geometry = geometry;
        this.beaconUUID = beaconUUID;
        this.beaconMajor = beaconMajor;
        this.beaconMinor = beaconMinor;
        this.rid = rid;
        this.createdAt = createdAt;
    }

    public Region(
            int userId,
            String desc,
            double lat,
            double lon,
            int rad,
            String beaconUUID,
            Integer beaconMajor,
            Integer beaconMinor,
            String rid,
            ZonedDateTime createdAt) {

        GeometricShapeFactory shape = new GeometricShapeFactory();
        shape.setCentre(new Coordinate(lon, lat));
        shape.setSize(2 * rad);
        shape.setNumPoints(36);

        this.geometry = shape.createCircle();

        this.userId = userId;
        this.desc = desc;
        this.beaconUUID = beaconUUID;
        this.beaconMajor = beaconMajor;
        this.beaconMinor = beaconMinor;
        this.rid = rid;
        this.createdAt = createdAt;
    }

    public Region(
            int userId,
            String desc,
            double lat,
            double lon,
            int rad,
            String rid) {

        this(userId, desc, lat, lon, rad, rid, null, null, rid, null);
    }
}
