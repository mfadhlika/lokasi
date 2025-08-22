package com.fadhlika.lokasi.controller.mqtt.owntracks;

import com.fadhlika.lokasi.model.User;
import com.fadhlika.lokasi.service.LocationService;
import com.fadhlika.lokasi.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.MessagingException;

public class OwntracksMqttController {
    private final Logger logger = LoggerFactory.getLogger(OwntracksMqttController.class);

    private final LocationService locationService;

    private final UserService userService;

    public OwntracksMqttController(LocationService locationService, UserService userService) {
        this.locationService = locationService;
        this.userService = userService;
    }

    public void addLocation(String username, String deviceId, com.fadhlika.lokasi.dto.owntracks.Location message)
            throws MessagingException {
        User user = (User) userService.loadUserByUsername(username);

        try {
            this.locationService.saveLocation(message.toLocation(user.getId(), deviceId));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
