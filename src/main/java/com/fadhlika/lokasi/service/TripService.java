package com.fadhlika.lokasi.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fadhlika.lokasi.model.Location;
import com.fadhlika.lokasi.model.Trip;
import com.fadhlika.lokasi.repository.LocationRepository;
import com.fadhlika.lokasi.repository.TripRepository;

@Service
public class TripService {
    @Autowired
    private TripRepository tripRepository;

    @Autowired
    private LocationRepository locationRepository;

    public void saveTrip(Trip trip) {
        tripRepository.saveTrip(trip);
    }

    public List<Trip> getTrips(int userId) {
        List<Trip> trips = tripRepository.getTrips(userId);

        for (int i = 0; i < trips.size(); i++) {
            Trip trip = trips.get(i);
            List<Location> locations = locationRepository
                    .findLocations(Optional.of(userId), Optional.of(trip.startAt()),
                            Optional.of(trip.endAt()), Optional.empty(), Optional.empty(),
                            Optional.empty(), Optional.empty(), Optional.empty());

            trips.set(i, new Trip(trip.userId(), trip.title(), trip.startAt(), trip.endAt(), trip.createdAt(),
                    locations));
        }

        return trips;
    }
}
