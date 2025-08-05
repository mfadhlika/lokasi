/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.fadhlika.lokasi.controller.api.owntracks;

import java.time.Instant;
import java.time.ZoneOffset;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fadhlika.lokasi.dto.owntracks.Message;
import com.fadhlika.lokasi.model.User;
import com.fadhlika.lokasi.service.LocationService;
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

    private final LocationService locationService;

    @Autowired
    public OwntracksController(LocationService locationService) {
        this.locationService = locationService;
    }

    @PostMapping
    public void pub(@RequestHeader("X-Limit-D") String deviceId, @RequestBody Message message)
            throws JsonProcessingException {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        switch (message) {
            case com.fadhlika.lokasi.dto.owntracks.Location location:
                this.locationService.saveLocation(location.toLocation(user.getId(), deviceId));
                break;
            case com.fadhlika.lokasi.dto.owntracks.Status status:
                ObjectMapper mapper = new ObjectMapper();
                logger.info(mapper.writeValueAsString(status));
            default:
                break;
        }
    }
}
