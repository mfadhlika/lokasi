/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.fadhlika.lokasi.service;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

import org.locationtech.jts.geom.Geometry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fadhlika.lokasi.exception.InternalErrorException;
import com.fadhlika.lokasi.model.Location;
import com.fadhlika.lokasi.repository.LocationRepository;
import com.fasterxml.jackson.core.JsonProcessingException;

/**
 *
 * @author fadhl
 */
@Service
public class LocationService {

    private static final Logger logger = LoggerFactory.getLogger(LocationService.class);

    @Value("${reverse_geocode.batch_size}")
    private int batchSize;

    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    private ReverseGeocodeService reverseGeocodeService;

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

    public List<Location> findLocations(int userId, ZonedDateTime start, ZonedDateTime end) {
        return findLocations(userId, Optional.of(start), Optional.of(end), Optional.empty(), Optional.empty(),
                Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty());
    }

    public List<Location> findLocations(int userId, ZonedDateTime start, ZonedDateTime end, Optional<String> device) {
        return findLocations(userId, Optional.of(start), Optional.of(end), device, Optional.empty(), Optional.empty(),
                Optional.empty(), Optional.empty(), Optional.empty());
    }

    public List<Location> findLocations(int userId, Optional<ZonedDateTime> start, Optional<ZonedDateTime> end,
            Optional<String> device, Optional<String> order, Optional<Boolean> desc,
            Optional<Integer> offset, Optional<Integer> limit, Optional<Geometry> bounds) {
        return locationRepository.findLocations(Optional.of(userId), start, end, device, order, desc, offset,
                limit, bounds);
    }

    public Optional<Location> findLocation(int userId, Optional<ZonedDateTime> start, Optional<ZonedDateTime> end,
            Optional<String> device, Optional<String> order, Optional<Boolean> desc) {
        return locationRepository.findLocation(Optional.of(userId), start, end, device, order, desc,
                Optional.empty());
    }

    public Location findLocation(int id) {
        return locationRepository.findLocation(id);
    }

    public void reverseGeocode() {
        reverseGeocodeService.startReverseGeocode();
    }
}
