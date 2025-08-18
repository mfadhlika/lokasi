package com.fadhlika.lokasi.service;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.locationtech.jts.geom.Coordinate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.fadhlika.lokasi.dto.FeatureCollection;
import com.fadhlika.lokasi.model.Location;
import com.fadhlika.lokasi.repository.LocationRepository;
import com.fadhlika.lokasi.repository.PhotonRepository;

@Service
public class ReverseGeocodeService {
    private static final Logger logger = LoggerFactory.getLogger(ReverseGeocodeService.class);

    @Value("${reverse_geocode.batch_size}")
    private int batchSize;

    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    private PhotonRepository photonRepository;

    private Lock lock = new ReentrantLock();

    @Scheduled(cron = "0 0 0 * * *")
    public void scheduledReverseGeocode() {
        if (lock.tryLock())
            processReverseGeocode();
    }

    public boolean tryLock() {
        return lock.tryLock();
    }

    @Async
    public void startReverseGeocode() {
        processReverseGeocode();
    }

    public void processReverseGeocode() {
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
        } finally {
            lock.unlock();
        }
    }
}
