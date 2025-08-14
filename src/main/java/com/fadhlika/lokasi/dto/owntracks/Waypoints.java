package com.fadhlika.lokasi.dto.owntracks;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Waypoints(
        @JsonProperty("_created") String creator,
        List<Waypoint> waypoints) implements Message {

}
