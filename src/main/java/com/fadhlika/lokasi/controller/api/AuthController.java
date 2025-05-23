package com.fadhlika.lokasi.controller.api;

import com.fadhlika.lokasi.dto.ErrorResponse;
import com.fadhlika.lokasi.dto.LoginRequest;
import com.fadhlika.lokasi.dto.LoginResponse;
import com.fadhlika.lokasi.dto.Response;
import com.fadhlika.lokasi.service.JwtAuthService;
import com.fadhlika.lokasi.service.UserService;
import org.apache.juli.logging.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final Logger logger = LoggerFactory.getLogger(AuthController.class);

    private final JwtAuthService jwtAuthService;

    private final UserService userService;

    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AuthController(UserService userService, JwtAuthService jwtAuthService,PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.jwtAuthService = jwtAuthService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("token")
    public Response login(@RequestBody LoginRequest loginRequest) {
        try {
            UserDetails user = this.userService.loadUserByUsername(loginRequest.username());

            if(!passwordEncoder.matches(loginRequest.password(), user.getPassword())) {
                return new ErrorResponse("Wrong password");
            }

            String token = jwtAuthService.generateToken(user.getUsername());

            return new LoginResponse(token);
        } catch (UsernameNotFoundException e) {
            return new ErrorResponse(e.getMessage());
        }
    }
}
