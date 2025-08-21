package com.fadhlika.lokasi.dto;

import java.time.ZonedDateTime;

public record Stats(
        int totalPoints,
        int totalReverseGeocodedPoints,
        int totalCitiesVisited,
        int totalCountriesVisited,
        ZonedDateTime lastPointTimestamp) {

}
