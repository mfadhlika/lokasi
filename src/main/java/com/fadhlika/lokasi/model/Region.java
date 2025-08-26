package com.fadhlika.lokasi.model;

import java.time.ZonedDateTime;

import com.fadhlika.lokasi.dto.FeatureCollection;

public record Region(
        int id,
        int userId,
        String desc,
        Double lat,
        Double lon,
        Integer rad,
        String beaconUUID,
        Integer beaconMajor,
        Integer beaconMinor,
        String rid,
        FeatureCollection geocode,
        ZonedDateTime createdAt) {
    public Region(
            int userId,
            String desc,
            Double lat,
            Double lon,
            Integer rad,
            String beaconUUID,
            Integer beaconMajor,
            Integer beaconMinor,
            String rid,
            ZonedDateTime createdAt) {
        this(0, userId, desc, lat, lon, rad, beaconUUID, beaconMajor, beaconMinor, rid, null, createdAt);

    }

    public Region(
            int userId,
            String desc,
            Double lat,
            Double lon,
            Integer rad,
            String rid) {
        this(0, userId, desc, lat, lon, rad, null, null, null, rid, null, ZonedDateTime.now());
    }
}
