package com.fadhlika.lokasi.exception;

public class UnhandledMqttMessage extends RuntimeException {

    public UnhandledMqttMessage(String message) {
        super(message);
    }
}
