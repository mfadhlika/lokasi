package com.fadhlika.lokasi.service;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fadhlika.lokasi.exception.InternalErrorException;
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

        return trips.stream().map(
                (trip) -> {
                    try {
                        List<Location> locations = locationRepository.findLocations(userId, Optional.of(trip.startAt()),
                                Optional.of(trip.endAt()), Optional.empty(), Optional.empty(),
                                Optional.empty(), Optional.empty(), Optional.empty()).toList();

                        return new Trip(trip.userId(), trip.title(), trip.startAt(), trip.endAt(), trip.createdAt(),
                                locations);
                    } catch (SQLException e) {
                        throw new InternalErrorException(e.getMessage());
                    }
                })
                .toList();
    }
}
