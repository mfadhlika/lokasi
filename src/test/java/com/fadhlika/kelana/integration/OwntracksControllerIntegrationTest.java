package com.fadhlika.kelana.integration;

import org.eclipse.paho.client.mqttv3.MqttException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureTestRestTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import com.fadhlika.kelana.KelanaApplication;
import com.fadhlika.kelana.dto.Auth;
import com.fadhlika.kelana.dto.FeatureCollection;
import com.fadhlika.kelana.dto.LoginRequest;
import com.fadhlika.kelana.dto.Response;
import com.fadhlika.kelana.dto.owntracks.Cmd;
import com.fadhlika.kelana.dto.owntracks.Message;
import com.fadhlika.kelana.dto.owntracks.Request;
import com.fadhlika.kelana.dto.owntracks.Tour;
import com.fadhlika.kelana.dto.owntracks.Waypoint;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

import tools.jackson.core.StreamReadFeature;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;

import org.springframework.test.context.TestPropertySource;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = KelanaApplication.class)
@TestPropertySource(locations = "classpath:test.properties")
@TestInstance(Lifecycle.PER_CLASS)
@AutoConfigureTestRestTemplate
public class OwntracksControllerIntegrationTest {
  @Autowired
  private TestRestTemplate testRestTemplate;

  private ObjectMapper mapper = JsonMapper.builder()
      .enable(StreamReadFeature.INCLUDE_SOURCE_IN_LOCATION)
      .build();

  private String token;

  @BeforeAll
  public void setUp() throws MqttException, JsonMappingException, JsonProcessingException {
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

  private FeatureCollection getTrips() {
    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(token);

    HttpEntity<Void> request = new HttpEntity<>(headers);

    ResponseEntity<String> res = testRestTemplate.exchange(
        "/api/v1/trips?limit=100", HttpMethod.GET,
        request, String.class);

    assertEquals(HttpStatusCode.valueOf(200), res.getStatusCode(), res.getBody());

    Response<FeatureCollection> tripRes = mapper.readValue(res.getBody(),
        new TypeReference<Response<FeatureCollection>>() {
        });

    return tripRes.data;
  }

  @Test
  public void publishLocation() throws Exception {
    com.fadhlika.kelana.dto.owntracks.Location location = new com.fadhlika.kelana.dto.owntracks.Location(10, 50, 95,
        null, 270, -1.23456,
        12.34567, null, null,
        "NE", 1672531200, 30, 15, 100.664, null, null, null, "w", null, null, null, null, new ArrayList<>() {
          {
            add("driving");
          }
        }, null, null,
        0,
        1, null);

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setBasicAuth("owntracks", "owntracks");
    headers.add("X-Limit-D", "tes-device");

    HttpEntity<com.fadhlika.kelana.dto.owntracks.Location> request = new HttpEntity<>(location, headers);

    @SuppressWarnings({ "unchecked", "rawtypes" })
    ResponseEntity<ArrayList<Message>> res = testRestTemplate
        .withBasicAuth("owntracks", "owntracks")
        .exchange("/api/owntracks", HttpMethod.POST, request, (Class<ArrayList<Message>>) ((Class) ArrayList.class));

    assertEquals(HttpStatusCode.valueOf(200), res.getStatusCode());
  }

  @Test
  public void createTour() throws Exception {
    FeatureCollection trips = getTrips();
    int initialTripSize = trips.features().size();

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setBasicAuth("owntracks", "owntracks");
    headers.add("X-Limit-D", "tes-device");

    Tour tour = new Tour("Meeting with C. in Essen", LocalDateTime.parse("2022-08-01T05:35:58").atZone(ZoneOffset.UTC),
        LocalDateTime.parse("2022-08-02T15:00:58").atZone(ZoneOffset.UTC), null, null);

    Request req = new Request("tour", tour, null);

    HttpEntity<Request> request = new HttpEntity<>(req, headers);

    ResponseEntity<Cmd> res = testRestTemplate
        .withBasicAuth("owntracks", "owntracks")
        .exchange("/api/owntracks", HttpMethod.POST, request, Cmd.class);

    assertEquals(HttpStatusCode.valueOf(200), res.getStatusCode());

    Message msg = res.getBody();

    Cmd cmd = (Cmd) msg;

    assertNotNull(cmd);
    // assertEquals("cmd", cmd._type()); // _type not deserialized even though it's
    // there on the payload
    assertEquals("response", cmd.action());
    assertEquals("tour", cmd.request());
    assertEquals(200, cmd.status());
    assertEquals("Meeting with C. in Essen", cmd.tour().label());
    assertEquals("2022-08-01T05:35:58", cmd.tour().from());
    assertEquals("2022-08-02T15:00:58", cmd.tour().to());

    req = new Request("tours", null, null);

    request = new HttpEntity<>(req, headers);

    res = testRestTemplate
        .withBasicAuth("owntracks", "owntracks")
        .exchange("/api/owntracks", HttpMethod.POST, request, Cmd.class);

    assertEquals(HttpStatusCode.valueOf(200), res.getStatusCode());

    msg = res.getBody();

    cmd = (Cmd) msg;

    assertNotNull(cmd);
    // assertEquals("cmd", cmd._type()); // _type not deserialized even though it's
    // there on the payload
    assertEquals("response", cmd.action());
    assertEquals("tours", cmd.request());
    assertEquals(initialTripSize + 1, cmd.tours().size());
    assertEquals("Meeting with C. in Essen", cmd.tours().get(0).label());
    assertEquals("2022-08-01T05:35:58", cmd.tours().get(0).from());
    assertEquals("2022-08-02T15:00:58", cmd.tours().get(0).to());
  }

  @Test
  public void publishWaypoint() throws Exception {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setBasicAuth("owntracks", "owntracks");
    headers.add("X-Limit-D", "tes-device");

    Waypoint waypoint = new Waypoint("Here-4a23e5", -1.23456, 12.34567, 10, 1756003551, null, null, null, "4a23e5");

    HttpEntity<Waypoint> request = new HttpEntity<>(waypoint, headers);

    @SuppressWarnings({ "unchecked", "rawtypes" })
    ResponseEntity<ArrayList<Message>> res = testRestTemplate
        .withBasicAuth("owntracks", "owntracks")
        .exchange("/api/owntracks", HttpMethod.POST, request,
            (Class<ArrayList<Message>>) ((Class) ArrayList.class));

    assertEquals(HttpStatusCode.valueOf(200), res.getStatusCode());
  }
}
