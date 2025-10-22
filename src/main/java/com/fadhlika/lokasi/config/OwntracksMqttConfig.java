package com.fadhlika.lokasi.config;

import org.eclipse.paho.client.mqttv3.IMqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.persist.MqttDefaultFilePersistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.mqtt.core.ClientManager;
import org.springframework.integration.mqtt.core.Mqttv3ClientManager;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
import org.springframework.integration.mqtt.outbound.MqttPahoMessageHandler;
import org.springframework.messaging.MessageChannel;

@Configuration
@ConditionalOnProperty(value = "mqtt.enable", havingValue = "true")
class OwntracksMqttConfig {

    @Value("${mqtt.server}")
    private String[] servers;

    @Value("${mqtt.username}")
    private String username;

    @Value("${mqtt.password}")
    private String password;

    private final Logger logger = LoggerFactory.getLogger(OwntracksMqttConfig.class);

    @Bean
    public ClientManager<IMqttAsyncClient, MqttConnectOptions> clientManager() {
        MqttConnectOptions options = new MqttConnectOptions();
        options.setServerURIs(servers);
        if (!username.isEmpty())
            options.setUserName(username);
        if (!password.isEmpty())
            options.setPassword(password.toCharArray());
        Mqttv3ClientManager clientManager = new Mqttv3ClientManager(options, "lokasi");
        clientManager.setPersistence(new MqttDefaultFilePersistence());
        return clientManager;
    }

    @Bean
    public MqttPahoMessageDrivenChannelAdapter inboundAdapter(
            ClientManager<IMqttAsyncClient, MqttConnectOptions> mqttClientManager) {
        MqttPahoMessageDrivenChannelAdapter adapter = new MqttPahoMessageDrivenChannelAdapter(mqttClientManager,
                "owntracks/+/+", "owntracks/+/+/cmd", "owntracks/+/+/request");
        adapter.setOutputChannel(mqttInboundChannel());
        return adapter;
    }

    @Bean
    @ServiceActivator(inputChannel = "mqttOutboundChannel")
    public MqttPahoMessageHandler outboundAdapter(
            ClientManager<IMqttAsyncClient, MqttConnectOptions> mqttClientManager) {
        MqttPahoMessageHandler handler = new MqttPahoMessageHandler(mqttClientManager);
        return handler;
    }

    @Bean
    public MessageChannel mqttOutboundChannel() {
        return new DirectChannel();
    }

    @Bean
    public MessageChannel mqttInboundChannel() {
        return new DirectChannel();
    }
}
