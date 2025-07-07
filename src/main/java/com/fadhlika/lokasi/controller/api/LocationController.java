package com.fadhlika.lokasi.controller.api;

import java.sql.SQLException;
import java.text.DecimalFormat;
import java.time.Duration;
import java.time.LocalDateTime;
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
            @RequestParam(defaultValue = "#{T(java.time.LocalDate).now().atStartOfDay()}")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam(defaultValue = "#{T(java.time.LocalDate).now().atTime(T(java.time.LocalTime).MAX)}")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end,
            @RequestParam Optional<String> device,
            @RequestParam(defaultValue = "false") Optional<Boolean> raw
    ) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        List<Feature> features = new ArrayList<>();
        try {
            List<Location> locations = this.locationService.findLocations(user.getId(), start, end, device);
            for (int i = 1; i < locations.size(); i++) {
                try {
                    Location curr = locations.get(i);

                    HashMap<String, Object> props = new HashMap<>();
                    if (raw.get()) {
                        props.put("device", curr.getDeviceId());
                        props.put("altitude", curr.getAltitude());
                        props.put("course", curr.getCourse());
                        props.put("courseAccuracy", curr.getCourseAccuracy());
                        props.put("battery", curr.getBattery());
                        props.put("batteryStatus", curr.getBatteryState());
                        props.put("timestamp", curr.getTimestamp());
                        features.add(new Feature(curr.getGeometry(), props));
                    } else if (i > 0) {
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
                } catch (Exception e) {
                    logger.error(e.getMessage());
                }
            }
        } catch (SQLException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(new FeatureCollection(features), HttpStatus.OK);
    }
}
