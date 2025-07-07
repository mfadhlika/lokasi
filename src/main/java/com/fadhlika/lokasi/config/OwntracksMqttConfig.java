package com.fadhlika.lokasi.config;

import com.fadhlika.lokasi.controller.mqtt.owntracks.OwntracksMqttController;
import com.fadhlika.lokasi.dto.owntracks.Message;
import com.fadhlika.lokasi.service.LocationService;
import com.fadhlika.lokasi.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.endpoint.MessageProducerSupport;
import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
import org.springframework.integration.mqtt.support.DefaultPahoMessageConverter;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Configuration
class OwntracksMqttConfig {

    @Value("${mqtt.server}")
    private String[] servers;

    @Value("${mqtt.username}")
    private String username;

    @Value("${mqtt.password}")
    private String password;

    private final Logger logger = LoggerFactory.getLogger(OwntracksMqttConfig.class);

    private final OwntracksMqttController owntracksMqttController;

    @Autowired
    public OwntracksMqttConfig(LocationService locationService, UserService userService) {
        this.owntracksMqttController = new OwntracksMqttController(locationService, userService);
    }

    @Bean
    public MqttPahoClientFactory mqttClientFactory() {
        DefaultMqttPahoClientFactory factory = new DefaultMqttPahoClientFactory();
        MqttConnectOptions options = new MqttConnectOptions();
        options.setServerURIs(servers);
        options.setUserName(username);
        options.setPassword(password.toCharArray());
        factory.setConnectionOptions(options);
        return factory;
    }

    @Bean
    public MessageProducerSupport mqttLocationInbound() {
        MqttPahoMessageDrivenChannelAdapter adapter = new MqttPahoMessageDrivenChannelAdapter(
                "lokasi",
                mqttClientFactory(),
                "owntracks/+/+"
        );
        adapter.setCompletionTimeout(5000);
        adapter.setConverter(new DefaultPahoMessageConverter());
        adapter.setQos(1);
        return adapter;
    }

    @Bean
    public IntegrationFlow mqttLocationIntegrationFlow() {
        return IntegrationFlow.from(mqttLocationInbound()).transform(m -> {
            ObjectMapper mapper = new ObjectMapper();
            try {
                return mapper.readValue(m.toString(), Message.class);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }).handle(message -> {
            String topic = message.getHeaders().get("mqtt_receivedTopic", String.class);

            String username;
            String deviceId;
            String command;
            try {
                Pattern pattern = Pattern.compile("owntracks/(?<username>[a-zA-Z0-9-_]+)/(?<deviceId>[a-zA-Z0-9-_]+)/?(?<command>[a-zA-Z0-9-_]*)");
                Matcher matcher = pattern.matcher(topic);

                matcher.find();

                username = matcher.group("username");
                deviceId = matcher.group("deviceId");
                command = matcher.group("command");
            } catch (Exception e) {
                logger.error("error extracting topic {}: {}", topic, e.getMessage());
                return;
            }

            switch (command) {
                case "":
                    this.owntracksMqttController.addLocation(username, deviceId, (com.fadhlika.lokasi.dto.owntracks.Location) message.getPayload());
                    break;
                case "cmd":
                    break;
                default:
                    throw new RuntimeException("Unknown command: " + command);
            }
        }).get();
    }
}
