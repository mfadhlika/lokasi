package com.fadhlika.lokasi.model;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

public record Trip(
                int id,
                int userId,
                String title,
                ZonedDateTime startAt,
                ZonedDateTime endAt,
                ZonedDateTime createdAt,
                List<Location> locations,
                UUID uuid,
                boolean isPublic) {

        public Trip(
                        int userId,
                        String title,
                        ZonedDateTime startAt,
                        ZonedDateTime endAt) {
                this(0,
                                userId,
                                title,
                                startAt,
                                endAt,
                                ZonedDateTime.now(),
                                null,
                                UUID.randomUUID(),
                                false);
        }

        public Trip(
                        int userId,
                        String title,
                        ZonedDateTime startAt,
                        ZonedDateTime endAt,
                        UUID uuid,
                        boolean isPublic) {
                this(0,
                                userId,
                                title,
                                startAt,
                                endAt,
                                ZonedDateTime.now(),
                                null,
                                uuid,
                                isPublic);
        }

        public Trip(
                        int userId,
                        String title,
                        ZonedDateTime startAt,
                        ZonedDateTime endAt,
                        boolean isPublic) {
                this(0,
                                userId,
                                title,
                                startAt,
                                endAt,
                                ZonedDateTime.now(),
                                null,
                                UUID.randomUUID(),
                                isPublic);
        }
}
