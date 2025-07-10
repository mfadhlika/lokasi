package com.fadhlika.lokasi.service;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.List;
import java.util.zip.CRC32;
import java.util.zip.Checksum;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.fadhlika.lokasi.dto.FeatureCollection;
import com.fadhlika.lokasi.exception.BadRequestException;
import com.fadhlika.lokasi.model.Import;
import com.fadhlika.lokasi.model.Location;
import com.fadhlika.lokasi.repository.ImportRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class ImportService {

    private static final Logger logger = LoggerFactory.getLogger(ImportService.class);

    private final ImportRepository importRepository;

    private final LocationService locationService;

    @Autowired
    public ImportService(ImportRepository importRepository, LocationService locationService) {
        this.importRepository = importRepository;
        this.locationService = locationService;
    }

    public int saveImport(int userId, String source, String filename, ByteBuffer content) throws IOException {
        Checksum crc32 = new CRC32();
        crc32.update(content);
        String checksum = Long.toHexString(crc32.getValue());

        return this.importRepository.saveImport(new Import(userId, source, filename, null, null, checksum, false, LocalDateTime.now()));
    }

    public void deleteImport(int importId) throws IOException {
        this.importRepository.deleteImport(importId);
    }

    public void importFromDawarich(int userId, int importId, ByteBuffer content) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            FeatureCollection featureCollection = mapper.readValue(content.array(), FeatureCollection.class);
            List<Location> locations = featureCollection.features().stream().map(feature -> {
                Location l = new Location();

                l.setGeometry(feature.getGeometry());
                l.setUserId(userId);
                l.setImportId(importId);

                HashMap<String, Object> properties = feature.getProperties();

                l.setTimestamp(Instant.ofEpochSecond((int) properties.get("timestamp")).atZone(ZoneOffset.UTC).toLocalDateTime());
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

                return l;
            }).toList();

            this.locationService.saveLocations(locations);
        } catch (IOException e) {
            try {
                deleteImport(importId);
            } catch (IOException ex) {
                throw new RuntimeException(e);
            }

            throw new RuntimeException(e);
        }
    }

    @Async
    public void doImport(int userId, String source, String filename, ByteBuffer content) throws IOException {
        int importId = saveImport(userId, source, filename, content);

        logger.info("starting import {}", importId);

        switch (source) {
            case "dawarich" ->
                importFromDawarich(userId, importId, content);
            default -> {
            }
        }

        importRepository.updateImportStatus(importId, true);
        logger.info("import {} done", importId);
    }
}
