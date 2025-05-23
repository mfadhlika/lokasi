package com.fadhlika.lokasi.dto.owntracks;

import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName("lwt")
public record Lwt(int tst) implements Message {
}
