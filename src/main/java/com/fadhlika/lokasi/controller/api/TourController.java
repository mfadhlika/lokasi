package com.fadhlika.lokasi.controller.api;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fadhlika.lokasi.dto.Feature;
import com.fadhlika.lokasi.dto.FeatureCollection;
import com.fadhlika.lokasi.dto.PointProperties;
import com.fadhlika.lokasi.dto.Response;
import com.fadhlika.lokasi.service.TourService;

@RestController
@RequestMapping("/api/v1/tours")
public class TourController {
    @Autowired
    private TourService tourService;

    @GetMapping("/{uuid}")
    public Response<FeatureCollection> getTourLocations(@PathVariable UUID uuid) {
        List<Feature> features = this.tourService.findToursLocations(uuid).stream().map(curr -> {
            PointProperties props = new PointProperties(
                    curr.getTimestamp(),
                    curr.getAltitude(),
                    curr.getSpeed(),
                    curr.getCourse(),
                    curr.getCourseAccuracy(),
                    curr.getAccuracy(),
                    curr.getVerticalAccuracy(),
                    curr.getMotions(),
                    curr.getBatteryState().toString(),
                    curr.getBattery(),
                    curr.getDeviceId(),
                    curr.getSsid(),
                    curr.getGeocode(),
                    curr.getRawData());
            return new Feature(curr.getGeometry(), props);
        }).toList();

        return new Response<>(new FeatureCollection(features));
    }
}
