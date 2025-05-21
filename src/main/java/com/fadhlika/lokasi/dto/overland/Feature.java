package com.fadhlika.lokasi.dto.overland;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.geojson.GeoJsonReader;

import java.io.IOException;
import java.util.Map;

@JsonDeserialize(using = GeoJsonJacksonDeserializer.class)
public class Feature {
    private final String type = "Feature";

    private final Geometry geometry;

    private Properties properties;

    public Feature(Geometry geometry) {
        this.geometry = geometry;
    }

    public Feature(Geometry geometry, Properties properties) {
        this.geometry = geometry;
        this.properties = properties;
    }

    public Geometry getGeometry() {
        return geometry;
    }

    public Properties getProperties() {
        return properties;
    }
}

class GeoJsonJacksonDeserializer extends JsonDeserializer<Feature> {

    @Override
    public Feature deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JacksonException {
        GeoJsonReader reader = new GeoJsonReader();
        try {
            Geometry geometry = reader.read(p.getText());
            JsonNode node = p.getCodec().readTree(p);
            Properties properties = p.readValueAs(Properties.class);
            return new Feature(geometry, properties);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }
}
