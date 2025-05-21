/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.fadhlika.lokasi.service;

import org.locationtech.jts.geom.GeometryCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fadhlika.lokasi.model.Point;
import com.fadhlika.lokasi.repository.PointRepository;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

/**
 *
 * @author fadhl
 */
@Service
public class PointService {

    private static final Logger logger = LoggerFactory.getLogger(PointService.class);

    private final PointRepository pointRepository;

    @Autowired
    public PointService(PointRepository pointRepository) {
        this.pointRepository = pointRepository;
    }

    public void createPoint(Point point) {
        pointRepository.createPoint(point);
        logger.info("successfully created point: {}", point.getPoint());
    }

    public void createPoints(List<Point> points) {
        pointRepository.createPoints(points);
    }

    public List<Point> findPoints(int userId, LocalDateTime start, LocalDateTime end) throws SQLException {
        return pointRepository.find(userId, start, end);
    }

    public GeometryCollection findGeometries(int userId, LocalDateTime start, LocalDateTime end) throws SQLException {
        return pointRepository.listPoints(userId, start, end);
    }
}
