package com.fadhlika.lokasi.dto;

import java.util.HashMap;
import java.util.List;

public record FeatureCollection<T>(
        String type,
        List<Feature<T>> features) {

    public FeatureCollection(List<Feature<T>> features) {
        this("FeatureCollection", features);
    }
}
