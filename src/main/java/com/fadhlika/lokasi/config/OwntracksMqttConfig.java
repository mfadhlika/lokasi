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

    // @Bean
    // public IntegrationFlow mqttInFlow(ClientManager<IMqttAsyncClient,
    // MqttConnectOptions> clientManager) {
    // MqttPahoMessageDrivenChannelAdapter messageProducer = new
    // MqttPahoMessageDrivenChannelAdapter(clientManager,
    // "owntracks/+/+", "owntracks/+/+/cmd", "owntracks/+/+/request");
    // return IntegrationFlow.from(messageProducer).transform(m -> {
    // ObjectMapper mapper = new ObjectMapper();
    // try {
    // return mapper.readValue(m.toString(), Message.class);
    // } catch (JsonProcessingException e) {
    // throw new RuntimeException(e);
    // }
    // }).handle(message -> {
    // String topic = message.getHeaders().get("mqtt_receivedTopic", String.class);

    // logger.info("handle message %s", topic);

    // String username;
    // String deviceId;
    // String command;
    // try {
    // Pattern pattern = Pattern.compile(
    // "owntracks/(?<username>[a-zA-Z0-9-_]+)/(?<deviceId>[a-zA-Z0-9-_]+)/?(?<command>[a-zA-Z0-9-_]*)");
    // Matcher matcher = pattern.matcher(topic);

    // matcher.find();

    // username = matcher.group("username");
    // deviceId = matcher.group("deviceId");
    // command = matcher.group("command");
    // } catch (Exception e) {
    // logger.error("error extracting topic {}: {}", topic, e.getMessage());
    // return;
    // }

    // this.owntracksMqttController.handleMessage(command, username, deviceId,
    // (com.fadhlika.lokasi.dto.owntracks.Message) message.getPayload());
    // }).get();

    // }

    // @Bean
    // @ServiceActivator(inputChannel = "mqttOutboundChannel")
    // public IntegrationFlow mqttOutFlow(
    // ClientManager<IMqttAsyncClient, MqttConnectOptions> clientManager) {

    // return f -> f.handle(new MqttPahoMessageHandler(clientManager));
    // }

    @Bean
    public MessageChannel mqttOutboundChannel() {
        return new DirectChannel();
    }

    @Bean
    public MessageChannel mqttInboundChannel() {
        return new DirectChannel();
    }
}
