package com.fadhlika.lokasi.dto.owntracks;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

public record Cmd(
                String action,
                @JsonInclude(JsonInclude.Include.NON_NULL) String request,
                @JsonInclude(JsonInclude.Include.NON_NULL) Integer status,
                @JsonInclude(JsonInclude.Include.NON_NULL) Tour tour,
                @JsonInclude(JsonInclude.Include.NON_NULL) List<Tour> tours) {
        public Cmd(
                        String action,
                        Integer status,
                        List<Tour> tours) {
                this(action, "tours", status, null, tours);
        }

        public Cmd(
                        String action,
                        Integer status,
                        Tour tour) {
                this(action, "tour", status, tour, null);
        }

}
