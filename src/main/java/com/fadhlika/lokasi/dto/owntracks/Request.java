package com.fadhlika.lokasi.dto.owntracks;

import java.util.UUID;

public record Request(
                String request,
                Tour tour,
                UUID uuid) implements Message {

}
