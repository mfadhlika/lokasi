/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.fadhlika.lokasi.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 *
 * @author fadhl
 */
@Configuration
@Order(1)
public class SpatiaLiteInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(SpatiaLiteInitializer.class);

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public SpatiaLiteInitializer(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void run(String... args) throws Exception {
        try {
            jdbcTemplate.execute("SELECT load_extension('mod_spatialite')");
            logger.info("SpatiaLite extension loaded.");

            jdbcTemplate.execute("SELECT InitSpatialMetadata(1)");
            logger.info("SpatiaLite metadata initialized.");
        } catch (DataAccessException e) {
            logger.error("Error initializing SpatiaLite: {}", e.getMessage(), e);
        }
    }
}
