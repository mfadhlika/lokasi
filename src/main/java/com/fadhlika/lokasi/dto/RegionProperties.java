package com.fadhlika.lokasi.dto;

import java.time.ZonedDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

public record RegionProperties(
        String desc,
        String beaconUUID,
        Integer beaconMajor,
        Integer beaconMinor,
        String rid,
        FeatureCollection geocode,
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssZ") ZonedDateTime createdAt) {

}
