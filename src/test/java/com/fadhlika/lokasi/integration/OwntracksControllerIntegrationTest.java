package com.fadhlika.lokasi.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import com.fadhlika.lokasi.LokasiApplication;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK, classes = LokasiApplication.class)
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
