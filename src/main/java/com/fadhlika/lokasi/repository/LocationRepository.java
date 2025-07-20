/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.fadhlika.lokasi.repository;

import java.sql.*;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

    @Autowired
    public LocationRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcClient = JdbcClient.create(jdbcTemplate);
        this.wktReader = new WKTReader();
    }

    private final RowMapper<Location> locationRowMapper = new RowMapper<Location>() {
        @Override
        public Location mapRow(ResultSet rs, int rowNum) throws SQLException {
            Location location = new Location();
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
                    ObjectMapper mapper = new ObjectMapper();
                    location.setMotions(mapper.readValue(motions, new TypeReference<List<String>>() {
                    }));
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            }
            return location;
        }
    };

    public void createLocation(Location location) throws DataAccessException, JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        jdbcClient.sql(
                "INSERT INTO location("
                        + "user_id, "
                        + "device_id, "
                        + "geometry, "
                        + "altitude, "
                        + "course, "
                        + "course_accuracy, "
                        + "speed, "
                        + "accuracy, "
                        + "vertical_accuracy, "
                        + "motions, "
                        + "battery_state, "
                        + "battery, "
                        + "ssid, "
                        + "timestamp, "
                        + "raw_data, "
                        + "created_at, "
                        + "import_id) VALUES(?, ?, ST_GeomFromText(?), ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)")
                .param(location.getUserId())
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
                .update();
    }

    public List<Location> findLocations(int userId, ZonedDateTime start, ZonedDateTime end, Optional<String> device,
            Optional<Integer> offset, Optional<Integer> limit) throws SQLException {
        List<String> where = new ArrayList<>() {
            {
                add("user_id = ?");
                add("timestamp BETWEEN ? AND ?");
            }
        };

        List<Object> args = new ArrayList<Object>() {
            {
                add(userId);
                add(start);
                add(end);
            }
        };

        device.ifPresent((d) -> {
            where.add("device_id = ?");
            args.add(d);
        });

        StringBuilder sqlBuilder = new StringBuilder("SELECT "
                + "id, "
                + "user_id, "
                + "device_id, "
                + "AsText(geometry) AS geometry, "
                + "altitude, "
                + "course, "
                + "speed, "
                + "accuracy, "
                + "vertical_accuracy, "
                + "motions, "
                + "battery_state, "
                + "battery, "
                + "ssid, "
                + "raw_data, "
                + "timestamp, "
                + "created_at, "
                + "import_id, "
                + "course_accuracy FROM location WHERE "
                + String.join(" AND ", where)
                + " ORDER BY timestamp DESC");

        limit.ifPresent((l) -> {
            sqlBuilder.append(" LIMIT ?");
            args.add(l);
        });

        offset.ifPresent((o) -> {
            sqlBuilder.append(" OFFSET ?");
            args.add(o);
        });

        StatementSpec stmt = jdbcClient.sql(sqlBuilder.toString());
        for (Object arg : args) {
            stmt = stmt.param(arg);
        }
        return stmt.query(locationRowMapper).list();
    }
}
