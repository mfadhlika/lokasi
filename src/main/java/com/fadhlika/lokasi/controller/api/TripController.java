package com.fadhlika.lokasi.controller.api;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.MultiLineString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fadhlika.lokasi.dto.Feature;
import com.fadhlika.lokasi.dto.FeatureCollection;
import com.fadhlika.lokasi.dto.Response;
import com.fadhlika.lokasi.dto.TripProperties;
import com.fadhlika.lokasi.model.Location;
import com.fadhlika.lokasi.model.Trip;
import com.fadhlika.lokasi.model.User;
import com.fadhlika.lokasi.service.TripService;

@RestController
@RequestMapping("/api/v1/trips")
public class TripController {
    @Autowired
    private TripService tripService;

    @PostMapping
    public Response<?> saveTrip(@RequestBody Trip trip) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        trip = new Trip(
                user.getId(),
                trip.title(),
                trip.startAt(),
                trip.endAt(),
                trip.isPublic());

        tripService.saveTrip(trip);

        return new Response<>("trip saved");
    }

    @GetMapping
    public Response<FeatureCollection> getTrips() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        List<Trip> trips = tripService.getTrips(user.getId(), Optional.empty());

        List<Feature> features = new ArrayList<>();
        for (Trip trip : trips) {
            List<Location> locations = trip.locations();

            GeometryFactory gf = new GeometryFactory();
            List<LineString> lineStrings = new ArrayList<>();
            for (int i = 1; i < locations.size(); i++) {
                Location curr = locations.get(i);

                Location prev = locations.get(i - 1);

                Duration duration = Duration.between(prev.getTimestamp(), curr.getTimestamp());

                if (duration.getSeconds() > 15 * 60) {
                    continue;
                }

                Coordinate[] twoPoints = {
                        prev.getGeometry().getCoordinate(),
                        curr.getGeometry().getCoordinate()
                };

                GeometryFactory gf1 = new GeometryFactory();
                lineStrings.add(gf1.createLineString(twoPoints));
            }

            MultiLineString geom = gf.createMultiLineString(lineStrings.toArray(new LineString[0]));

            TripProperties props = new TripProperties(trip.id(), trip.title(), trip.startAt(), trip.endAt());

            features.add(new Feature(geom, props));
        }

        return new Response<>(new FeatureCollection(features));
    }

    @GetMapping("/{uuid:^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$}")
    public Response<Feature> getTripByUUID(@PathVariable UUID uuid) {
        Trip trip = tripService.getTrip(uuid);

        List<Location> locations = trip.locations();

        GeometryFactory gf = new GeometryFactory();
        List<LineString> lineStrings = new ArrayList<>();
        for (int i = 1; i < locations.size(); i++) {
            Location curr = locations.get(i);

            Location prev = locations.get(i - 1);

            Duration duration = Duration.between(prev.getTimestamp(), curr.getTimestamp());

            if (duration.getSeconds() > 15 * 60) {
                continue;
            }

            Coordinate[] twoPoints = {
                    prev.getGeometry().getCoordinate(),
                    curr.getGeometry().getCoordinate()
            };

            GeometryFactory gf1 = new GeometryFactory();
            lineStrings.add(gf1.createLineString(twoPoints));
        }

        MultiLineString geom = gf.createMultiLineString(lineStrings.toArray(new LineString[0]));

        TripProperties props = new TripProperties(trip.id(), trip.title(), trip.startAt(), trip.endAt());

        return new Response<>(new Feature(geom, props));
    }

    @DeleteMapping("/{tripId}")
    public Response<?> deleteTrip(@PathVariable int tripId) {
        tripService.deleteTrip(tripId);

        return new Response<>("trip deleted");
    }
}
