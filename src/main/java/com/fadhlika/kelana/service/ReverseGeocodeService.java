package com.fadhlika.kelana.service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Optional;

import org.locationtech.jts.geom.Coordinate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.fadhlika.kelana.dto.Feature;
import com.fadhlika.kelana.dto.FeatureCollection;
import com.fadhlika.kelana.dto.GeocodeProperties;
import com.fadhlika.kelana.model.Location;
import com.fadhlika.kelana.model.Place;
import com.fadhlika.kelana.repository.LocationRepository;
import com.fadhlika.kelana.repository.PhotonRepository;
import com.fadhlika.kelana.repository.PlaceRepository;

@Service
public class ReverseGeocodeService {
    private static final Logger logger = LoggerFactory.getLogger(ReverseGeocodeService.class);

    @Value("${reverse_geocode.batch_size}")
    private int batchSize;

    private final LocationRepository locationRepository;

    private final PhotonRepository photonRepository;

    private final PlaceRepository placeRepository;

    ReverseGeocodeService(LocationRepository locationRepository, PhotonRepository photonRepository,
            PlaceRepository placeRepository, ObjectMapper mapper) {
        this.locationRepository = locationRepository;
        this.photonRepository = photonRepository;
        this.placeRepository = placeRepository;
    }

    @Async
    public void startReverseGeocode() {
        processReverseGeocode();
    }

    @Scheduled(cron = "0 0 0 * * *")
    public synchronized void processReverseGeocode() {
        try {

            logger.debug("start running reverse geocode job");
            Instant start = Instant.now();

            int i = 0;
            while (true) {
                Optional<Location> location = locationRepository
                        .findLocation(Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
                                Optional.empty(), Optional.empty(), Optional.of(false));

                if (location.isEmpty())
                    break;

                Location l = location.get();
                Coordinate coord = l.getGeometry().getCoordinate();
                try {
                    FeatureCollection geocode = photonRepository.reverseGeocode(coord.y, coord.x, 3);

                    locationRepository.updateLocationGeocode(l.getId(), geocode);

                    for (Feature feature : geocode.features()) {
                        GeocodeProperties props = feature.convertProperties(new TypeReference<GeocodeProperties>() {
                        });

                        placeRepository.createPlace(new Place(
                                "photon",
                                feature.getGeometry(),
                                props.type(),
                                props.postcode(),
                                props.countrycode(),
                                props.name(),
                                props.country(),
                                props.country(),
                                props.district(),
                                props.locality(),
                                props.street(),
                                props.state(),
                                new FeatureCollection(new ArrayList<>() {
                                    {
                                        add(feature);
                                    }
                                })));
                    }

                } catch (Exception ex) {
                    logger.error("error: {}, skipping...", ex.getMessage());
                }

                i++;

                Thread.sleep(1000);
            }

            long duration = start.until(Instant.now(), ChronoUnit.SECONDS);
            logger.info("reverse geocoded {} locations in {}s", i, duration);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
