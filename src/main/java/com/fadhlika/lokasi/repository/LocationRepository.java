/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.fadhlika.lokasi.repository;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import com.fadhlika.lokasi.dto.FeatureCollection;
import com.fadhlika.lokasi.model.Location;

import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.jdbc.core.simple.JdbcClient.MappedQuerySpec;
import org.springframework.jdbc.core.simple.JdbcClient.StatementSpec;
import org.springframework.stereotype.Repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author fadhl
 */
@Repository
public class LocationRepository {

    private static final Logger logger = LoggerFactory.getLogger(LocationRepository.class);

    private final JdbcClient jdbcClient;

    private final WKTReader wktReader;

    private ObjectMapper mapper;

    @Autowired
    public LocationRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcClient = JdbcClient.create(jdbcTemplate);
        this.wktReader = new WKTReader();
        this.mapper = new ObjectMapper();
    }

    private final RowMapper<Location> locationRowMapper = new RowMapper<Location>() {
        @Override
        public Location mapRow(ResultSet rs, int rowNum) throws SQLException {
            Location location = new Location();
            location.setId(rs.getInt("id"));
            location.setDeviceId(rs.getString("device_id"));
            location.setAltitude(rs.getInt("altitude"));
            location.setCourse(rs.getInt("course"));
            location.setSpeed(rs.getDouble("speed"));
            location.setAccuracy(rs.getInt("accuracy"));
            location.setVerticalAccuracy(rs.getInt("vertical_accuracy"));
            location.setBatteryState(rs.getString("battery_state"));
            location.setBattery(rs.getDouble("battery"));
            location.setSsid(rs.getString("ssid"));
            location.setTimestamp(ZonedDateTime.parse(rs.getString("timestamp")));
            location.setCourseAccuracy(rs.getInt("course_accuracy"));
            location.setCreatedAt(ZonedDateTime.parse(rs.getString("created_at")));
            location.setRawData(rs.getString("raw_data"));
            String pointWkt = rs.getString("geometry");
            if (pointWkt != null) {
                try {
                    location.setGeometry(wktReader.read(pointWkt));
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
            }

            String motions = rs.getString("motions");
            if (motions != null) {
                try {
                    location.setMotions(mapper.readValue(motions, new TypeReference<List<String>>() {
                    }));
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            }
            InputStream geocode = rs.getAsciiStream("geocode");
            if (geocode != null) {
                try {
                    location.setGeocode(mapper.readValue(geocode, FeatureCollection.class));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            return location;
        }
    };

    public void createLocation(Location location) throws DataAccessException, JsonProcessingException {
        StringBuilder sqlBuilder = new StringBuilder("INSERT OR REPLACE INTO location(");
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
                ssid,
                timestamp,
                raw_data,
                created_at,
                import_id,
                geocode) VALUES(""");
        if (location.getId() != 0) {
            sqlBuilder.append("?, ");
        }
        sqlBuilder
                .append("?, ?, ST_GeomFromText(?), ?, ?, ?, ?, ?, ?, jsonb(?), ?, ?, ?, ?, jsonb(?), ?, ?, jsonb(?))");

        StatementSpec stmt = jdbcClient.sql(sqlBuilder.toString());
        if (location.getId() != 0) {
            stmt = stmt.param(location.getId());
        }
        stmt.param(location.getUserId())
                .param(location.getDeviceId())
                .param(location.getGeometry())
                .param(location.getAltitude())
                .param(location.getCourse())
                .param(location.getCourseAccuracy())
                .param(location.getSpeed())
                .param(location.getAccuracy())
                .param(location.getVerticalAccuracy())
                .param(mapper.writeValueAsString(location.getMotions()))
                .param(location.getBatteryState())
                .param(location.getBattery())
                .param(location.getSsid())
                .param(location.getTimestamp())
                .param(location.getRawData())
                .param(location.getCreatedAt())
                .param(location.getImportId())
                .param(mapper.writeValueAsString(location.getGeocode()))
                .update();
    }

    public Stream<Location> findLocations(
            Optional<Integer> userId,
            Optional<ZonedDateTime> start,
            Optional<ZonedDateTime> end,
            Optional<String> device,
            Optional<String> order,
            Optional<Boolean> desc,
            Optional<Integer> offset,
            Optional<Integer> limit) throws SQLException {
        return findLocationsStatementSpecBuilder(userId,
                start,
                end,
                device,
                order,
                desc,
                offset,
                limit,
                Optional.empty(),
                Optional.empty()).stream();
    }

    public Optional<Location> findLocation(
            Optional<Integer> userId,
            Optional<ZonedDateTime> start,
            Optional<ZonedDateTime> end,
            Optional<String> device,
            Optional<String> order,
            Optional<Boolean> desc,
            Optional<Boolean> geocoded) throws SQLException {
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
                geocoded).optional();
    }

    public Location findLocation(int id) throws SQLException {
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
                Optional.empty()).single();
    }

    public MappedQuerySpec<Location> findLocationsStatementSpecBuilder(
            Optional<Integer> userId,
            Optional<ZonedDateTime> start,
            Optional<ZonedDateTime> end,
            Optional<String> device,
            Optional<String> order,
            Optional<Boolean> desc,
            Optional<Integer> offset,
            Optional<Integer> limit,
            Optional<Integer> id,
            Optional<Boolean> geocoded) throws SQLException {
        List<String> where = new ArrayList<>();

        List<Object> args = new ArrayList<Object>();

        id.ifPresent((v) -> {
            where.add("id = ?");
            args.add(v);
        });

        userId.ifPresent((v) -> {
            where.add("user_id = ?");
            args.add(v);
        });

        if (start.isPresent() && end.isPresent()) {
            where.add("timestamp BETWEEN ? AND ?");
            args.add(start.get());
            args.add(end.get());
        } else if (start.isPresent()) {
            where.add("timestamp < ?");
            args.add(start.get());
        }

        device.ifPresent((d) -> {
            where.add("device_id = ?");
            args.add(d);
        });

        geocoded.ifPresent((v) -> {
            if (v)
                where.add("geocode != jsonb('null')");
            else
                where.add("geocode = jsonb('null')");
        });

        StringBuilder sqlBuilder = new StringBuilder("""
                SELECT
                    id,
                    user_id,
                    device_id,
                    AsText(geometry) AS geometry,
                    altitude,
                    course,
                    speed,
                    accuracy,
                    vertical_accuracy,
                    json(motions) AS motions,
                    battery_state,
                    battery,
                    ssid,
                    json(raw_data) AS raw_data,
                    timestamp,
                    created_at,
                    import_id,
                    course_accuracy,
                    json(geocode) AS geocode FROM location""");
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
            sqlBuilder.append(" LIMIT ?");
            args.add(l);
        });

        offset.ifPresent((o) -> {
            sqlBuilder.append(" OFFSET ?");
            args.add(o);
        });

        return jdbcClient.sql(sqlBuilder.toString()).params(args).query(locationRowMapper);
    }

    public void updateLocationGeocode(int id, FeatureCollection geocode) throws JsonProcessingException {
        jdbcClient.sql("UPDATE location SET geocode = jsonb(?) WHERE id = ?")
                .param(mapper.writeValueAsString(geocode)).param(id).update();
    }
}
