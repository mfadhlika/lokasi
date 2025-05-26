/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.fadhlika.lokasi.controller;

import com.fadhlika.lokasi.dto.Feature;
import com.fadhlika.lokasi.dto.FeatureCollection;
import com.fadhlika.lokasi.dto.Properties;
import com.fadhlika.lokasi.model.Location;
import com.fadhlika.lokasi.model.User;
import com.fadhlika.lokasi.service.LocationService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.sql.SQLException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * @author fadhl
 */
@Controller
public class MapController {

    private final Logger logger = LoggerFactory.getLogger(MapController.class);

    private final LocationService locationService;

    @Autowired
    public MapController(LocationService locationService) {
        this.locationService = locationService;
    }

    @GetMapping("/map")
    public String map(
            Model model,
            @RequestParam(defaultValue = "#{T(java.time.LocalDate).now().atStartOfDay()}")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime start,
            @RequestParam(defaultValue = "#{T(java.time.LocalDate).now().atTime(23, 59, 59)}")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime end) throws SQLException, JsonProcessingException {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<Feature> features = new ArrayList<>();
        List<Feature> lineStringFeatures = new ArrayList<>();


        List<Location> locations = this.locationService.findPoints(user.getId(), start, end);

        for (int i = 0; i < locations.size(); i++) {
            Location location = locations.get(i);

            Properties props = new Properties(
                    location.getTimestamp(),
                    location.getAltitude(),
                    location.getSpeed(),
                    location.getCourse(),
                    location.getAccuracy(),
                    location.getVerticalAccuracy(),
                    location.getMotions(),
                    null,
                    location.getBattery(),
                    location.getDeviceId(),
                    location.getSsid()

            );

            features.add(new Feature(location.getGeometry(), props));

            try {
                Location nextLocation = locations.get(i + 1);
                Duration timeDiff = Duration.between(nextLocation.getTimestamp(), location.getTimestamp());
                if (timeDiff.toSeconds() > 1800) continue;

                GeometryFactory geometryFactory = new GeometryFactory();
                Geometry ls = geometryFactory.createLineString(new Coordinate[]{location.getGeometry().getCoordinate(), nextLocation.getGeometry().getCoordinate()});

                double distance = location.getGeometry().distance(nextLocation.getGeometry()) / 180 * Math.PI * 6371;

                double timeDiffSeconds = Duration.between(nextLocation.getTimestamp(), location.getTimestamp()).toSeconds() / 3600.0;
                double speed = Math.round(distance / timeDiffSeconds);
                lineStringFeatures.add(new Feature(ls, new HashMap<>() {
                    {
                        put("speed", speed);
                    }
                }));
            } catch (IndexOutOfBoundsException _) {
            }
        }

        FeatureCollection points = new FeatureCollection(features);
        FeatureCollection lines = new FeatureCollection(lineStringFeatures);

        model.addAttribute("name", user.getUsername());
        model.addAttribute("start", start);
        model.addAttribute("end", end);
        model.addAttribute("points", points);
        model.addAttribute("lines", lines);

        try {
            Coordinate lastLocation = features.getLast().getGeometry().getCoordinate();
            model.addAttribute("lastLocation", new Double[]{lastLocation.getY(), lastLocation.getX()});
        } catch (
                NoSuchElementException _) {
            model.addAttribute("lastLocation", new Double[]{-6.1750, 106.8266});
        }
        return "map";
    }
}
