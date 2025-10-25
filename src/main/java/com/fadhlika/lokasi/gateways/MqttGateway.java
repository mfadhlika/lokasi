package com.fadhlika.lokasi.gateways;

import org.eclipse.paho.client.mqttv3.MqttException;
import org.springframework.integration.annotation.MessagingGateway;
import org.springframework.integration.mqtt.support.MqttHeaders;
import org.springframework.messaging.handler.annotation.Header;

@MessagingGateway(defaultRequestChannel = "mqttOutboundChannel")
public interface MqttGateway {
    void publish(@Header(MqttHeaders.TOPIC) String topic, String payload) throws MqttException;
}
