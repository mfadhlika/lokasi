package com.fadhlika.kelana.util;

import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ValueSerializer;

import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.io.geojson.GeoJsonWriter;

public class GeometrySerializer extends ValueSerializer<Geometry> {

    @Override
    public void serialize(Geometry value, JsonGenerator gen, SerializationContext ctxt)
            throws JacksonException {
        GeoJsonWriter writer = new GeoJsonWriter();
        writer.setEncodeCRS(false);
        String str = writer.write(value);
        gen.writeRawValue(str);
    }
}
