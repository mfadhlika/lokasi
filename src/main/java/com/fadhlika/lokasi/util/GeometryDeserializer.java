package com.fadhlika.lokasi.util;

import com.fadhlika.lokasi.config.SecurityConfig;
import com.fadhlika.lokasi.dto.Feature;
import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.geojson.GeoJsonReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class GeometryDeserializer extends JsonDeserializer<Geometry> {
        @Override
        public Geometry deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JacksonException {
            GeoJsonReader reader = new GeoJsonReader();
            try {
                JsonNode node = p.getCodec().readTree(p);
                return reader.read(node.toString());
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        }
    }