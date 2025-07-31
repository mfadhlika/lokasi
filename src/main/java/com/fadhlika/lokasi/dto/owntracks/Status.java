package com.fadhlika.lokasi.dto.owntracks;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Status(
        @JsonProperty("iOS") StatusIos ios,
        @JsonProperty("android") StatusAndroid android,
        @JsonProperty("_id") String id)
        implements Message {
}
