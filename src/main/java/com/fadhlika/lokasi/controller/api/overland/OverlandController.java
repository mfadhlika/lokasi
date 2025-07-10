package com.fadhlika.lokasi.controller.api.overland;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.fadhlika.lokasi.dto.overland.InputRequest;
import com.fadhlika.lokasi.dto.overland.Properties;
import com.fadhlika.lokasi.dto.overland.Response;
import com.fadhlika.lokasi.model.Location;
import com.fadhlika.lokasi.model.User;
import com.fadhlika.lokasi.service.LocationService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;

@RestController
public class OverlandController {

    private final Logger logger = LoggerFactory.getLogger(OverlandController.class);

    private final LocationService locationService;

    @Autowired
    public OverlandController(LocationService locationService) {
        this.locationService = locationService;
    }

    @PostMapping("/api/overland")
    public Response input(@RequestBody InputRequest input) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        List<Location> locations = input.locations().stream().map(feature -> {
            Location l = new Location();

            l.setUserId(user.getId());

            Properties props = feature.convertProperties(new TypeReference<Properties>() {

            });
            l.setDeviceId(props.deviceId());
            l.setGeometry(feature.getGeometry());
            l.setAltitude(props.altitude());
            l.setBatteryState(props.batteryState());
            l.setBattery(props.batteryLevel());
            l.setMotions(props.motion());
            l.setCourse(props.course());
            l.setCourseAccuracy(props.courseAccuracy());
            l.setAccuracy(props.horizontalAccuracy());
            l.setVerticalAccuracy(props.verticalAccuracy());
            l.setSpeed(props.speed());
            l.setSsid(props.wifi());
            l.setTimestamp(props.timestamp().toLocalDateTime());

            try {
                l.setRawData(input);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }

            return l;
        }).toList();

        locationService.saveLocations(locations);

        return new Response("ok");
    }
}
