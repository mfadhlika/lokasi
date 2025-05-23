package com.fadhlika.lokasi.dto.overland;

import com.fadhlika.lokasi.dto.Feature;

import java.util.List;

public record InputRequest(List<Feature<Properties>> locations) {
}
