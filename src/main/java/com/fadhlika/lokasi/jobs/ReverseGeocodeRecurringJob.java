package com.fadhlika.lokasi.jobs;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.jobrunr.jobs.annotations.Job;
import org.jobrunr.jobs.annotations.Recurring;
import org.locationtech.jts.geom.Coordinate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

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

    @Recurring(id = "reverse-geocode-job", cron = "0 0 * * *")
    @Job(name = "Reverse geocode job", retries = 0)
    @Transactional
    public void execute() throws Exception {
        try {
            logger.debug("start running reverse geocode job");
            Instant start = Instant.now();

            List<Location> locations = locationRepository
                    .findLocations(Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
                            Optional.empty(), Optional.empty(), Optional.empty(), Optional.of(25))
                    .toList();

            for (Location l : locations) {
                Coordinate coord = l.getGeometry().getCoordinate();
                FeatureCollection geocode = photonRepository.reverseGeocode(coord.y, coord.x);

                locationRepository.updateLocationGeocode(l.getId(), geocode);
                Thread.sleep(1000);
            }

            Duration duration = start.until(Instant.now());
            logger.info("reverse geocoded {} locations in {}s", locations.size(), duration.getSeconds());
        } catch (Exception ex) {
            ex.printStackTrace();
            throw ex;
        }
    }
}
