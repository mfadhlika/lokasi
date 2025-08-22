package com.fadhlika.lokasi.dto.owntracks;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName("tour")
public record Request(
        String request,
        Tour tour,
        UUID uuid) implements Message {

}
