/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.fadhlika.lokasi.controller.api.owntracks;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fadhlika.lokasi.dto.owntracks.Message;
import com.fadhlika.lokasi.model.User;
import com.fadhlika.lokasi.service.OwntracksMqttService;
import com.fasterxml.jackson.core.JsonProcessingException;

/**
 *
 * @author fadhl
 */
@RestController
@RequestMapping("/api/owntracks")
public class OwntracksController {

        private final Logger logger = LoggerFactory.getLogger(OwntracksController.class);

        @Value("${lokasi.base_url}")
        private String baseUrl;

        @Autowired
        private OwntracksMqttService owntracksMqttService;

        @PostMapping
        public ResponseEntity<?> pub(@RequestHeader("X-Limit-D") String deviceId,
                        @RequestBody(required = false) Message message)
                        throws JsonProcessingException {
                User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

                Optional<?> res = this.owntracksMqttService.handleMessage(user, deviceId, message);

                return ResponseEntity.ok().body(res);
        }
}
