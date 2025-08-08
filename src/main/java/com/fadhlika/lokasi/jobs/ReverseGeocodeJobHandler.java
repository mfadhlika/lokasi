package com.fadhlika.lokasi.jobs;

import org.jobrunr.jobs.lambdas.JobRequestHandler;
import org.locationtech.jts.geom.Coordinate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fadhlika.lokasi.dto.FeatureCollection;
import com.fadhlika.lokasi.dto.jobs.ReverseGeocodeJobRequest;
import com.fadhlika.lokasi.model.Location;
import com.fadhlika.lokasi.repository.PhotonRepository;
import com.fadhlika.lokasi.service.LocationService;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class ReverseGeocodeJobHandler implements JobRequestHandler<ReverseGeocodeJobRequest> {
    private static final Logger logger = LoggerFactory.getLogger(ReverseGeocodeJobHandler.class);

    @Autowired
    private LocationService locationService;

    @Autowired
    private PhotonRepository photonRepository;

    @Override
    public void run(ReverseGeocodeJobRequest args) throws Exception {
        Location location = locationService.findLocation(args.locationId());

        Coordinate coord = location.getGeometry().getCoordinate();
        FeatureCollection featureCollection = photonRepository.reverseGeocode(coord.y, coord.x);

        ObjectMapper mapper = new ObjectMapper();
        logger.info("reverse geocode ({}, {}): {}", coord.y, coord.x, mapper.writeValueAsString(featureCollection));

        location.setGeocode(featureCollection);

        locationService.saveLocation(location);
    }

}
