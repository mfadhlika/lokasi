package com.fadhlika.lokasi.controller.api;

import java.text.DecimalFormat;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fadhlika.lokasi.dto.Feature;
import com.fadhlika.lokasi.dto.FeatureCollection;
import com.fadhlika.lokasi.dto.Properties;
import com.fadhlika.lokasi.model.Location;
import com.fadhlika.lokasi.model.User;
import com.fadhlika.lokasi.service.LocationService;

@RestController
@RequestMapping("/api/v1/locations")
public class LocationController {

    private static final Logger logger = LoggerFactory.getLogger(LocationController.class);

    private final LocationService locationService;

    @Autowired
    public LocationController(LocationService locationService) {
        this.locationService = locationService;
    }

    @GetMapping
    public ResponseEntity<FeatureCollection> getLocations(
            @RequestParam(defaultValue = "#{T(java.time.LocalDate).now().atStartOfDay().atZone(ZoneOffset.UTC)}")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) ZonedDateTime start,
            @RequestParam(defaultValue = "#{T(java.time.LocalDate).now().atTime(T(java.time.LocalTime).MAX).atZone(ZoneOffset.UTC)}")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) ZonedDateTime end,
            @RequestParam Optional<String> device
    ) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        List<Feature> features = new ArrayList<>();
        List<Location> locations = this.locationService.findLocations(user.getId(), start, end, device);
        for (int i = 1; i < locations.size(); i++) {
            Location curr = locations.get(i);

            HashMap<String, Object> props = new HashMap<>();

            Location prev = locations.get(i - 1);

            Duration duration = Duration.between(prev.getTimestamp(), curr.getTimestamp());

            if (duration.getSeconds() > 15 * 60) {
                continue;
            }

            Coordinate[] twoPoints = {
                prev.getGeometry().getCoordinate(),
                curr.getGeometry().getCoordinate()
            };

            GeometryFactory gf = new GeometryFactory();
            LineString ls = gf.createLineString(twoPoints);

            Double distance = curr.getDistanceInMeters(prev);
            Double speed = (distance / 1000.0) / (duration.getSeconds() / 3600.0);

            DecimalFormat df = new DecimalFormat("0.##");

            props.put("distance", df.format(distance));
            props.put("speed", df.format(speed));
            props.put("startAt", curr.getTimestamp());
            props.put("endAt", prev.getTimestamp());

            features.add(new Feature(ls, props));
        }

        return new ResponseEntity<>(new FeatureCollection(features), HttpStatus.OK);
    }

    @GetMapping("/raw")
    public ResponseEntity<FeatureCollection> getRawLocations(
            @RequestParam(defaultValue = "#{T(java.time.LocalDate).now().atStartOfDay().atZone(ZoneOffset.UTC)}")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) ZonedDateTime start,
            @RequestParam(defaultValue = "#{T(java.time.LocalDate).now().atTime(T(java.time.LocalTime).MAX).atZone(ZoneOffset.UTC)}")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) ZonedDateTime end,
            @RequestParam Optional<String> device,
            @RequestParam(defaultValue = "0") Optional<Integer> offset,
            @RequestParam(defaultValue = "25") Optional<Integer> limit
    ) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        List<Feature> features = new ArrayList<>();
        List<Location> locations = this.locationService.findLocations(user.getId(), start, end, device, offset, limit);
        for (int i = 1; i < locations.size(); i++) {
            Location curr = locations.get(i);

            Properties props = new Properties(
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
                    curr.getRawData()
            );
            features.add(new Feature(curr.getGeometry(), props));
        }

        return new ResponseEntity<>(new FeatureCollection(features), HttpStatus.OK);
    }
}
