package com.fadhlika.lokasi.dto;


import com.fadhlika.lokasi.util.GeometryDeserializer;
import com.fadhlika.lokasi.util.GeometrySerializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.locationtech.jts.geom.Geometry;

public class Feature<T> {

    @JsonSerialize(using = GeometrySerializer.class)
    @JsonDeserialize(using = GeometryDeserializer.class)
    private Geometry geometry;

    private T properties;

    public Feature() {

    }

    public Feature(Geometry geometry, T properties) {
        this.geometry = geometry;
        this.properties = properties;
    }

    public Geometry getGeometry() {
        return geometry;
    }

    public T getProperties() {
        return properties;
    }

    public String getType() {
        return "Feature";
    }
}