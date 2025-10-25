package com.fadhlika.lokasi.service;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.mqtt.support.MqttHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;

import com.fadhlika.lokasi.model.MqttMessage;
import com.fadhlika.lokasi.repository.MqttRepository;

@Service
public class MqttService {

    @Autowired
    private MqttRepository mqttRepository;

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
