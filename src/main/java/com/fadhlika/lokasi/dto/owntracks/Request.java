package com.fadhlika.lokasi.dto.owntracks;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName("request")
public record Request(
        String request,
        Tour tour,
        UUID uuid) implements Message {

}
