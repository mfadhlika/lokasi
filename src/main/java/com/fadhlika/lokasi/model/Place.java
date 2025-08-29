package com.fadhlika.lokasi.model;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;

import org.locationtech.jts.geom.Geometry;

import com.fadhlika.lokasi.util.GeometryDeserializer;
import com.fadhlika.lokasi.util.GeometrySerializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

public record Place(
        int id,
        String provider,
        @JsonSerialize(using = GeometrySerializer.class) @JsonDeserialize(using = GeometryDeserializer.class) Geometry geometry,
        String type,
        String postcode,
        String countryCode,
        String name,
        String country,
        String city,
        String district,
        String locality,
        String street,
        String state,
        byte[] geodata,
        ZonedDateTime createdAt) {
    public Place(
            String provider,
            @JsonSerialize(using = GeometrySerializer.class) @JsonDeserialize(using = GeometryDeserializer.class) Geometry geometry,
            String type,
            String postcode,
            String countryCode,
            String name,
            String country,
            String city,
            String district,
            String locality,
            String street,
            String state,
            byte[] geodata) {
        this(0, provider, geometry, type, postcode, countryCode, name, country, city, district, locality, street, state,
                geodata, ZonedDateTime.now(ZoneOffset.UTC));
    }
}
