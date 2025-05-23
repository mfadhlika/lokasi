package com.fadhlika.lokasi.dto;

import java.util.HashMap;
import java.util.List;

public record FeatureCollection(
        String type,
        List<Feature<HashMap<String, Object>>> features) {

    public FeatureCollection(List<Feature<HashMap<String, Object>>> features) {
        this("FeatureCollection", features);
    }
}
