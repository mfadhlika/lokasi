package com.fadhlika.lokasi.dto.owntracks;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;

import com.fasterxml.jackson.databind.ObjectMapper;

@JsonTest
public class LocationTest {
    @Autowired
    private ObjectMapper mapper;

    @Test
    public void givenBasicJsonMapToCorrectLocation() {
        String msgStr = """
                {
                  "_type": "location",
                  "tid": "my_device_id",
                  "tst": 1672531200,
                  "lat": -1.23456,
                  "lon": 12.34567
                }""";

        Message msgObj = assertDoesNotThrow(() -> mapper.readValue(msgStr, Message.class));

        assertInstanceOf(Location.class, msgObj);

        com.fadhlika.lokasi.model.Location location = assertDoesNotThrow(
                () -> ((Location) msgObj).toLocation(1, "test"));

        GeometryFactory gf = new GeometryFactory();
        Point expectedPoint = gf.createPoint(new Coordinate(12.34567, -1.23456));

        assertEquals(expectedPoint, location.getGeometry());
        assertEquals(ZonedDateTime.ofInstant(Instant.ofEpochSecond(1672531200), ZoneOffset.UTC),
                location.getTimestamp());
    }

    @Test
    public void givenBasicWithCommonFieldsJsonMapToCorrectLocation() {
        String msgStr = """
                {
                  "_type": "location",
                  "tid": "my_device_id",
                  "tst": 1672531200,
                  "lat": -1.23456,
                  "lon": 12.34567,
                  "acc": 10,
                  "alt": 50,
                  "batt": 95
                }""";

        Message msgObj = assertDoesNotThrow(() -> mapper.readValue(msgStr, Message.class));

        assertInstanceOf(Location.class, msgObj);

        com.fadhlika.lokasi.model.Location location = assertDoesNotThrow(
                () -> ((Location) msgObj).toLocation(1, "test"));

        GeometryFactory gf = new GeometryFactory();
        Point expectedPoint = gf.createPoint(new Coordinate(12.34567, -1.23456));

        assertEquals(expectedPoint, location.getGeometry());
        assertEquals(ZonedDateTime.ofInstant(Instant.ofEpochSecond(1672531200), ZoneOffset.UTC),
                location.getTimestamp());
        assertEquals(10, location.getAccuracy());
        assertEquals(50, location.getAltitude());
        assertEquals(95, location.getBattery());
    }

    @Test
    public void givenIntermediateJsonMapToCorrectLocation() {
        String msgStr = """
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
                }""";

        Message msgObj = assertDoesNotThrow(() -> mapper.readValue(msgStr, Message.class));

        assertInstanceOf(Location.class, msgObj);

        com.fadhlika.lokasi.model.Location location = assertDoesNotThrow(
                () -> ((Location) msgObj).toLocation(1, "test"));

        GeometryFactory gf = new GeometryFactory();
        Point expectedPoint = gf.createPoint(new Coordinate(12.34567, -1.23456));

        assertEquals(expectedPoint, location.getGeometry());
        assertEquals(ZonedDateTime.ofInstant(Instant.ofEpochSecond(1672531200), ZoneOffset.UTC),
                location.getTimestamp());
        assertEquals(10, location.getAccuracy());
        assertEquals(50, location.getAltitude());
        assertEquals(95, location.getBattery());
        assertEquals(15, location.getSpeed());
        assertEquals(270, location.getCourse());
    }
}
