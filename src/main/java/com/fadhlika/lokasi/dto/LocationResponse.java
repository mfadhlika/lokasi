package com.fadhlika.lokasi.dto;

import com.fasterxml.jackson.annotation.JsonUnwrapped;

public class LocationResponse extends Response {
    private final FeatureCollection featureCollection;

    public LocationResponse(String message, FeatureCollection featureCollection) {
        super(message);
        this.featureCollection = featureCollection;
    }

    @JsonUnwrapped
    public FeatureCollection getFeatureCollection() {
        return featureCollection;
    }
}
