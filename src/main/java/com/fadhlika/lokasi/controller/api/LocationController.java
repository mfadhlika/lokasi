package com.fadhlika.lokasi.controller.api;

import com.fadhlika.lokasi.dto.Feature;
import com.fadhlika.lokasi.dto.FeatureCollection;
import com.fadhlika.lokasi.dto.LocationResponse;
import com.fadhlika.lokasi.dto.Response;
import com.fadhlika.lokasi.exception.BadRequestException;
import com.fadhlika.lokasi.model.Location;
import com.fadhlika.lokasi.model.User;
import com.fadhlika.lokasi.service.LocationService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/locations")
public class LocationController {
    private final LocationService locationService;

    @Autowired
    public LocationController(LocationService locationService) {
        this.locationService = locationService;
    }

    @GetMapping
    public ResponseEntity<FeatureCollection> getLocations(
            @RequestParam(defaultValue = "#{T(java.time.LocalDate).now().atStartOfDay()}")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime start,
            @RequestParam(defaultValue = "#{T(java.time.LocalDate).now().atTime(T(java.time.LocalTime).MAX)}")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime end,
            @RequestParam
            Optional<String> device
    ) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        List<Feature> features = null;
        try {
            features = this.locationService.findLocations(user.getId(), start, end, device).stream().map(location -> {
                HashMap<String, Object> props = new HashMap<>();
                props.put("timestamp", location.getTimestamp());
                return new Feature(location.getGeometry(), props);
            }).toList();
        } catch (SQLException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(new FeatureCollection(features), HttpStatus.OK);
    }
}
