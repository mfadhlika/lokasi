package com.fadhlika.lokasi.controller.api.overland;

import com.fadhlika.lokasi.controller.api.owntracks.OwntracksController;
import com.fadhlika.lokasi.dto.overland.Input;
import com.fadhlika.lokasi.dto.overland.Response;
import com.fadhlika.lokasi.model.Point;
import com.fadhlika.lokasi.model.User;
import com.fadhlika.lokasi.service.PointService;
import org.locationtech.jts.geom.Coordinate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/overland")
public class OverlandController {
    private final Logger logger = LoggerFactory.getLogger(OverlandController.class);

    private final PointService pointService;

    @Autowired
    public OverlandController(PointService pointService) {
        this.pointService = pointService;
    }

    @PostMapping
    public Response input(Input input) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        List<Point> points = input.locations.stream().map(location -> {
            Coordinate coordinate = location.getGeometry().getCoordinate();
            return new Point(user.getId(), coordinate.getX(), coordinate.getY(), location.getProperties().getTimestamp());
        }).toList();

        pointService.createPoints(points);

        return new Response("ok");
    }
}
