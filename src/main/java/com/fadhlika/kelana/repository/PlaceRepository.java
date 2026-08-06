package com.fadhlika.kelana.repository;

import java.io.IOException;
import java.io.InputStream;
import java.sql.ResultSet;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKBReader;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import com.fadhlika.kelana.dto.FeatureCollection;
import com.fadhlika.kelana.model.Place;
import com.fasterxml.jackson.core.JsonProcessingException;
import tools.jackson.databind.ObjectMapper;

@Repository
public class PlaceRepository {
    public final JdbcClient jdbcClient;

    private final ObjectMapper mapper;

    private final WKBReader wkbReader = new WKBReader();

    private RowMapper<Place> rowMapper = (ResultSet rs, int rowNum) -> {
        byte[] geomBytes = rs.getBytes("geometry");
        Geometry geom = null;
        if (geomBytes != null) {
            try {
                geom = wkbReader.read(geomBytes);
            } catch (ParseException e) {
                throw new RuntimeException(e.getMessage());
            }
        }

        FeatureCollection geodata = null;
        InputStream geodataIS = rs.getAsciiStream("geodata");
        if (geodataIS != null) {
            try {
                geodata = ((PlaceRepository) this).mapper.readValue(geodataIS, FeatureCollection.class);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        return new Place(
                rs.getInt("id"),
                rs.getString("provider"),
                geom,
                rs.getString("type"),
                rs.getString("postcode"),
                rs.getString("country_code"),
                rs.getString("name"),
                rs.getString("country"),
                rs.getString("city"),
                rs.getString("district"),
                rs.getString("locality"),
                rs.getString("street"),
                rs.getString("state"),
                geodata,
                rs.getObject("created_at", OffsetDateTime.class).toZonedDateTime());
    };

    PlaceRepository(JdbcClient jdbcClient, ObjectMapper mapper) {
        this.jdbcClient = jdbcClient;
        this.mapper = mapper;
    }

    public void createPlace(Place place) throws JsonProcessingException {
        jdbcClient.sql("""
                INSERT INTO place(
                    provider,
                    geometry,
                    type,
                    postcode,
                    country_code,
                    name,
                    country,
                    city,
                    district,
                    locality,
                    street,
                    state,
                    geodata,
                    created_at
                ) VALUES (?, ST_GeomFromText(?), ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?::json, ?)
                ON CONFLICT (type, postcode, country_code, name, country, city, district, locality, street, state)
                DO NOTHING""")
                .param(place.provider())
                .param(place.geometry().toText())
                .param(place.type())
                .param(place.postcode())
                .param(place.countryCode())
                .param(place.name())
                .param(place.country())
                .param(place.city())
                .param(place.district())
                .param(place.locality())
                .param(place.street())
                .param(place.state())
                .param(mapper.writeValueAsString(place.geodata()))
                .param(place.createdAt().toOffsetDateTime())
                .update();
    }

    public Optional<Place> fetchPlace(Geometry geometry) {
        return jdbcClient.sql("""
                SELECT
                    id,
                    provider,
                    type,
                    postcode,
                    country_code,
                    name,
                    country,
                    city,
                    district,
                    locality,
                    street,
                    state,
                    ST_AsBinary(geometry) AS geometry,
                    geodata,
                    created_at
                FROM place WHERE ST_AsText(geometry) =  ?""")
                .param(geometry.toText())
                .query(rowMapper)
                .optional();
    }

    public List<Place> fetchPlaces(
            Optional<String> city,
            Optional<String> country,
            Optional<Integer> limit,
            Optional<Integer> offset) {

        List<String> where = new ArrayList<>();
        List<Object> args = new ArrayList<>();

        city.ifPresent(v -> {
            where.add("city = ?");
            args.add(v);
        });

        country.ifPresent(v -> {
            where.add("country = ?");
            args.add(v);
        });

        StringBuilder sb = new StringBuilder("""
                SELECT
                    id,
                    provider,
                    type,
                    postcode,
                    country_code,
                    name,
                    country,
                    city,
                    district,
                    locality,
                    street,
                    state,
                    ST_AsBinary(geometry) AS geometry,
                    geodata,
                    created_at
                FROM place""");

        if (!where.isEmpty()) {
            sb.append(" WHERE ");
            sb.append(String.join(" AND ", where));
        }

        limit.ifPresent(v -> {
            sb.append(" LIMIT ?");
            args.add(v);
        });

        offset.ifPresent(v -> {
            sb.append(" OFFSET ?");
            args.add(v);
        });

        String sql = sb.toString();
        return jdbcClient.sql(sql)
                .params(args)
                .query(rowMapper)
                .list();
    }
}
