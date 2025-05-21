/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.fadhlika.lokasi.repository;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.locationtech.jts.geom.GeometryCollection;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.fadhlika.lokasi.model.Point;

/**
 * @author fadhl
 */
@Repository
public class PointRepository {

    private static final Logger logger = LoggerFactory.getLogger(PointRepository.class);

    private final JdbcTemplate jdbcTemplate;

    private final WKTReader wktReader;

    @Autowired
    public PointRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.wktReader = new WKTReader();
    }

    private final RowMapper<Point> pointRowMapper = new RowMapper<Point>() {
        @Override
        public Point mapRow(ResultSet rs, int rowNum) throws SQLException {
            Point point = new Point();
            point.setId(rs.getInt("id"));
            point.setSpeed(rs.getDouble("speed"));
            point.setTimestamp(LocalDateTime.parse(rs.getString("timestamp")));
            point.setCreatedAt(LocalDateTime.parse(rs.getString("created_at")));
            String pointWkt = rs.getString("point");
            if (pointWkt != null) {
                try {
                    point.setPoint((org.locationtech.jts.geom.Point) wktReader.read(pointWkt));
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
            }
            return point;
        }
    };

    public void createPoint(Point point) {
        try {
            jdbcTemplate.update("INSERT INTO point(user_id, point, timestamp, created_at) VALUES(?, ?, ?, ?)", point.getUserId(),
                    point.getPoint(),
                    point.getTimestamp(),
                    point.getCreatedAt());
        } catch (DataAccessException e) {
            logger.error("error creating point: {}", e.getMessage(), e);
        }
    }

    public void createPoints(List<Point> points) {
        try {
            StringBuilder sb = new StringBuilder("INSERT INTO point(user_id, point, timestamp, created_at) VALUES");
            String[] values = new String[points.size()];
            Arrays.fill(values, "(?, ?, ?, ?)");
            sb.append(String.join(",", values));
            String sql = sb.toString();

            List<Object> args = new ArrayList<>();
            points.forEach(point -> {
                args.add(point.getUserId());
                args.add(point.getPoint());
                args.add(point.getTimestamp());
                args.add(point.getCreatedAt());
            });
            jdbcTemplate.update(sql, args);
        } catch (DataAccessException e) {
            logger.error("error creating point: {}", e.getMessage(), e);
        }
    }

    public List<Point> find(int userId, LocalDateTime start, LocalDateTime end) throws SQLException {
        return jdbcTemplate.query("SELECT id, point, speed, timestamp, created_at FROM point", pointRowMapper);
    }

    public GeometryCollection listPoints(int userId, LocalDateTime start, LocalDateTime end) throws SQLException {
        return jdbcTemplate.query("SELECT AsText(CastToGeometryCollection(ST_Collect(ST_GeomFromText(point)))) AS points FROM point WHERE user_id = ? AND timestamp BETWEEN ? AND ?", (rs) -> {
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
