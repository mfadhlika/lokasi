package com.fadhlika.lokasi.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import com.fadhlika.lokasi.LokasiApplication;
import com.fadhlika.lokasi.config.DatabaseConfigTestContext;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK, classes = LokasiApplication.class)
@ContextConfiguration(classes = DatabaseConfigTestContext.class)
@AutoConfigureMockMvc
public class OwntracksControllerIntegrationTest {
  @Autowired
  private MockMvc mvc;

  @Test
  public void publishLocation() throws Exception {
    mvc.perform(post("/api/owntracks")
        .header("Authorization", "Basic b3dudHJhY2tzOm93bnRyYWNrcw==")
        .header("X-Limit-D", "tes-device")
        .contentType(MediaType.APPLICATION_JSON)
        .content("""
            {
              "_type": "location",
              "tid": "my_device_id",
              "tst": 1672531200,
              "lat": -1.23456,
              "lon": 12.34567,
              "acc": 10,
              "alt": 50,
              "batt": 95,
              "vel": 15,
              "cog": 270
            }"""))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(0)));
  }

  @Test
  public void createTour() throws Exception {
    mvc.perform(post("/api/owntracks")
        .header("Authorization", "Basic b3dudHJhY2tzOm93bnRyYWNrcw==")
        .header("X-Limit-D", "tes-device")
        .contentType(MediaType.APPLICATION_JSON)
        .content("""
            {
              "_type": "request",
              "request": "tour",
              "tour": {
                "label": "Meeting with C. in Essen",
                "from": "2022-08-01T05:35:58",
                "to": "2022-08-02T15:00:58"
              }
            }"""))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$._type", equalTo("cmd")))
        .andExpect(jsonPath("$.action", equalTo("response")))
        .andExpect(jsonPath("$.request", equalTo("tour")))
        .andExpect(jsonPath("$.status", equalTo(200)))
        .andExpect(jsonPath("$.tour.label", equalTo("Meeting with C. in Essen")))
        .andExpect(jsonPath("$.tour.from", equalTo("2022-08-01T05:35:58")))
        .andExpect(jsonPath("$.tour.to", equalTo("2022-08-02T15:00:58")));

    mvc.perform(post("/api/owntracks")
        .header("Authorization", "Basic b3dudHJhY2tzOm93bnRyYWNrcw==")
        .header("X-Limit-D", "tes-device")
        .contentType(MediaType.APPLICATION_JSON)
        .content("""
            {
              "_type": "request",
              "request": "tours"
            }"""))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$._type", equalTo("cmd")))
        .andExpect(jsonPath("$.action", equalTo("response")))
        .andExpect(jsonPath("$.request", equalTo("tours")))
        .andExpect(jsonPath("$.ntours", equalTo(1)))
        .andExpect(jsonPath("$.tours[0].label", equalTo("Meeting with C. in Essen")))
        .andExpect(jsonPath("$.tours[0].from", equalTo("2022-08-01T05:35:58")))
        .andExpect(jsonPath("$.tours[0].to", equalTo("2022-08-02T15:00:58")));

  }

  @Test
  public void publishWaypoint() throws Exception {
    mvc.perform(post("/api/owntracks")
        .header("Authorization", "Basic b3dudHJhY2tzOm93bnRyYWNrcw==")
        .header("X-Limit-D", "tes-device")
        .contentType(MediaType.APPLICATION_JSON)
        .content("""
            {
                "_type": "waypoint",
                "desc": "Here-4a23e5",
                "lat": -1.23456,
                "lon": 12.34567,
                "rad": 10,
                "tst": 1756003551,
                "uuid": null,
                "major": null,
                "minor": null,
                "rid": "4a23e5"
            }"""))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(0)));
  }
}
