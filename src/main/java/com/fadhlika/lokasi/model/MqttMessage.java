package com.fadhlika.lokasi.model;

import java.time.ZonedDateTime;

public record MqttMessage(Integer id, String serial, String topic, String payload, Status status, String reason,
        ZonedDateTime createdAt) {
    public enum Status {
        RECEIVED,
        PROCESSED,
        ERROR;
    }
}
