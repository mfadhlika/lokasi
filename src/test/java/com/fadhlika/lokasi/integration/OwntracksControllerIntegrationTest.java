package com.fadhlika.lokasi.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import com.fadhlika.lokasi.LokasiApplication;
import com.fadhlika.lokasi.config.DatabaseConfigTestContext;
import com.fadhlika.lokasi.dto.owntracks.Cmd;
import com.fadhlika.lokasi.dto.owntracks.Message;
import com.fadhlika.lokasi.dto.owntracks.Request;
import com.fadhlika.lokasi.dto.owntracks.Tour;
import com.fadhlika.lokasi.dto.owntracks.Waypoint;

import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ContextConfiguration;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = LokasiApplication.class)
@ContextConfiguration(classes = DatabaseConfigTestContext.class)
@DirtiesContext(classMode = ClassMode.BEFORE_EACH_TEST_METHOD)
public class OwntracksControllerIntegrationTest {
  @Autowired
  private TestRestTemplate testRestTemplate;

  @Test
  public void publishLocation() throws Exception {

    com.fadhlika.lokasi.dto.owntracks.Location location = new com.fadhlika.lokasi.dto.owntracks.Location(10, 50, 95,
        null, 270, -1.23456,
        12.34567, null, null,
        null, 1672531200, null, 15, null, null, null, null, null, null, null, null, null, null, null, null,
        0,
        null, null);

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setBasicAuth("owntracks", "owntracks");
    headers.add("X-Limit-D", "tes-device");

    HttpEntity<com.fadhlika.lokasi.dto.owntracks.Location> request = new HttpEntity<>(location, headers);

    @SuppressWarnings("unchecked")
    ResponseEntity<ArrayList<Message>> res = testRestTemplate
        .withBasicAuth("owntracks", "owntracks")
        .exchange("/api/owntracks", HttpMethod.POST, request, (Class<ArrayList<Message>>) ((Class) ArrayList.class));

    assertEquals(HttpStatusCode.valueOf(200), res.getStatusCode());

    ArrayList<Message> messages = res.getBody();

    assertNotNull(messages);
    assertEquals(0, messages.size());
  }

  @Test
  public void createTour() throws Exception {
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
    assertEquals(1, cmd.tours().size());
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

    @SuppressWarnings("unchecked")
    ResponseEntity<ArrayList<Message>> res = testRestTemplate
        .withBasicAuth("owntracks", "owntracks")
        .exchange("/api/owntracks", HttpMethod.POST, request, (Class<ArrayList<Message>>) ((Class) ArrayList.class));

    assertEquals(HttpStatusCode.valueOf(200), res.getStatusCode());
  }
}
