package com.fadhlika.kelana.controller.api;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.fadhlika.kelana.dto.Feature;
import com.fadhlika.kelana.dto.FeatureCollection;
import com.fadhlika.kelana.dto.RegionProperties;
import com.fadhlika.kelana.dto.Response;
import com.fadhlika.kelana.exception.BadRequestException;
import com.fadhlika.kelana.model.Region;
import com.fadhlika.kelana.model.User;
import com.fadhlika.kelana.service.RegionService;
import com.fasterxml.jackson.core.type.TypeReference;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/v1/regions")
public class RegionController {
    private final RegionService regionService;

    RegionController(RegionService regionService) {
        this.regionService = regionService;
    }

    @GetMapping
    public Response<FeatureCollection> getRegions() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<Feature> features = regionService.fetchRegions(user.getId()).stream().map(curr -> {
            RegionProperties props = new RegionProperties(
                    curr.getDesc(),
                    curr.getBeaconUUID(),
                    curr.getBeaconMajor(),
                    curr.getBeaconMinor(),
                    curr.getRid(),
                    curr.getGeocode(),
                    curr.getCreatedAt());

            return new Feature(curr.getGeometry(), props);
        }).toList();

        return new Response<>(new FeatureCollection(features));
    }

    @PostMapping
    @ResponseStatus(code = HttpStatus.CREATED)
    public Response<Void> postMethodName(@RequestBody FeatureCollection featureCollection) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (featureCollection.features().isEmpty()) {
            throw new BadRequestException("empty regions");
        }

        List<Region> regions = featureCollection.features().stream().map(curr -> {
            RegionProperties props = curr.convertProperties(new TypeReference<RegionProperties>() {

            });

            return new Region(0, user.getId(), props.desc(), curr.getGeometry(), props.beaconUUID(),
                    props.beaconMajor(),
                    props.beaconMinor(), props.rid(), props.geocode(), props.createdAt());
        }).toList();

        regionService.createRegions(regions);

        return new Response<>("Regions created");
    }

}
