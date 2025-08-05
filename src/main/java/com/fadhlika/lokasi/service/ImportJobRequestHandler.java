package com.fadhlika.lokasi.service;

import java.io.IOException;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.HashMap;

import org.jobrunr.jobs.lambdas.JobRequestHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.fadhlika.lokasi.dto.Feature;
import com.fadhlika.lokasi.dto.FeatureCollection;
import com.fadhlika.lokasi.exception.BadRequestException;
import com.fadhlika.lokasi.model.Import;
import com.fadhlika.lokasi.model.Location;
import com.fadhlika.lokasi.repository.ImportRepository;
import com.fadhlika.lokasi.service.dto.ImportLocationJobRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ImportJobRequestHandler implements JobRequestHandler<ImportLocationJobRequest> {
    private static final Logger logger = LoggerFactory.getLogger(ImportJobRequestHandler.class);

    @Autowired
    private ImportRepository importRepository;

    @Autowired
    private LocationService locationService;

    @Override
    public void run(ImportLocationJobRequest args) throws Exception {
        try {
            Import anImport = importRepository.fetch(args.importId());

            switch (anImport.source()) {
                case "dawarich" ->
                    importFromDawarich(anImport);
                default -> {

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    @Transactional
    public void importFromDawarich(Import anImport)
            throws StreamReadException, DatabindException, IOException {

        logger.info("Starting import {}", anImport.id());

        ObjectMapper mapper = new ObjectMapper();
        FeatureCollection featureCollection = mapper.readValue(anImport.content(), FeatureCollection.class);

        for (Feature feature : featureCollection.features()) {
            Location l = new Location();

            l.setGeometry(feature.getGeometry());
            l.setUserId(anImport.userId());
            l.setImportId(anImport.id());

            HashMap<String, Object> properties = feature.getProperties();

            l.setTimestamp(Instant.ofEpochSecond((int) properties.get("timestamp")).atZone(ZoneOffset.UTC));
            if (properties.get("altitude") != null) {
                l.setAltitude((int) properties.get("altitude"));
            }
            if (properties.get("ssid") != null) {
                l.setSsid((String) properties.get("ssid"));
            }
            if (properties.get("accuracy") != null) {
                l.setAccuracy((int) properties.get("accuracy"));
            }
            if (properties.get("vertical_accuracy") != null) {
                l.setVerticalAccuracy((int) properties.get("vertical_accuracy"));
            }
            if (properties.get("tracker_id") != null) {
                l.setDeviceId((String) properties.get("tracker_id"));
            }
            if (properties.get("battery") != null) {
                l.setBattery((int) properties.get("battery"));
            }
            if (properties.get("battery_state") != null) {
                l.setBatteryState((String) properties.get("battery_state"));
            }
            if (properties.get("velocity") != null) {
                l.setSpeed(Double.parseDouble((String) properties.get("velocity")));
            }

            try {
                l.setRawData(feature);
            } catch (JsonProcessingException e) {
                throw new BadRequestException(e.getMessage());
            }

            locationService.saveLocation(l);
        }

        anImport = new Import(
                anImport.id(),
                anImport.userId(),
                anImport.source(),
                anImport.filename(),
                anImport.content(),
                anImport.checksum(),
                true,
                featureCollection.features().size(),
                anImport.createdAt());

        importRepository.updateImport(anImport);

        logger.info("Completed import {}", anImport.id());
    }

}
