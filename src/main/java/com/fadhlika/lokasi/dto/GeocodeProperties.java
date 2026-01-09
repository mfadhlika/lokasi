package com.fadhlika.lokasi.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public record GeocodeProperties(
                @JsonProperty("osm_type") String osmType,
                @JsonProperty("osm_id") long osmId,
                @JsonProperty("osm_key") String osmKey,
                @JsonProperty("osm_value") String osmValue,
                String type,
                String postcode,
                String countrycode,
                String name,
                String country,
                String city,
                String district,
                String locality,
                String street,
                String state,
                String county,
                String housenumber,
                List<Double> extent) {

}
