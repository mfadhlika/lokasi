package com.fadhlika.lokasi.integration;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ContextConfiguration;

import com.fadhlika.lokasi.LokasiApplication;
import com.fadhlika.lokasi.config.DatabaseConfigTestContext;
import com.fadhlika.lokasi.dto.Auth;
import com.fadhlika.lokasi.dto.FeatureCollection;
import com.fadhlika.lokasi.dto.LoginRequest;
import com.fadhlika.lokasi.dto.Response;
import com.fadhlika.lokasi.dto.owntracks.Cmd;
import com.fadhlika.lokasi.service.MqttService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = LokasiApplication.class)
@ContextConfiguration(classes = DatabaseConfigTestContext.class)
@DirtiesContext(classMode = ClassMode.BEFORE_EACH_TEST_METHOD)
@TestInstance(Lifecycle.PER_CLASS)
public class OwntraksMqttControllerIntegrationTest {
        private final Logger logger = LoggerFactory.getLogger(OwntraksMqttControllerIntegrationTest.class);

        @Value("${mqtt.server}")
        private String mqttServer;

        @Autowired
        private TestRestTemplate testRestTemplate;

        @Autowired
        private MqttService mqttService;

        @Autowired
        private ObjectMapper mapper;

        private IMqttClient client;

        private String token;

        @BeforeAll
        public void setUp() throws MqttException, JsonMappingException, JsonProcessingException {
                try {
                        client = new MqttClient(mqttServer, "test");

                        client.connect();
                } catch (MqttException e) {
                        logger.error("Failed to connect mqtt broker {}: {}", client.getServerURI(), e.getMessage());
                        throw e;
                }

                LoginRequest login = new LoginRequest("test", "test");

                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);

                HttpEntity<LoginRequest> request = new HttpEntity<>(login, headers);

                ResponseEntity<String> res = testRestTemplate.exchange("/api/v1/login", HttpMethod.POST, request,
                                String.class);

                Response<Auth> loginRes = mapper.readValue(res.getBody(), new TypeReference<Response<Auth>>() {
                });

                token = loginRes.data.accessToken();
        }

        @Test
        public void publishLocation() throws MqttPersistenceException, MqttException, InterruptedException,
                        JsonMappingException, JsonProcessingException {
                MqttMessage msg = new MqttMessage("""
                                {
                                  "_type": "location",
                                  "tid": "my_device_id",
                                  "tst": 1672532200,
                                  "lat": -1.23456,
                                  "lon": 12.34567,
                                  "acc": 10,
                                  "alt": 50,
                                  "batt": 95,
                                  "vel": 15,
                                  "cog": 270
                                }""".getBytes());
                client.publish("owntracks/test/my_device_id", msg);

                Thread.sleep(3000);

                HttpHeaders headers = new HttpHeaders();
                headers.setBearerAuth(token);

                HttpEntity<Void> request = new HttpEntity<>(headers);

                ResponseEntity<String> res = testRestTemplate.exchange(
                                "/api/v1/locations?start=2023-01-01T00:00:00Z&end=2023-01-01T23:59:59Z", HttpMethod.GET,
                                request, String.class);

                assertEquals(res.getStatusCode(), HttpStatusCode.valueOf(200));

                Response<FeatureCollection> locationRes = mapper.readValue(res.getBody(),
                                new TypeReference<Response<FeatureCollection>>() {
                                });

                assertNotNull(locationRes);
                assertNotNull(locationRes.data);
                assertNotNull(locationRes.data.features());
                assertEquals(1, locationRes.data.features().size());

                GeometryFactory gf = new GeometryFactory();
                Geometry geometry = gf.createPoint(new Coordinate(12.34567, -1.23456));

                assertEquals(locationRes.data.features().get(0).getGeometry(), geometry);

                List<com.fadhlika.lokasi.model.MqttMessage> mqttMessages = mqttService.fetchMessages(1, 0);

                assertNotNull(mqttMessages);
                assertEquals(1, mqttMessages.size());
                assertEquals("owntracks/test/my_device_id", mqttMessages.get(0).topic());
                assertEquals(com.fadhlika.lokasi.model.MqttMessage.Status.PROCESSED, mqttMessages.get(0).status());
        }

        @Test
        public void createTour() {
                CountDownLatch lock = new CountDownLatch(1);

                var listener = new IMqttMessageListener() {
                        public Cmd cmd;

                        @Override
                        public void messageArrived(String topic, MqttMessage message) {
                                try {
                                        cmd = mapper.readValue(message.getPayload(), Cmd.class);

                                        client.unsubscribe("owntracks/test/my_device_id/cmd");
                                } catch (Exception e) {
                                        e.printStackTrace();
                                } finally {
                                        lock.countDown();
                                }
                        }
                };

                assertDoesNotThrow(
                                () -> client.subscribe("owntracks/test/my_device_id/cmd", listener));

                MqttMessage msg = new MqttMessage("""
                                {
                                  "_type": "request",
                                  "request": "tour",
                                  "tour": {
                                          "label":"Meeting with C. in Essen",
                                          "from": "2022-08-01T05:35:58",
                                          "to": "2022-08-02T15:00:58"
                                  }
                                }""".getBytes());

                assertDoesNotThrow(() -> client.publish("owntracks/test/my_device_id/request", msg));

                assertDoesNotThrow(() -> assertTrue(lock.await(60, TimeUnit.SECONDS)), "didn't receive message");

                assertNotNull(listener.cmd);
                assertEquals("cmd", listener.cmd._type());
                assertEquals("response", listener.cmd.action());
                assertEquals("tour", listener.cmd.request());
                assertEquals(200, listener.cmd.status());
                assertNotNull(listener.cmd.tour());
                assertEquals("Meeting with C. in Essen", listener.cmd.tour().label());
                assertEquals("2022-08-01T05:35:58", listener.cmd.tour().from());
                assertEquals("2022-08-02T15:00:58", listener.cmd.tour().to());
                assertNotNull(listener.cmd.tour().uuid());

                List<com.fadhlika.lokasi.model.MqttMessage> mqttMessages = mqttService.fetchMessages(10, 0);

                assertNotNull(mqttMessages);
                assertEquals(1, mqttMessages.size());
                assertEquals("owntracks/test/my_device_id/request", mqttMessages.get(0).topic());
                assertEquals(com.fadhlika.lokasi.model.MqttMessage.Status.PROCESSED, mqttMessages.get(0).status());
        }

        @Test
        public void badMessage() throws InterruptedException {
                MqttMessage msg = new MqttMessage("""
                                {
                                  "_type": "request",
                                  "request": "tour",
                                  "tour": {
                                          "label":"Meeting with C. in Essen",
                                          "from": "2022-08-01T05:35:58",
                                          "to": "2022-08-02T15:00:58"
                                }""".getBytes());

                assertDoesNotThrow(() -> client.publish("owntracks/test/my_device_id/request", msg));

                Thread.sleep(3000);

                List<com.fadhlika.lokasi.model.MqttMessage> mqttMessages = mqttService.fetchMessages(10, 0);

                assertNotNull(mqttMessages);
                assertEquals(1, mqttMessages.size());
                assertEquals("owntracks/test/my_device_id/request", mqttMessages.get(0).topic());
                assertEquals(com.fadhlika.lokasi.model.MqttMessage.Status.ERROR, mqttMessages.get(0).status());
                assertNotNull(mqttMessages.get(0).reason());
        }
}
