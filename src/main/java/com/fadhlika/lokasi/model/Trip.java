package com.fadhlika.lokasi.model;

import java.time.ZonedDateTime;
import java.util.List;

public record Trip(
                int userId,
                String title,
                ZonedDateTime startAt,
                ZonedDateTime endAt,
                ZonedDateTime createdAt,
                List<Location> locations) {

        public Trip(
                        int userId,
                        String title,
                        ZonedDateTime startAt,
                        ZonedDateTime endAt,
                        ZonedDateTime createdAt) {
                this(
                                userId,
                                title,
                                startAt,
                                endAt,
                                createdAt,
                                null);
        }

        public Trip(
                        int userId,
                        String title,
                        ZonedDateTime startAt,
                        ZonedDateTime endAt) {
                this(
                                userId,
                                title,
                                startAt,
                                endAt,
                                ZonedDateTime.now(),
                                null);
        }

}
