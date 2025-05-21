package com.fadhlika.lokasi.config;

import com.fadhlika.lokasi.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Random;
import java.util.random.RandomGenerator;

@Configuration
@Order(3)
public class FirstUserGenerator implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(FirstUserGenerator.class);

   @Autowired
   private UserService userService;

    @Override
    public void run(String... args) throws Exception {
        if(userService.hasUser()) {
            return;
        }

        String password = new Random().ints('a', 'z').limit(16).collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append).toString();

        userService.createUser("admin", password);

        logger.info("Created new user with username {} and password: {}", "admin", password);
    }
}
