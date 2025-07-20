package com.fadhlika.lokasi.dto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;

import com.fadhlika.lokasi.exception.InternalErrorException;
import com.fadhlika.lokasi.util.GeometryDeserializer;
import com.fadhlika.lokasi.util.GeometrySerializer;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public class Feature {

    private final String type = "Feature";

    @JsonSerialize(using = GeometrySerializer.class)
    @JsonDeserialize(using = GeometryDeserializer.class)
    private Geometry geometry;

    private List<Double> bbox = new ArrayList<>();

    private HashMap<String, Object> properties;

    public Feature() {

    }

    public Feature(Geometry geometry, HashMap<String, Object> properties) {
        this.geometry = geometry;
        for (Coordinate coordinate : geometry.getBoundary().getCoordinates()) {
            this.bbox.add(coordinate.y);
            this.bbox.add(coordinate.x);
        }
        this.properties = properties;
    }

    public <T> Feature(Geometry geometry, T properties) {
        this.geometry = geometry;
        for (Coordinate coordinate : geometry.getBoundary().getCoordinates()) {
            this.bbox.add(coordinate.y);
            this.bbox.add(coordinate.x);
        }
        ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
        this.properties = mapper.convertValue(properties, new TypeReference<>() {
        });
    }

    public Geometry getGeometry() {
        return geometry;
    }

    public HashMap<String, Object> getProperties() {
        return properties;
    }

    public <T> T convertProperties(TypeReference<T> typeRef) {
        ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
        try {
            return mapper.convertValue(properties, typeRef);
        } catch (IllegalArgumentException ex) {
            throw new InternalErrorException(ex.getMessage());
        }
    }

    public String getType() {
        return type;
    }

    public List<Double> getBbox() {
        return bbox;
    }
}
