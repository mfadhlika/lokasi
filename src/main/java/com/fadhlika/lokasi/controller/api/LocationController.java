package com.fadhlika.lokasi.controller.api;

import com.fadhlika.lokasi.dto.Feature;
import com.fadhlika.lokasi.dto.FeatureCollection;
import com.fadhlika.lokasi.dto.Response;
import com.fadhlika.lokasi.model.Location;
import com.fadhlika.lokasi.model.User;
import com.fadhlika.lokasi.service.LocationService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/api/v1/location")
public class LocationController {
    private final LocationService locationService;

    @Autowired
    public LocationController(LocationService locationService) {
        this.locationService = locationService;
    }

    @GetMapping
    public FeatureCollection getLocations(
            @RequestParam(defaultValue = "#{T(java.time.LocalDate).now().atStartOfDay()}")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime start,
            @RequestParam(defaultValue = "#{T(java.time.LocalDate).now().atTime(T(java.time.LocalTime).MAX)}")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime end
    ) throws SQLException {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        List<Feature<HashMap<String, Object>>> features = this.locationService.find(user.getId(), start, end).stream().map(location -> {
            HashMap<String, Object> props = new HashMap<>();
            props.put("timestamp", location.getTimestamp());
            return new Feature<>(location.getGeometry(), props);
        }).toList();

        return new FeatureCollection(features);
    }

    @PostMapping
    public Response addLocations(@RequestBody FeatureCollection featureCollection) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        List<Location> locations = featureCollection.features().stream().map(feature -> {
            Location l = new Location();

            l.setUserId(user.getId());
            l.setGeometry(feature.getGeometry());

            HashMap<String, Object> properties = feature.getProperties();
            l.setTimestamp(Instant.ofEpochSecond((int) properties.get("timestamp")).atZone(ZoneOffset.UTC).toLocalDateTime());

            try {
                l.setRawData(featureCollection);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }

            return l;
        }).toList();

        this.locationService.createPoints(locations);

        return new Response("ok");
    }
}
