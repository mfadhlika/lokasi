/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.fadhlika.kelana.repository;

import java.io.InputStream;
import java.sql.*;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import com.fadhlika.kelana.dto.FeatureCollection;
import com.fadhlika.kelana.model.Location;

import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKBReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.jdbc.core.simple.JdbcClient.MappedQuerySpec;
import org.springframework.jdbc.core.simple.JdbcClient.StatementSpec;
import org.springframework.stereotype.Repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import tools.jackson.databind.ObjectMapper;

/**
 * @author fadhl
 */
@Repository
public class LocationRepository {

    @SuppressWarnings("unused")
    private static final Logger logger = LoggerFactory.getLogger(LocationRepository.class);

    private final JdbcClient jdbcClient;

    private final WKBReader wkbReader = new WKBReader();

    private final ObjectMapper mapper;

    private final RowMapper<Location> locationRowMapper = new RowMapper<Location>() {
        @Override
        public Location mapRow(ResultSet rs, int rowNum) throws SQLException {
            Location location;
            byte[] point = rs.getBytes("geometry");
            try {
                location = new Location(wkbReader.read(point));
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
            location.setId(rs.getInt("id"));
            location.setDeviceId(rs.getString("device_id"));
            location.setAltitude(rs.getInt("altitude"));
            location.setCourse(rs.getInt("course"));
            location.setSpeed(rs.getDouble("speed"));
            location.setAccuracy(rs.getInt("accuracy"));
            location.setVerticalAccuracy(rs.getInt("vertical_accuracy"));
            location.setBatteryState(rs.getInt("battery_state"));
            location.setBattery(rs.getDouble("battery"));
            location.setPressure(rs.getDouble("pressure"));
            location.setSsid(rs.getString("ssid"));
            location.setTimestamp(rs.getObject("timestamp", OffsetDateTime.class).toZonedDateTime());
            location.setCourseAccuracy(rs.getInt("course_accuracy"));
            location.setCreatedAt(rs.getObject("created_at", OffsetDateTime.class).toZonedDateTime());
            location.setRawData(rs.getString("raw_data"));

            Array motions = rs.getArray("motions");
            if (motions != null)
                location.setMotions(Arrays.stream((String[]) motions.getArray()).map(e -> e).toList());

            InputStream geocode = rs.getAsciiStream("geocode");
            if (geocode != null) {
                try {
                    location.setGeocode(mapper.readValue(geocode, FeatureCollection.class));
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
            return location;
        }
    };

    LocationRepository(JdbcClient jdbcClient, ObjectMapper mapper) {
        this.jdbcClient = jdbcClient;
        this.mapper = mapper;
    }

    public void createLocation(Location location) throws DataAccessException, JsonProcessingException {
        StringBuilder sqlBuilder = new StringBuilder("INSERT INTO location(");
        if (location.getId() != 0) {
            sqlBuilder.append("id, ");
        }
        sqlBuilder.append("""
                user_id,
                device_id,
                geometry,
                altitude,
                course,
                course_accuracy,
                speed,
                accuracy,
                vertical_accuracy,
                motions,
                battery_state,
                battery,
                pressure,
                ssid,
                timestamp,
                raw_data,
                created_at,
                import_id,
                geocode) VALUES(""");
        if (location.getId() != 0) {
            sqlBuilder.append(":id, ");
        }
        sqlBuilder
                .append("""
                        :user_id, :device_id, ST_GeomFromText(:geometry), :altitude, :course, :course_accuracy,
                        :speed, :accuracy, :vertical_accuracy, :motions::text[], :battery_state, :battery, :pressure,
                        :ssid, :timestamp, :raw_data::json, :created_at, :import_id, :geocode::json)
                        ON CONFLICT (user_id, device_id, geometry, timestamp, import_id) DO UPDATE SET
                                    altitude = excluded.altitude,
                                    course = excluded.course,
                                    course_accuracy = excluded.course_accuracy,
                                    speed = excluded.speed,
                                    accuracy = excluded.accuracy,
                                    vertical_accuracy = excluded.vertical_accuracy,
                                    motions = excluded.motions,
                                    battery_state = excluded.battery_state,
                                    battery = excluded.battery,
                                    pressure = excluded.pressure,
                                    ssid = excluded.ssid,
                                    raw_data = excluded.raw_data,
                                    created_at = excluded.created_at,
                                    import_id = excluded.import_id,
                                    geocode = excluded.geocode
                                """);

        StatementSpec stmt = jdbcClient.sql(sqlBuilder.toString());
        if (location.getId() > 0) {
            stmt = stmt.param("id", location.getId());
        }
        stmt = stmt
                .param("user_id", location.getUserId())
                .param("device_id", location.getDeviceId())
                .param("geometry", location.getGeometry().toText())
                .param("altitude", location.getAltitude())
                .param("course", location.getCourse())
                .param("course_accuracy", location.getCourseAccuracy())
                .param("speed", location.getSpeed())
                .param("accuracy", location.getAccuracy())
                .param("vertical_accuracy", location.getVerticalAccuracy());

        String motionParam = null;
        if (location.getMotions() != null) {
            motionParam = "{" + String.join(",", location.getMotions()) + "}";
        }

        stmt.param("motions", motionParam)
                .param("battery_state", location.getBatteryState().value)
                .param("battery", location.getBattery())
                .param("pressure", location.getPressure())
                .param("ssid", location.getSsid())
                .param("timestamp", location.getTimestamp().toOffsetDateTime())
                .param("raw_data", location.getRawData())
                .param("created_at", location.getCreatedAt().toOffsetDateTime())
                .param("import_id", location.getImportId())
                .param("geocode", mapper.writeValueAsString(location.getGeocode()))
                .update();
    }

    public List<Location> findLocations(
            Optional<Integer> userId,
            Optional<ZonedDateTime> start,
            Optional<ZonedDateTime> end,
            Optional<String> device,
            Optional<String> order,
            Optional<Boolean> desc,
            Optional<Integer> offset,
            Optional<Integer> limit,
            Optional<Geometry> bounds) {
        return findLocationsStatementSpecBuilder(userId,
                start,
                end,
                device,
                order,
                desc,
                offset,
                limit,
                Optional.empty(),
                Optional.empty(),
                bounds).list();
    }

    public Optional<Location> findLocation(
            Optional<Integer> userId,
            Optional<ZonedDateTime> start,
            Optional<ZonedDateTime> end,
            Optional<String> device,
            Optional<String> order,
            Optional<Boolean> desc,
            Optional<Boolean> geocoded) {
        return findLocationsStatementSpecBuilder(
                userId,
                start,
                end,
                device,
                order,
                desc,
                Optional.empty(),
                Optional.of(1),
                Optional.empty(),
                geocoded,
                Optional.empty()).optional();
    }

    public Location findLocation(int id) {
        return findLocationsStatementSpecBuilder(
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.of(1),
                Optional.of(id),
                Optional.empty(),
                Optional.empty()).single();
    }

    private MappedQuerySpec<Location> findLocationsStatementSpecBuilder(
            Optional<Integer> userId,
            Optional<ZonedDateTime> start,
            Optional<ZonedDateTime> end,
            Optional<String> device,
            Optional<String> order,
            Optional<Boolean> desc,
            Optional<Integer> offset,
            Optional<Integer> limit,
            Optional<Integer> id,
            Optional<Boolean> geocoded,
            Optional<Geometry> bounds) {
        List<String> where = new ArrayList<>();

        HashMap<String, Object> args = new HashMap<String, Object>();

        id.ifPresent((v) -> {
            where.add("id = :id");
            args.put("id", v);
        });

        userId.ifPresent((v) -> {
            where.add("user_id = :user_id");
            args.put("user_id", v);
        });

        if (start.isPresent() && end.isPresent()) {
            where.add("timestamp BETWEEN :start AND :end");
            args.put("start", start.get().toOffsetDateTime());
            args.put("end", end.get().toOffsetDateTime());
        } else if (start.isPresent()) {
            where.add("timestamp < :timestamp");
            args.put("timestamp", start.get());
        }

        device.ifPresent((d) -> {
            where.add("device_id = :device_id");
            args.put("device_id", d);
        });

        geocoded.ifPresent((v) -> {
            if (v)
                where.add("geocode IS NOT NULL OR geocode != 'null'");
            else
                where.add("geocode IS NULL OR geocode = 'null'");
        });

        bounds.ifPresent((b) -> {
            where.add("ST_CoveredBy(geometry, ST_GeomFromText(:bounds))");
            args.put("bounds", b.toText());
        });

        StringBuilder sqlBuilder = new StringBuilder("""
                SELECT
                    id,
                    user_id,
                    device_id,
                    ST_AsBinary(geometry) AS geometry,
                    altitude,
                    course,
                    speed,
                    accuracy,
                    vertical_accuracy,
                    motions,
                    battery_state,
                    battery,
                    pressure,
                    ssid,
                    raw_data::json AS raw_data,
                    timestamp,
                    created_at,
                    import_id,
                    course_accuracy,
                    geocode::json AS geocode FROM location""");
        if (!where.isEmpty()) {
            sqlBuilder.append(" WHERE ");
            sqlBuilder.append(String.join(" AND ", where));
        }

        sqlBuilder.append(" ORDER BY ");
        sqlBuilder.append(order.orElse("timestamp"));

        desc.ifPresent((v) -> {
            if (v)
                sqlBuilder.append(" DESC");
        });

        limit.ifPresent((l) -> {
            sqlBuilder.append(" LIMIT :limit");
            args.put("limit", l);
        });

        offset.ifPresent((o) -> {
            sqlBuilder.append(" OFFSET :offset");
            args.put("offset", o);
        });

        return jdbcClient.sql(sqlBuilder.toString()).params(args).query(locationRowMapper);
    }

    public void updateLocationGeocode(int id, FeatureCollection geocode) throws JsonProcessingException {
        jdbcClient.sql("UPDATE location SET geocode = ?::jsonb WHERE id = ?")
                .param(mapper.writeValueAsString(geocode)).param(id).update();
    }
}
