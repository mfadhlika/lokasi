package com.fadhlika.lokasi.controller.api;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.PrecisionModel;
import org.locationtech.jts.geom.Geometry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fadhlika.lokasi.dto.Feature;
import com.fadhlika.lokasi.dto.FeatureCollection;
import com.fadhlika.lokasi.dto.PointProperties;
import com.fadhlika.lokasi.dto.Response;
import com.fadhlika.lokasi.model.Location;
import com.fadhlika.lokasi.model.User;
import com.fadhlika.lokasi.service.LocationService;

@RestController
@RequestMapping("/api/v1/locations")
public class LocationController {

        private static final Logger logger = LoggerFactory.getLogger(LocationController.class);

        @Autowired
        private LocationService locationService;

        @GetMapping
        public Response<FeatureCollection> getLocations(
                        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Optional<ZonedDateTime> start,
                        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Optional<ZonedDateTime> end,
                        @RequestParam Optional<String> device,
                        @RequestParam Optional<String> order,
                        @RequestParam Optional<Boolean> desc,
                        @RequestParam Optional<Integer> offset,
                        @RequestParam Optional<Integer> limit,
                        @RequestParam Optional<double[]> bounds) {
                User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

                Optional<Geometry> rect = bounds.flatMap((b) -> {
                        Envelope env = new Envelope(b[0], b[2], b[1], b[3]);
                        GeometryFactory gf = new GeometryFactory(new PrecisionModel(), 4218);
                        return Optional.of(gf.toGeometry(env));
                });

                List<Feature> features = this.locationService.findLocations(user.getId(), start,
                                end, device, order, desc, offset, limit, rect).stream().map(curr -> {
                                        PointProperties props = new PointProperties(
                                                        curr.getTimestamp(),
                                                        curr.getAltitude(),
                                                        curr.getSpeed(),
                                                        curr.getCourse(),
                                                        curr.getCourseAccuracy(),
                                                        curr.getAccuracy(),
                                                        curr.getVerticalAccuracy(),
                                                        curr.getMotions(),
                                                        curr.getBatteryState().toString(),
                                                        curr.getBattery(),
                                                        curr.getDeviceId(),
                                                        curr.getSsid(),
                                                        curr.getGeocode(),
                                                        curr.getRawData());
                                        return new Feature(curr.getGeometry(), props);
                                }).toList();

                return new Response<>(new FeatureCollection(features));
        }

        @GetMapping("/last")
        public ResponseEntity<Response<Feature>> getLastLocation(
                        @RequestParam Optional<String> device) {
                User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

                try {
                        Location location = this.locationService.findLocation(user.getId(), Optional.empty(),
                                        Optional.empty(), device, Optional.empty(), Optional.of(true)).orElseThrow();

                        PointProperties props = new PointProperties(
                                        location.getTimestamp(),
                                        location.getAltitude(),
                                        location.getSpeed(),
                                        location.getCourse(),
                                        location.getCourseAccuracy(),
                                        location.getAccuracy(),
                                        location.getVerticalAccuracy(),
                                        location.getMotions(),
                                        location.getBatteryState().toString(),
                                        location.getBattery(),
                                        location.getDeviceId(),
                                        location.getSsid(),
                                        location.getGeocode(),
                                        location.getRawData());

                        Feature feature = new Feature(location.getGeometry(), props);
                        return ResponseEntity.ok().body(new Response<>(feature, "success"));
                } catch (NoSuchElementException e) {
                        return ResponseEntity.status(HttpStatus.NO_CONTENT)
                                        .body(new Response<>("last location doesn't exist"));
                }
        }

        @PostMapping("/reverse")
        public Response<?> reverseGeocode() {
                locationService.reverseGeocode();

                return new Response<>("reverse geocode job started");
        }

}
