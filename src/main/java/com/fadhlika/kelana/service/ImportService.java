package com.fadhlika.kelana.service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.List;
import java.util.zip.CRC32;
import java.util.zip.Checksum;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fadhlika.kelana.dto.Feature;
import com.fadhlika.kelana.dto.FeatureCollection;
import com.fadhlika.kelana.exception.BadRequestException;
import com.fadhlika.kelana.exception.ConflictException;
import com.fadhlika.kelana.model.Import;
import com.fadhlika.kelana.model.Location;
import com.fadhlika.kelana.repository.ImportRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class ImportService {

    private static final Logger logger = LoggerFactory.getLogger(ImportService.class);

    private final LocationService locationService;

    private final ImportRepository importRepository;

    ImportService(LocationService locationService, ImportRepository importRepository) {
        this.locationService = locationService;
        this.importRepository = importRepository;
    }

    private void saveImport(int userId, String source, String filename, InputStream content) {
        try {
            byte[] contentBytes = content.readAllBytes();
            Checksum crc32 = new CRC32();
            crc32.update(contentBytes);
            String checksum = Long.toHexString(crc32.getValue());

            InputStream is = new ByteArrayInputStream(contentBytes);

            this.importRepository
                    .saveImport(
                            new Import(userId, source, filename, is, checksum));
        } catch (Exception e) {
            throw new InternalError(e.getMessage());
        }
    }

    public void deleteImport(int importId) throws IOException {
        this.importRepository.deleteImport(importId);
    }

    public Import importLocations(int userId, String source, String filename, InputStream content) {
        try {
            importRepository.fetch(userId, filename);
            throw new ConflictException("import review already exist");
        } catch (EmptyResultDataAccessException ex) {

        }

        saveImport(userId, source, filename, content);

        return importRepository.fetch(userId, filename);
    }

    public List<Import> getImports(int userId) {
        return importRepository.getImports(userId);
    }

    public Import getImport(int importId) {
        return importRepository.fetch(importId);
    }

    @Async
    public void processImport(Import anImport) {
        logger.info("Starting import {}", anImport.id());
        try {
            switch (anImport.source()) {
                case "dawarich" ->
                    importFromDawarich(anImport);
                default -> {

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        logger.info("Completed import {}", anImport.id());
    }

    @Transactional
    public void importFromDawarich(Import anImport)
            throws StreamReadException, DatabindException, IOException {

        ObjectMapper mapper = new ObjectMapper();
        FeatureCollection featureCollection = mapper.readValue(anImport.content(), FeatureCollection.class);

        for (Feature feature : featureCollection.features()) {
            Location l = new Location(feature.getGeometry());

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
    }
}
