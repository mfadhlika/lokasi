package com.fadhlika.kelana.controller.api;

import com.fadhlika.kelana.dto.CreateUserRequest;
import com.fadhlika.kelana.dto.Response;
import com.fadhlika.kelana.model.User;
import com.fadhlika.kelana.service.UserService;

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

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PutMapping
    public Response<Void> updateUser(@RequestBody CreateUserRequest createUserRequest) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        createUserRequest.validate();

        userService.updateUser(user.getId(), createUserRequest.username(), createUserRequest.password());

        return new Response<>("user updated");
    }

    @GetMapping("/devices")
    public Response<List<String>> getUserDevices() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        List<String> devices = userService.getUserDevices(user.getId());
        return new Response<>(devices);
    }
}
