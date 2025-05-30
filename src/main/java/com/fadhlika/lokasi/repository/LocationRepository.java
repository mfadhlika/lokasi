/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.fadhlika.lokasi.repository;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
//            location.setMotions(rs.getString("motions"));
            location.setBatteryState(rs.getInt("battery_state"));
            location.setBattery(rs.getDouble("battery"));
            location.setSsid(rs.getString("ssid"));
            location.setTimestamp(LocalDateTime.parse(rs.getString("timestamp")));
            location.setCreatedAt(LocalDateTime.parse(rs.getString("created_at")));
            String pointWkt = rs.getString("geometry");
            if (pointWkt != null) {
                try {
                    location.setGeometry(wktReader.read(pointWkt));
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
            }
            return location;
        }
    };

    public void createLocation(Location location) {
        createLocations(new ArrayList<>() {{
            add(location);
        }});
    }

    public void createLocations(List<Location> locations) {
        try {
            jdbcTemplate.batchUpdate(
                    "INSERT INTO location(" +
                            "user_id, " +
                            "device_id, " +
                            "geometry, " +
                            "altitude, " +
                            "course, " +
                            "speed, " +
                            "accuracy, " +
                            "vertical_accuracy, " +
                            "motions, " +
                            "battery_state, " +
                            "battery, " +
                            "ssid, " +
                            "timestamp, " +
                            "raw_data, " +
                            "created_at) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                    new BatchPreparedStatementSetter() {
                        @Override
                        public void setValues(PreparedStatement ps, int i) throws SQLException {
                            Location location = locations.get(i);
                            ps.setInt(1, location.getUserId());
                            ps.setString(2, location.getDeviceId());
                            ps.setObject(3, location.getGeometry());
                            ps.setInt(4, location.getAltitude());
                            ps.setInt(5, location.getCourse());
                            ps.setDouble(6, location.getSpeed());
                            ps.setInt(7, location.getAccuracy());
                            ps.setInt(8, location.getVerticalAccuracy());
                            ps.setObject(9, location.getMotions());
                            ps.setObject(10, location.getBatteryState());
                            ps.setDouble(11, location.getBattery());
                            ps.setString(12, location.getSsid());
                            ps.setObject(13, location.getTimestamp());
                            ps.setString(14, location.getRawData());
                            ps.setObject(15, location.getCreatedAt());
                        }

                        @Override
                        public int getBatchSize() {
                            return locations.size();
                        }
                    });
        } catch (DataAccessException e) {
            logger.error("error creating point: {}", e.getMessage(), e);
        }
    }

    public List<Location> findLocations(int userId, LocalDateTime start, LocalDateTime end) throws SQLException {
        return jdbcTemplate.query("SELECT * FROM location WHERE user_id = ? AND timestamp BETWEEN ? AND ? ORDER BY timestamp DESC", locationRowMapper, userId, start, end);
    }
}
