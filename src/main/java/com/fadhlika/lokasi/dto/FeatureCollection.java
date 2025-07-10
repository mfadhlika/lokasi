package com.fadhlika.lokasi.dto;

import java.util.List;

public record FeatureCollection(
        String type,
        List<Feature> features) {

    public FeatureCollection(List<Feature> features) {
        this("FeatureCollection", features);
    }
}
