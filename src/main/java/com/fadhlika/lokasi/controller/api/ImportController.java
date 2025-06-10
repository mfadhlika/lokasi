package com.fadhlika.lokasi.controller.api;

import com.fadhlika.lokasi.dto.FeatureCollection;
import com.fadhlika.lokasi.exception.BadRequestException;
import com.fadhlika.lokasi.model.Location;
import com.fadhlika.lokasi.model.User;
import com.fadhlika.lokasi.service.LocationService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@RestController
public class ImportController {
    private final LocationService locationService;

    @Autowired
    public ImportController(LocationService locationService) {
        this.locationService = locationService;
    }

    @PostMapping("/api/v1/import")
    public ResponseEntity<Void> importData(@RequestParam("source") String source, @RequestParam("file") MultipartFile file) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        List<Location> locations;
        switch (source.toLowerCase()) {
            case "dawarich":
                ObjectMapper mapper = new ObjectMapper();
                try {
                    FeatureCollection featureCollection = mapper.readValue(file.getInputStream(), FeatureCollection.class);
                    locations = featureCollection.features().stream().map(feature -> {
                        Location l = new Location();

                        l.setGeometry(feature.getGeometry());
                        l.setUserId(user.getId());

                        HashMap<String, Object> properties = feature.getProperties();

                        l.setTimestamp(Instant.ofEpochSecond((int) properties.get("timestamp")).atZone(ZoneOffset.UTC).toLocalDateTime());
                        if (properties.get("altitude") != null) l.setAltitude((int) properties.get("altitude"));
                        if (properties.get("ssid") != null) l.setSsid((String) properties.get("ssid"));
                        if (properties.get("accuracy") != null) l.setAccuracy((int) properties.get("accuracy"));
                        if (properties.get("vertical_accuracy") != null)
                            l.setVerticalAccuracy((int) properties.get("vertical_accuracy"));
                        if (properties.get("tracker_id") != null) l.setDeviceId((String) properties.get("tracker_id"));
                        if (properties.get("battery") != null) l.setBattery((int) properties.get("battery"));
                        if (properties.get("battery_state") != null)
                            l.setBatteryState((String) properties.get("battery_state"));
                        if (properties.get("velocity") != null)
                            l.setSpeed(Double.parseDouble((String) properties.get("velocity")));

                        try {
                            l.setRawData(feature);
                        } catch (JsonProcessingException e) {
                            throw new BadRequestException(e.getMessage());
                        }

                        return l;
                    }).toList();

                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                break;
            default:
                throw new BadRequestException("Invalid source");
        }

        if(locations.isEmpty()) {
            throw new BadRequestException("File is empty");
        }

        locationService.saveLocations(locations);

        return new ResponseEntity<>(HttpStatus.OK);
    }
}
