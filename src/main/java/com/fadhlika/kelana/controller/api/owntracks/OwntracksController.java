/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.fadhlika.kelana.controller.api.owntracks;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fadhlika.kelana.dto.owntracks.Message;
import com.fadhlika.kelana.model.User;
import com.fadhlika.kelana.service.OwntracksService;
import com.fasterxml.jackson.core.JsonProcessingException;

/**
 *
 * @author fadhl
 */
@RestController
@RequestMapping("/api/owntracks")
public class OwntracksController {

        @SuppressWarnings("unused")
        private final Logger logger = LoggerFactory.getLogger(OwntracksController.class);

        @Value("${kelana.base_url}")
        private String baseUrl;

        private final OwntracksService owntracksService;

        OwntracksController(OwntracksService owntracksService) {
                this.owntracksService = owntracksService;
        }

        @PostMapping
        public ResponseEntity<?> pub(@RequestHeader("X-Limit-D") String deviceId,
                        @RequestBody(required = false) Message message)
                        throws JsonProcessingException {
                User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

                Optional<?> res = this.owntracksService.handleMessage(user, deviceId, message);

                return ResponseEntity.ok().body(res);
        }
}
