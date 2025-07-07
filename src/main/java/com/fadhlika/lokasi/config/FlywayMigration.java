/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.fadhlika.lokasi.config;

import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

/**
 *
 * @author fadhl
 */
@Configuration
@Order(2)
public class FlywayMigration implements CommandLineRunner {

    private final Flyway flyway;

    @Autowired
    public FlywayMigration(Flyway flyway) {
        this.flyway = flyway;
    }

    @Override
    public void run(String... args) throws Exception {
        flyway.baseline();
        flyway.migrate();
    }
}
