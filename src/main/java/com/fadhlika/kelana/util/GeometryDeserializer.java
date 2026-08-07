package com.fadhlika.kelana.util;

import tools.jackson.core.JsonParser;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ValueDeserializer;

import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.geojson.GeoJsonReader;

public class GeometryDeserializer extends ValueDeserializer<Geometry> {
    @Override
    public Geometry deserialize(JsonParser p, tools.jackson.databind.DeserializationContext ctxt)
            throws tools.jackson.core.JacksonException {
        GeoJsonReader reader = new GeoJsonReader();
        try {
            JsonNode node = ctxt.readTree(p);
            return reader.read(node.toString());
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }
}
