package com.fadhlika.lokasi.service;

import java.time.Duration;
import java.time.Instant;
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

    @Async
    public void startReverseGeocode() {
        processReverseGeocode();
    }

    @Scheduled(cron = "0 0 0 * * *")
    public void processReverseGeocode() {
        try {
            lock.lock();

            logger.debug("start running reverse geocode job");
            Instant start = Instant.now();

            int i = 0;
            while (true) {
                Optional<Location> location = locationRepository
                        .findLocation(Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
                                Optional.empty(), Optional.empty(), Optional.of(true));

                if (location.isEmpty())
                    break;

                Location l = location.get();
                Coordinate coord = l.getGeometry().getCoordinate();
                try {
                    FeatureCollection geocode = photonRepository.reverseGeocode(coord.y, coord.x, 3);

                    locationRepository.updateLocationGeocode(l.getId(), geocode);
                } catch (Exception ex) {
                    logger.error("error: {}, skipping...", ex.getMessage());
                }

                i++;

                Thread.sleep(1000);
            }

            Duration duration = start.until(Instant.now());
            logger.info("reverse geocoded {} locations in {}s", i, duration.getSeconds());
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            logger.info("unlocking...");
            lock.unlock();
        }
    }
}
