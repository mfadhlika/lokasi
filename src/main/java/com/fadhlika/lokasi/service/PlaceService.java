package com.fadhlika.lokasi.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fadhlika.lokasi.model.Place;
import com.fadhlika.lokasi.repository.PlaceRepository;

@Service
public class PlaceService {
    @Autowired
    private PlaceRepository placeRepository;

    public List<Place> fetchPlaces(Optional<String> city,
            Optional<String> country,
            Optional<Integer> limit,
            Optional<Integer> offset) {
        return placeRepository.fetchPlaces(city, country, limit, offset);
    }
}
