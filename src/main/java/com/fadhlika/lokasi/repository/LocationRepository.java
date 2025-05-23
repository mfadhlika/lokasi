/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.fadhlika.lokasi.repository;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.fadhlika.lokasi.model.Location;
import org.locationtech.jts.geom.GeometryCollection;
import org.locationtech.jts.geom.GeometryFactory;
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

    private final RowMapper<Location> pointRowMapper = new RowMapper<Location>() {
        @Override
        public Location mapRow(ResultSet rs, int rowNum) throws SQLException {
            Location location = new Location();
            location.setSpeed(rs.getDouble("speed"));
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

    public void createPoint(Location location) {
        try {
            jdbcTemplate.update("INSERT INTO location(user_id, geometry, timestamp, created_at) VALUES(?, ?, ?, ?)", location.getUserId(),
                    location.getGeometry(),
                    location.getTimestamp(),
                    location.getCreatedAt());
        } catch (DataAccessException e) {
            logger.error("error creating point: {}", e.getMessage(), e);
        }
    }

    public void createPoints(List<Location> locations) {
        try {
            jdbcTemplate.batchUpdate("INSERT INTO location(user_id, geometry, timestamp, created_at) VALUES(?, ?, ?, ?)", new BatchPreparedStatementSetter() {
                @Override
                public void setValues(PreparedStatement ps, int i) throws SQLException {
                    Location l = locations.get(i);
                    ps.setInt(1, l.getUserId());
                    ps.setObject(2, l.getGeometry());
                    ps.setObject(3, l.getTimestamp());
                    ps.setObject(4, l.getCreatedAt());
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

    public List<Location> find(int userId, LocalDateTime start, LocalDateTime end) throws SQLException {
        return jdbcTemplate.query("SELECT id, geometry, speed, timestamp, created_at FROM location", pointRowMapper);
    }

    public GeometryCollection listPoints(int userId, LocalDateTime start, LocalDateTime end) throws SQLException {
        return jdbcTemplate.query("SELECT AsText(CastToGeometryCollection(ST_Collect(ST_GeomFromText(geometry)))) AS points FROM location WHERE user_id = ? AND timestamp BETWEEN ? AND ?", (rs) -> {
            try {
                String pointWkt = rs.getString("points");
                if (pointWkt == null) {
                    return new GeometryFactory().createGeometryCollection();
                }

                return (GeometryCollection) wktReader.read(rs.getString(1));
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        }, userId, start, end);
    }
}
