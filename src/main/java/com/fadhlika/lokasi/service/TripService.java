package com.fadhlika.lokasi.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

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

    public Trip saveTrip(Trip trip) {
        tripRepository.saveTrip(trip);
        return tripRepository.getTrip(trip.uuid());
    }

    public List<Trip> getTrips(int userId, Optional<Boolean> isPublic) {
        List<Trip> trips = tripRepository.getTrips(userId, isPublic);

        for (int i = 0; i < trips.size(); i++) {
            Trip trip = trips.get(i);
            List<Location> locations = locationRepository
                    .findLocations(Optional.of(userId), Optional.of(trip.startAt()),
                            Optional.of(trip.endAt()), Optional.empty(), Optional.empty(),
                            Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty());

            trips.set(i,
                    new Trip(trip.id(), trip.userId(), trip.title(), trip.startAt(), trip.endAt(), trip.createdAt(),
                            locations, trip.uuid(), trip.isPublic()));
        }

        return trips;
    }

    public Trip getTrip(UUID uuid) {
        Trip trip = tripRepository.getTrip(uuid);

        List<Location> locations = locationRepository
                .findLocations(Optional.of(trip.userId()), Optional.of(trip.startAt()),
                        Optional.of(trip.endAt()), Optional.empty(), Optional.empty(),
                        Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty());

        return new Trip(trip.id(), trip.userId(), trip.title(), trip.startAt(), trip.endAt(), trip.createdAt(),
                locations, trip.uuid(), trip.isPublic());
    }

    public void deleteTrip(UUID uuid) {
        tripRepository.deleteTrip(uuid);
    }

    public void deleteTrip(int id) {
        tripRepository.deleteTrip(id);

    }
}
