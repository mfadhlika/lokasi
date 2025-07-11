/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.fadhlika.lokasi.repository;

import java.sql.*;
import java.time.LocalDateTime;
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
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
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

    private final JdbcTemplate jdbcTemplate;

    private final WKTReader wktReader;

    @Autowired
    public LocationRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
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
            location.setBatteryState(rs.getInt("battery_state"));
            location.setBattery(rs.getDouble("battery"));
            location.setSsid(rs.getString("ssid"));
            location.setTimestamp(LocalDateTime.parse(rs.getString("timestamp")));
            location.setCourseAccuracy(rs.getInt("course_accuracy"));
            location.setCreatedAt(LocalDateTime.parse(rs.getString("created_at")));
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

    public void createLocation(Location location) throws DataAccessException {
        createLocations(new ArrayList<>() {
            {
                add(location);
            }
        });
    }

    public void createLocations(List<Location> locations) throws DataAccessException {
        jdbcTemplate.batchUpdate(
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
                + "import_id) VALUES(?, ?, ST_GeomFromText(?), ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ObjectMapper mapper = new ObjectMapper();

                Location location = locations.get(i);
                ps.setInt(1, location.getUserId());
                ps.setString(2, location.getDeviceId());
                ps.setObject(3, location.getGeometry());
                ps.setInt(4, location.getAltitude());
                ps.setInt(5, location.getCourse());
                ps.setInt(6, location.getCourseAccuracy());
                ps.setDouble(7, location.getSpeed());
                ps.setInt(8, location.getAccuracy());
                ps.setInt(9, location.getVerticalAccuracy());
                try {
                    ps.setObject(10, mapper.writeValueAsString(location.getMotions()));
                } catch (JsonProcessingException ex) {
                    throw new RuntimeException(ex.getMessage());
                }
                ps.setObject(11, location.getBatteryState());
                ps.setDouble(12, location.getBattery());
                ps.setString(13, location.getSsid());
                ps.setObject(14, location.getTimestamp());
                ps.setString(15, location.getRawData());
                ps.setObject(16, location.getCreatedAt());
                ps.setObject(17, location.getImportId());
            }

            @Override
            public int getBatchSize() {
                return locations.size();
            }
        });
    }

    public List<Location> findLocations(int userId, LocalDateTime start, LocalDateTime end, Optional<String> device) throws SQLException {
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

        return jdbcTemplate.query("SELECT "
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
                + String.join(" AND ", where), locationRowMapper, args.toArray());
    }
}
