/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.fadhlika.lokasi.controller.api.owntracks;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fadhlika.lokasi.dto.owntracks.Message;
import com.fadhlika.lokasi.model.Tour;
import com.fadhlika.lokasi.model.User;
import com.fadhlika.lokasi.service.LocationService;
import com.fadhlika.lokasi.service.TourService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 *
 * @author fadhl
 */
@RestController
@RequestMapping("/api/owntracks")
public class OwntracksController {

    private final Logger logger = LoggerFactory.getLogger(OwntracksController.class);

    @Value("${lokasi.base_url}")
    private String baseUrl;

    @Autowired
    private LocationService locationService;

    @Autowired
    private TourService tourService;

    @PostMapping
    public ResponseEntity<?> pub(@RequestHeader("X-Limit-D") String deviceId, @RequestBody(required = false) Message message)
            throws JsonProcessingException {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        switch (message) {
            case com.fadhlika.lokasi.dto.owntracks.Location location:
                this.locationService.saveLocation(location.toLocation(user.getId(), deviceId));
                break;
            case com.fadhlika.lokasi.dto.owntracks.Request request:
                switch (request.request()) {
                    case "tour":
                        Tour tour = this.tourService.createTour(
                                new Tour(user.getId(), request.tour().label(), request.tour().from(),
                                        request.tour().to()));
                        return ResponseEntity.ok()
                                .body(new com.fadhlika.lokasi.dto.owntracks.Cmd("response", 200,
                                        new com.fadhlika.lokasi.dto.owntracks.Tour(tour.uuid(), tour.label(),
                                                tour.from(),
                                                tour.to(), String.format("%s/tours/%s", baseUrl, tour.uuid()))));

                    case "tours":
                        List<com.fadhlika.lokasi.dto.owntracks.Tour> tours = this.tourService.fetchTours(user.getId())
                                .stream()
                                .map((t) -> new com.fadhlika.lokasi.dto.owntracks.Tour(t.uuid(), t.label(),
                                        t.from(),
                                        t.to(), String.format("%s/tours/%s", baseUrl, t.uuid())))
                                .toList();

                        return ResponseEntity.ok()
                                .body(new com.fadhlika.lokasi.dto.owntracks.Cmd("response", 200, tours));
                    case "untour":
                        this.tourService.deleteTour(request.uuid());
                }
            default:
                ObjectMapper mapper = new ObjectMapper();
                logger.info(mapper.writeValueAsString(message));
                break;
        }

        return ResponseEntity.ok().body(new ArrayList<>());
    }
}
