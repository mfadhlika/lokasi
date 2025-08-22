package com.fadhlika.lokasi.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fadhlika.lokasi.model.Location;
import com.fadhlika.lokasi.model.Tour;
import com.fadhlika.lokasi.repository.TourRepository;

@Service
public class TourService {
    @Autowired
    private TourRepository tourRepository;

    @Autowired
    private LocationService locationService;

    public Tour createTour(Tour tour) {
        tourRepository.createTour(tour);
        return tourRepository.fetchTour(tour.uuid());
    }

    public List<Tour> fetchTours(int userId) {
        return tourRepository.fetchTours(userId);
    }

    public Tour fetchTour(UUID uuid) {
        return tourRepository.fetchTour(uuid);
    }

    public void deleteTour(UUID uuid) {
        tourRepository.deleteTour(uuid);
    }

    public List<Location> findToursLocations(UUID uuid) {
        Tour tour = tourRepository.fetchTour(uuid);

        return locationService.findLocations(tour.userId(), Optional.of(tour.from()),
                Optional.of(tour.to()), Optional.empty(), Optional.empty(), Optional.empty(),
                Optional.empty(), Optional.empty());
    }
}
