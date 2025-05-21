/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.fadhlika.lokasi.config;

import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.flyway.FlywayMigrationInitializer;
import org.springframework.boot.autoconfigure.flyway.FlywayMigrationStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 *
 * @author fadhl
 */
@Configuration
public class FlywayConfig {
    private class MyFlywayMigrationInitializer extends FlywayMigrationInitializer {

        public MyFlywayMigrationInitializer(Flyway flyway, FlywayMigrationStrategy migrationStrategy) {
            super(flyway, migrationStrategy);
        }

        @Override
        public void afterPropertiesSet() throws Exception {
            // NO-OP
        }
    }

    @Bean
    public FlywayMigrationInitializer getFlywayInitializer(Flyway flyway, ObjectProvider<FlywayMigrationStrategy> migrationStrategy) {
        return new MyFlywayMigrationInitializer(flyway, migrationStrategy.getIfAvailable());
    }
}
