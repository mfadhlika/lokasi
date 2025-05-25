/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.fadhlika.lokasi.controller;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.NoSuchElementException;

import com.fadhlika.lokasi.dto.Feature;
import com.fadhlika.lokasi.dto.FeatureCollection;
import com.fadhlika.lokasi.dto.Properties;
import com.fadhlika.lokasi.model.User;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryCollection;
import org.locationtech.jts.io.WKTWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.fadhlika.lokasi.service.LocationService;
import org.springframework.web.bind.annotation.RequestParam;

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

        List<Feature<Properties>> features = this.locationService.findPoints(user.getId(), start, end).stream().map(location -> {
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
            return new Feature<>(location.getGeometry(), props);
        }).toList();

        FeatureCollection locations = new FeatureCollection(features);


        ObjectMapper mapper = JsonMapper.builder()
                .findAndAddModules()
                .build();

        model.addAttribute("name", user.getUsername());
        model.addAttribute("start", start);
        model.addAttribute("end", end);
        model.addAttribute("locations", locations);

        try {
            Coordinate lastLocation = features.getLast().getGeometry().getCoordinate();
            model.addAttribute("lastLocation", new Double[]{lastLocation.getY(), lastLocation.getX()});
        } catch (NoSuchElementException _) {
            model.addAttribute("lastLocation", new Double[]{-6.1750, 106.8266});
        }
        return "map";
    }
}
