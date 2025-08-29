package com.fadhlika.lokasi.controller.api;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fadhlika.lokasi.dto.Response;
import com.fadhlika.lokasi.model.Place;
import com.fadhlika.lokasi.service.PlaceService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/api/v1/places")
public class PlaceController {
    @Autowired
    private PlaceService placeService;

    @GetMapping
    public Response<List<Place>> fetchPlaces(
            @RequestParam Optional<String> city,
            @RequestParam Optional<String> country,
            @RequestParam Optional<Integer> limit,
            @RequestParam Optional<Integer> offset) {

        List<Place> places = placeService.fetchPlaces(city, country, limit, offset);

        return new Response<List<Place>>(places);
    }

}
