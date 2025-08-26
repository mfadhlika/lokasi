package com.fadhlika.lokasi.dto.owntracks;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

public record Cmd(
                String _type,
                String action,
                @JsonInclude(JsonInclude.Include.NON_NULL) String request,
                @JsonInclude(JsonInclude.Include.NON_NULL) Integer status,
                @JsonInclude(JsonInclude.Include.NON_NULL) Tour tour,
                @JsonInclude(JsonInclude.Include.NON_NULL) List<Tour> tours,
                @JsonInclude(JsonInclude.Include.NON_NULL) Integer ntours,
                @JsonInclude(JsonInclude.Include.NON_NULL) Waypoints waypoints) implements Message {
        public Cmd(
                        String action,
                        List<Tour> tours) {
                this("cmd", action, "tours", null, null, tours, tours.size(), null);
        }

        public Cmd(
                        String action,
                        Integer status,
                        Tour tour) {
                this("cmd", action, "tour", status, tour, null, null, null);
        }

        public Cmd(
                        String action,
                        Waypoints waypoints) {

                this("cmd", action, "tour", null, null, null, null, waypoints);
        }
}
