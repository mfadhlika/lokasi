package com.fadhlika.lokasi.service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.locationtech.jts.algorithm.MinimumBoundingCircle;
import org.locationtech.jts.geom.Coordinate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fadhlika.lokasi.dto.owntracks.Cmd;
import com.fadhlika.lokasi.dto.owntracks.Message;
import com.fadhlika.lokasi.dto.owntracks.Waypoint;
import com.fadhlika.lokasi.dto.owntracks.Waypoints;
import com.fadhlika.lokasi.model.Region;
import com.fadhlika.lokasi.model.Trip;
import com.fadhlika.lokasi.model.User;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class OwntracksService {

    private final Logger logger = LoggerFactory.getLogger(OwntracksService.class);

    @Value("${lokasi.base_url}")
    private String baseUrl;

    @Autowired
    private LocationService locationService;

    @Autowired
    private TripService tripService;

    @Autowired
    private RegionService regionService;

    public Optional<?> handleMessage(User user, String deviceId, com.fadhlika.lokasi.dto.owntracks.Message message)
            throws JsonProcessingException {
        switch (message) {
            case com.fadhlika.lokasi.dto.owntracks.Location location:
                return this.handleMessageLocation(user, deviceId, location);
            case com.fadhlika.lokasi.dto.owntracks.Request request:
                return this.handleMessageRequest(user, deviceId, request);
            case
                    com.fadhlika.lokasi.dto.owntracks.Waypoint waypoint:
                return this.handleMessageWaypoint(user, deviceId, waypoint);
            default:
                ObjectMapper mapper = new ObjectMapper();
                logger.info("received unhandled message: {}", mapper.writeValueAsString(message));
                return Optional.empty();
        }
    }

    public Optional<List<com.fadhlika.lokasi.dto.owntracks.Message>> handleMessageLocation(User user, String deviceId,
            com.fadhlika.lokasi.dto.owntracks.Location location) throws JsonProcessingException {
        List<Message> res = new ArrayList<>();

        this.locationService.saveLocation(location.toLocation(user.getId(), deviceId));

        List<Waypoint> waypoints = this.regionService.fetchRegions(user.getId()).stream()
                .map(region -> {
                    Coordinate coord = region.getGeometry().getCoordinate();
                    MinimumBoundingCircle circle = new MinimumBoundingCircle(region.getGeometry());

                    return new Waypoint(
                            region.getDesc(),
                            coord.y,
                            coord.x,
                            (int) circle.getRadius(),
                            Math.toIntExact(region.getCreatedAt().toEpochSecond()),
                            region.getBeaconUUID(),
                            region.getBeaconMajor(),
                            region.getBeaconMinor(),
                            region.getRid());
                })
                .toList();
        if (!waypoints.isEmpty())
            res.add(new Cmd("setWaypoints", new Waypoints(null, waypoints)));

        return Optional.of(res);
    }

    public Optional<com.fadhlika.lokasi.dto.owntracks.Message> handleMessageRequest(User user, String deviceId,
            com.fadhlika.lokasi.dto.owntracks.Request request) {
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

                return Optional.of(new com.fadhlika.lokasi.dto.owntracks.Cmd("response", 200,
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
                return Optional.of(new com.fadhlika.lokasi.dto.owntracks.Cmd("response", tours));
            case "untour":
                logger.info("request tour deletion");
                this.tripService.deleteTrip(request.uuid());
                break;
        }

        return Optional.empty();
    }

    public Optional<Void> handleMessageWaypoint(User user, String deviceId,
            com.fadhlika.lokasi.dto.owntracks.Waypoint waypoint) {
        try {
            regionService.createRegion(new Region(user.getId(), waypoint.desc(), waypoint.lat(), waypoint.lon(),
                    waypoint.rad(), waypoint.uuid(), waypoint.major(), waypoint.minor(), waypoint.rid(),
                    Instant.ofEpochSecond(waypoint.tst()).atZone(ZoneOffset.UTC)));

            return Optional.empty();
        } catch (Exception ex) {
            ex.printStackTrace();
            throw ex;
        }
    }
}
