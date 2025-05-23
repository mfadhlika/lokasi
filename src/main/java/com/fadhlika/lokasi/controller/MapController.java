/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.fadhlika.lokasi.controller;

import java.sql.SQLException;
import java.time.LocalDateTime;

import com.fadhlika.lokasi.model.User;
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
            @RequestParam(defaultValue = "#{T(java.time.LocalDate).now().atTime(T(java.time.LocalTime).MAX)}")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime end) throws SQLException {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        logger.info("fetch points from {} to {} ", start, end);
        GeometryCollection geometryCollection = locationService.findGeometries(user.getId(), start, end);

        WKTWriter wktWriter = new WKTWriter();

        model.addAttribute("name", user.getUsername());
        model.addAttribute("points", wktWriter.write(geometryCollection));
        return "map.html";
    }
}
