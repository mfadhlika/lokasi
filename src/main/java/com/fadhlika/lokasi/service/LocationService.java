/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.fadhlika.lokasi.service;

import java.io.IOException;
import java.sql.SQLException;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import org.locationtech.jts.geom.Coordinate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fadhlika.lokasi.dto.FeatureCollection;
import com.fadhlika.lokasi.exception.InternalErrorException;
import com.fadhlika.lokasi.model.Location;
import com.fadhlika.lokasi.repository.LocationRepository;
import com.fadhlika.lokasi.repository.PhotonRepository;
import com.fasterxml.jackson.core.JsonProcessingException;

/**
 *
 * @author fadhl
 */
@Service
public class LocationService {

    private static final Logger logger = LoggerFactory.getLogger(LocationService.class);

    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    private PhotonRepository photonRepository;

    public void saveLocation(Location location) {
        try {
            locationRepository.createLocation(location);
        } catch (DataAccessException | JsonProcessingException ex) {
            throw new InternalErrorException(ex.getMessage());
        }

    }

    @Transactional
    public void saveLocations(List<Location> locations) {
        for (Location location : locations) {
            try {
                locationRepository.createLocation(location);
            } catch (DataAccessException | JsonProcessingException e) {
                throw new InternalErrorException(e.getMessage());
            }
        }
    }

    public Stream<Location> findLocations(int userId, ZonedDateTime start, ZonedDateTime end) {
        return findLocations(userId, Optional.of(start), Optional.of(end), Optional.empty(), Optional.empty(),
                Optional.empty());
    }

    public Stream<Location> findLocations(int userId, ZonedDateTime start, ZonedDateTime end, Optional<String> device) {
        return findLocations(userId, Optional.of(start), Optional.of(end), device, Optional.empty(), Optional.empty());
    }

    public Stream<Location> findLocations(int userId, Optional<ZonedDateTime> start, Optional<ZonedDateTime> end,
            Optional<String> device,
            Optional<Integer> offset, Optional<Integer> limit) {
        try {
            return locationRepository.findLocations(userId, start, end, device, offset, limit);
        } catch (SQLException ex) {
            throw new InternalErrorException(ex.getMessage());
        }
    }

    public Location findLocation(int userId, Optional<ZonedDateTime> start, Optional<ZonedDateTime> end,
            Optional<String> device) {
        try {
            return locationRepository.findLocation(userId, start, end, device);
        } catch (SQLException ex) {
            throw new InternalErrorException(ex.getMessage());
        }
    }

    public Location findLocation(int id) {
        try {
            return locationRepository.findLocation(id);
        } catch (SQLException ex) {
            throw new InternalErrorException(ex.getMessage());
        }
    }

    public Location reverseGeocode(int id) {
        try {
            Location location = locationRepository.findLocation(id);

            Coordinate coord = location.getGeometry().getCoordinate();
            FeatureCollection featureCollection = photonRepository.reverseGeocode(coord.y, coord.x);

            location.setGeocode(featureCollection);

            locationRepository.createLocation(location);

            return location;
        } catch (SQLException | IOException | InterruptedException e) {
            throw new InternalErrorException(e.getMessage());
        }
    }
}
