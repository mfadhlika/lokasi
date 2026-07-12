package com.fadhlika.kelana.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.fadhlika.kelana.model.Place;
import com.fadhlika.kelana.repository.PlaceRepository;

@Service
public class PlaceService {
    private final PlaceRepository placeRepository;

    PlaceService(PlaceRepository placeRepository) {
        this.placeRepository = placeRepository;
    }

    public List<Place> fetchPlaces(Optional<String> city,
            Optional<String> country,
            Optional<Integer> limit,
            Optional<Integer> offset) {
        try {
            return placeRepository.fetchPlaces(city, country, limit, offset);
        } catch (Exception e) {
            throw e;
        }
    }
}
