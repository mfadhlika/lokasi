package com.fadhlika.kelana.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

import com.fadhlika.kelana.service.UserService;
import com.fadhlika.kelana.util.RandomStringGenerator;

@Configuration
@Order(3)
public class AdminGenerator implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(AdminGenerator.class);

    @Value("${admin.username}")
    private String adminUsername;

    @Value("${admin.password}")
    private String adminPassword;

    private final UserService userService;

    AdminGenerator(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void run(String... args) throws Exception {
        if (userService.hasUsers()) {
            return;
        }

        Boolean generateAdminPassword = adminPassword.isBlank();

        if (generateAdminPassword) {
            adminPassword = RandomStringGenerator.generate(16);
        }

        userService.createUser(adminUsername, adminPassword);

        if (generateAdminPassword)
            logger.info("Created new user with username {} and password: {}", adminUsername, adminPassword);
    }
}
