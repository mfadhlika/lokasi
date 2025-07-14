/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.fadhlika.lokasi.service;

import java.sql.SQLException;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

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

    private final LocationRepository locationRepository;

    private final TransactionTemplate transactionTemplate;

    @Autowired
    public LocationService(LocationRepository locationRepository, TransactionTemplate transactionTemplate) {
        this.locationRepository = locationRepository;
        this.transactionTemplate = transactionTemplate;
    }

    public void saveLocation(Location location) {
        try {
            locationRepository.createLocation(location);
        } catch (DataAccessException | JsonProcessingException ex) {
            throw new InternalErrorException(ex.getMessage());
        }
    }

    public void saveLocations(List<Location> locations) {
        transactionTemplate.executeWithoutResult(_ -> {
            for (Location location : locations) {
                try {
                    locationRepository.createLocation(location);
                } catch (DataAccessException | JsonProcessingException e) {
                    throw new InternalErrorException(e.getMessage());
                }
            }
        });
    }

    public List<Location> findLocations(int userId, ZonedDateTime start, ZonedDateTime end) {
        return findLocations(userId, start, end, Optional.empty(), Optional.empty(), Optional.empty());
    }

    public List<Location> findLocations(int userId, ZonedDateTime start, ZonedDateTime end, Optional<String> device) {
        return findLocations(userId, start, end, device, Optional.empty(), Optional.empty());
    }

    public List<Location> findLocations(int userId, ZonedDateTime start, ZonedDateTime end, Optional<String> device,
            Optional<Integer> offset, Optional<Integer> limit) {
        try {
            return locationRepository.findLocations(userId, start, end, device, offset, limit);
        } catch (SQLException ex) {
            throw new InternalErrorException(ex.getMessage());
        }
    }
}
