package com.fadhlika.lokasi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication(
        exclude = {SecurityAutoConfiguration.class})
public class LokasiApplication {

    public static void main(String[] args) {
        SpringApplication.run(LokasiApplication.class, args);
    }

}
