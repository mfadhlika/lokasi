package com.fadhlika.kelana.service;

import java.util.List;
import java.util.UUID;

import org.springframework.integration.mqtt.support.MqttHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;

import com.fadhlika.kelana.model.MqttMessage;
import com.fadhlika.kelana.repository.MqttRepository;

@Service
public class MqttService {

    private final MqttRepository mqttRepository;

    MqttService(MqttRepository mqttRepository) {
        this.mqttRepository = mqttRepository;
    }

    public UUID saveMessage(String payload, @Header(MqttHeaders.RECEIVED_TOPIC) String topic) {
        UUID uuid = UUID.randomUUID();

        mqttRepository.createMessage(uuid, topic, payload);

        return uuid;
    }

    public void updateMessageStatus(UUID uuid, MqttMessage.Status status) {
        mqttRepository.updateMessageStatus(uuid, status);
    }

    public void updateMessageStatus(UUID uuid, MqttMessage.Status status, String reason) {
        mqttRepository.updateMessageStatus(uuid, status, reason);
    }

    public List<MqttMessage> fetchMessages(Integer limit, Integer offset) {
        return mqttRepository.fetchMessages(limit, offset);
    }
}
