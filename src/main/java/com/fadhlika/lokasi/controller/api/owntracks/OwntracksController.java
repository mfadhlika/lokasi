/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.fadhlika.lokasi.controller.api.owntracks;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import com.fadhlika.lokasi.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fadhlika.lokasi.dto.owntracks.Location;
import com.fadhlika.lokasi.dto.owntracks.Message;
import com.fadhlika.lokasi.model.Point;
import com.fadhlika.lokasi.service.PointService;
import com.fasterxml.jackson.core.JsonProcessingException;

/**
 *
 * @author fadhl
 */
@RestController
@RequestMapping("/api/owntracks")
public class OwntracksController {

    private final Logger logger = LoggerFactory.getLogger(OwntracksController.class);

    private final PointService pointService;

    @Autowired
    public OwntracksController(PointService pointService) {
        this.pointService = pointService;
    }

    @PostMapping
    public void pub(@RequestBody Message message) throws JsonProcessingException {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (message instanceof Location location) {
            Point point = new Point(user.getId(), location.lon, location.lat, Instant.ofEpochSecond(location.tst).atOffset(ZoneOffset.UTC).toLocalDateTime());
            this.pointService.createPoint(point);
        }
    }
}
