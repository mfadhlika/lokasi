/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.fadhlika.lokasi.controller.api.owntracks;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
import com.fadhlika.lokasi.model.Trip;
import com.fadhlika.lokasi.model.User;
import com.fadhlika.lokasi.service.LocationService;
import com.fadhlika.lokasi.service.TripService;
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
    private TripService tripService;

    @PostMapping
    public ResponseEntity<?> pub(@RequestHeader("X-Limit-D") String deviceId,
            @RequestBody(required = false) Message message)
            throws JsonProcessingException {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        switch (message) {
            case com.fadhlika.lokasi.dto.owntracks.Location location:
                this.locationService.saveLocation(location.toLocation(user.getId(), deviceId));
                break;
            case com.fadhlika.lokasi.dto.owntracks.Request request:
                switch (request.request()) {
                    case "tour":
                        logger.info("request tour creation");
                        Trip trip = this.tripService.saveTrip(
                                new Trip(user.getId(), request.tour().label(),
                                        LocalDateTime
                                                .parse(request.tour().from(), DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                                                .atZone(ZoneOffset.UTC),
                                        LocalDateTime.parse(request.tour().to(), DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                                                .atZone(ZoneOffset.UTC),
                                        true));

                        return ResponseEntity.ok().body(new com.fadhlika.lokasi.dto.owntracks.Cmd("response", 200,
                                new com.fadhlika.lokasi.dto.owntracks.Tour(trip.title(),
                                        trip.startAt(),
                                        trip.endAt(), trip.uuid(),
                                        String.format("%s/trips/%s", baseUrl, trip.uuid()))));
                    case "tours":
                        logger.info("request tours");
                        List<com.fadhlika.lokasi.dto.owntracks.Tour> tours = this.tripService
                                .getTrips(user.getId(), Optional.of(true))
                                .stream()
                                .map((t) -> new com.fadhlika.lokasi.dto.owntracks.Tour(t.title(),
                                        t.startAt(),
                                        t.endAt(), t.uuid(),
                                        String.format("%s/trips/%s", baseUrl, t.uuid())))
                                .toList();
                        return ResponseEntity.ok().body(new com.fadhlika.lokasi.dto.owntracks.Cmd("response", tours));
                    case "untour":
                        logger.info("request tour deletion");
                        this.tripService.deleteTrip(request.uuid());
                        break;
                }
                break;
            default:
                ObjectMapper mapper = new ObjectMapper();
                logger.info("received unhandled message: {}", mapper.writeValueAsString(message));
                break;
        }

        return ResponseEntity.ok().body(new ArrayList<>());
    }
}
