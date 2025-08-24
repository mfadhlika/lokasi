package com.fadhlika.lokasi.model;

import java.time.ZonedDateTime;

import com.fadhlika.lokasi.dto.FeatureCollection;

public record Region(
        int id,
        int userId,
        Double lat,
        Double lon,
        Integer rad,
        String beacondUUID,
        Integer beaconMajor,
        Integer beaconMinor,
        String rid,
        FeatureCollection geocode,
        ZonedDateTime createdAt) {
    public Region(
            int userId,
            Double lat,
            Double lon,
            Integer rad,
            String beacondUUID,
            Integer beaconMajor,
            Integer beaconMinor,
            String rid,
            ZonedDateTime createdAt) {
        this(0, userId, lat, lon, rad, beacondUUID, beaconMajor, beaconMinor, rid, null, createdAt);

    }

    public Region(
            int userId,
            Double lat,
            Double lon,
            Integer rad,
            String rid) {
        this(0, userId, lat, lon, rad, null, null, null, rid, null, ZonedDateTime.now());
    }
}
