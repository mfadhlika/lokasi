package com.fadhlika.kelana.dto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;

import com.fadhlika.kelana.exception.InternalErrorException;
import com.fadhlika.kelana.util.GeometryDeserializer;
import com.fadhlika.kelana.util.GeometrySerializer;

import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

public class Feature {

    private final String type = "Feature";

    @JsonSerialize(using = GeometrySerializer.class)
    @JsonDeserialize(using = GeometryDeserializer.class)
    private Geometry geometry;

    private List<Double> bbox = new ArrayList<>();

    private HashMap<String, Object> properties;

    private final ObjectMapper mapper = new ObjectMapper();

    public Feature() {

    }

    public Feature(Geometry geometry, HashMap<String, Object> properties) {
        this.geometry = geometry;
        for (Coordinate coordinate : geometry.getBoundary().getCoordinates()) {
            this.bbox.add(coordinate.x);
            this.bbox.add(coordinate.y);
        }
        this.properties = properties;
    }

    public <T> Feature(Geometry geometry, T properties) {
        this.geometry = geometry;
        for (Coordinate coordinate : geometry.getBoundary().getCoordinates()) {
            this.bbox.add(coordinate.x);
            this.bbox.add(coordinate.y);
        }
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
