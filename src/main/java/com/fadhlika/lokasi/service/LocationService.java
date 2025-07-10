/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.fadhlika.lokasi.service;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import com.fadhlika.lokasi.exception.InternalErrorException;
import com.fadhlika.lokasi.model.Location;
import com.fadhlika.lokasi.repository.LocationRepository;

/**
 *
 * @author fadhl
 */
@Service
public class LocationService {

    private static final Logger logger = LoggerFactory.getLogger(LocationService.class);

    private final LocationRepository locationRepository;

    @Autowired
    public LocationService(LocationRepository locationRepository) {
        this.locationRepository = locationRepository;
    }

    public void saveLocation(Location location) {
        try {
            locationRepository.createLocation(location);
        } catch (DataAccessException ex) {
            throw new InternalErrorException(ex.getMessage());
        }
    }

    public void saveLocations(List<Location> locations) {
        locationRepository.createLocations(locations);
    }

    public List<Location> findLocations(int userId, LocalDateTime start, LocalDateTime end) throws SQLException {
        return findLocations(userId, start, end, Optional.empty());
    }

    public List<Location> findLocations(int userId, LocalDateTime start, LocalDateTime end, Optional<String> device) throws SQLException {
        return locationRepository.findLocations(userId, start, end, device);
    }
}
