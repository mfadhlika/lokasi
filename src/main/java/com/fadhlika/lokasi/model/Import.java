package com.fadhlika.lokasi.model;

import java.io.InputStream;
import java.time.ZonedDateTime;

public record Import(
                int id,
                int userId,
                String source,
                String filename,
                InputStream content,
                String checksum,
                boolean done, int count,
                ZonedDateTime created_at) {
        public Import(int userId,
                        String source,
                        String filename,
                        InputStream content,
                        String checksum) {
                this(0, userId, source, filename, content, checksum, false, -1, ZonedDateTime.now());
        }
}
