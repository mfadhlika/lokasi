package com.fadhlika.lokasi.controller.api;

import com.fadhlika.lokasi.dto.CreateUserRequest;
import com.fadhlika.lokasi.model.User;
import com.fadhlika.lokasi.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/user")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PutMapping
    public ResponseEntity<Void> updateUser(@RequestBody CreateUserRequest createUserRequest) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        createUserRequest.validate();

        userService.updateUser(user.getId(), createUserRequest.username(), createUserRequest.password());

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/devices")
    public ResponseEntity<List<String>> getUserDevices() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        return new ResponseEntity<>(userService.getUserDevices(user.getId()), HttpStatus.OK);
    }
}
