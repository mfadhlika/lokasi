package com.fadhlika.lokasi.controller.mqtt.owntracks;

import com.fadhlika.lokasi.dto.owntracks.Cmd;
import com.fadhlika.lokasi.model.Trip;
import com.fadhlika.lokasi.model.User;
import com.fadhlika.lokasi.service.LocationService;
import com.fadhlika.lokasi.service.TripService;
import com.fadhlika.lokasi.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.mqtt.support.MqttHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

@Service
public class OwntracksMqttController {
    private final Logger logger = LoggerFactory.getLogger(OwntracksMqttController.class);

    @Value("${lokasi.base_url}")
    private String baseUrl;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private LocationService locationService;

    @Autowired
    private UserService userService;

    @Autowired
    private TripService tripService;

    @Autowired
    private MessageChannel mqttOutboundChannel;

    @ServiceActivator(inputChannel = "mqttInboundChannel")
    public void handleMessage(String payload, @Header(MqttHeaders.RECEIVED_TOPIC) String topic)
            throws JsonMappingException, JsonProcessingException {
        logger.info("handle message %s", topic);

        String username;
        String deviceId;
        String command;
        Pattern pattern = Pattern.compile(
                "owntracks/(?<username>[a-zA-Z0-9-_]+)/(?<deviceId>[a-zA-Z0-9-_]+)/?(?<command>[a-zA-Z0-9-_]*)");
        Matcher matcher = pattern.matcher(topic);

        matcher.find();

        username = matcher.group("username");
        deviceId = matcher.group("deviceId");
        command = matcher.group("command");

        User user = (User) userService.loadUserByUsername(username);

        com.fadhlika.lokasi.dto.owntracks.Message message = mapper.readValue(payload,
                com.fadhlika.lokasi.dto.owntracks.Message.class);

        switch (message) {
            case com.fadhlika.lokasi.dto.owntracks.Location location:
                try {
                    this.locationService.saveLocation(location.toLocation(user.getId(), deviceId));
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }

                break;
            case com.fadhlika.lokasi.dto.owntracks.Request request:
                Trip trip = this.tripService.saveTrip(
                        new Trip(user.getId(), request.tour().label(),
                                LocalDateTime
                                        .parse(request.tour().from(), DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                                        .atZone(ZoneOffset.UTC),
                                LocalDateTime.parse(request.tour().to(), DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                                        .atZone(ZoneOffset.UTC),
                                true));

                Cmd cmd = new com.fadhlika.lokasi.dto.owntracks.Cmd("response", 200,
                        new com.fadhlika.lokasi.dto.owntracks.Tour(trip.title(),
                                trip.startAt(),
                                trip.endAt(), trip.uuid(),
                                String.format("%s/trips/%s", baseUrl, trip.uuid())));

                try {
                    String res = mapper.writeValueAsString(cmd);
                    Message<String> resMessage = MessageBuilder.withPayload(res).setHeader(MqttHeaders.TOPIC,
                            String.format("owntracks/%s/%s/cmd", username, deviceId)).build();
                    mqttOutboundChannel.send(resMessage);
                } catch (JsonProcessingException e) {
                    logger.error("failed to serialize tour creation response", e);
                }
            default:
                break;
        }

    }
}
