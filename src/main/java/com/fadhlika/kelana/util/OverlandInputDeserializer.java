package com.fadhlika.kelana.util;

import com.fadhlika.kelana.dto.Feature;
import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.geojson.GeoJsonReader;

import java.io.IOException;
import java.util.HashMap;

public class OverlandInputDeserializer extends JsonDeserializer<Feature> {

    @Override
    public Feature deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JacksonException {
        GeoJsonReader reader = new GeoJsonReader();
        try {
            Geometry geometry = reader.read(p.getText());
            @SuppressWarnings("unchecked")
            HashMap<String, Object> properties = p.readValueAs(HashMap.class);
            return new Feature(geometry, properties);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }
}
