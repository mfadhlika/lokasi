package com.fadhlika.lokasi.dto;

import java.util.List;

public record GeocodeProperties(String osmType, int osmId, String osmKey, String osmValue, String type, String postcode,
                String countrycode, String name, String country, String city, String district, String locality,
                String street, String state,
                List<Double> extent) {

}
