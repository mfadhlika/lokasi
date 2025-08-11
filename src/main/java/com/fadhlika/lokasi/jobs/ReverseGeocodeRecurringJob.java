package com.fadhlika.lokasi.jobs;

import java.util.Optional;

import org.jobrunr.jobs.annotations.Job;
import org.jobrunr.jobs.annotations.Recurring;
import org.locationtech.jts.geom.Coordinate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fadhlika.lokasi.dto.FeatureCollection;
import com.fadhlika.lokasi.model.Location;
import com.fadhlika.lokasi.repository.LocationRepository;
import com.fadhlika.lokasi.repository.PhotonRepository;

@Component
public class ReverseGeocodeRecurringJob {
    private static final Logger logger = LoggerFactory.getLogger(ReverseGeocodeRecurringJob.class);

    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    private PhotonRepository photonRepository;

    @Recurring(id = "reverse-geocode-job", interval = "PT1M")
    @Job(name = "Reverse geocode job")
    public void execute() throws Exception {
        try {
            logger.debug("start running reverse geocode job");
            Optional<Location> location = locationRepository.findLocation(
                    Optional.empty(),
                    Optional.empty(),
                    Optional.empty(),
                    Optional.empty(),
                    Optional.of(false));

            if (location.isPresent()) {
                Location l = location.get();
                Coordinate coord = l.getGeometry().getCoordinate();
                FeatureCollection geocode = photonRepository.reverseGeocode(coord.y, coord.x);

                locationRepository.updateLocationGeocode(l.getId(), geocode);
                logger.info("reverse geocode location {}", l.getId());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            throw ex;
        }
    }
}
