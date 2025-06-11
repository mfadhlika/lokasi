package com.fadhlika.lokasi.model;

import java.nio.ByteBuffer;
import java.time.LocalDateTime;

public record Import(int userId, String source, String filename, String path, ByteBuffer content, String checksum,
                     boolean done,
                     LocalDateTime created_at) {

}
