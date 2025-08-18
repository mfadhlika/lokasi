package com.fadhlika.lokasi.jobs;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.jobrunr.jobs.annotations.Job;
import org.jobrunr.jobs.annotations.Recurring;
import org.jobrunr.jobs.lambdas.JobRequestHandler;
import org.locationtech.jts.geom.Coordinate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.fadhlika.lokasi.dto.FeatureCollection;
import com.fadhlika.lokasi.dto.jobs.ReverseGeocodeJobRequest;
import com.fadhlika.lokasi.model.Location;
import com.fadhlika.lokasi.repository.LocationRepository;
import com.fadhlika.lokasi.repository.PhotonRepository;

@Component
public class ReverseGeocodeJob implements JobRequestHandler<ReverseGeocodeJobRequest> {
    private static final Logger logger = LoggerFactory.getLogger(ReverseGeocodeJob.class);

    @Value("${reverse_geocode.batch_size}")
    private int batchSize;

    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    private PhotonRepository photonRepository;

    @Recurring(id = ReverseGeocodeJobRequest.id, cron = "0 0 * * *")
    @Job(name = "Reverse geocode job", retries = 0)
    public void execute() throws Exception {
        try {
            logger.debug("start running reverse geocode job");
            Instant start = Instant.now();

            List<Location> locations;
            do {
                locations = locationRepository
                        .findLocations(Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
                                Optional.empty(), Optional.empty(), Optional.empty(), Optional.of(batchSize))
                        .toList();

                for (Location l : locations) {
                    Coordinate coord = l.getGeometry().getCoordinate();
                    FeatureCollection geocode = photonRepository.reverseGeocode(coord.y, coord.x);

                    locationRepository.updateLocationGeocode(l.getId(), geocode);
                    Thread.sleep(1000);
                }
            } while (!locations.isEmpty());

            Duration duration = start.until(Instant.now());
            logger.info("reverse geocoded {} locations in {}s", locations.size(), duration.getSeconds());
        } catch (Exception ex) {
            ex.printStackTrace();
            throw ex;
        }
    }

    @Override
    public void run(ReverseGeocodeJobRequest arg0) throws Exception {
        execute();
    }
}
