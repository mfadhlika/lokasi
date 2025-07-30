package com.fadhlika.lokasi.service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.List;
import java.util.zip.CRC32;
import java.util.zip.Checksum;

import org.jobrunr.scheduling.BackgroundJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import com.fadhlika.lokasi.dto.Feature;
import com.fadhlika.lokasi.dto.FeatureCollection;
import com.fadhlika.lokasi.exception.BadRequestException;
import com.fadhlika.lokasi.exception.ConflictException;
import com.fadhlika.lokasi.model.Import;
import com.fadhlika.lokasi.model.Location;
import com.fadhlika.lokasi.repository.ImportRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class ImportService {

    private static final Logger logger = LoggerFactory.getLogger(ImportService.class);

    @Autowired
    private ImportRepository importRepository;

    @Autowired
    private LocationService locationService;

    @Autowired
    private PlatformTransactionManager transactionManager;

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

    public void importFromDawarich(int userId, String filename)
            throws StreamReadException, DatabindException, IOException {
        Import anImport = importRepository.fetch(userId, filename);

        logger.info("Starting import {}", anImport.id());

        ObjectMapper mapper = new ObjectMapper();
        FeatureCollection featureCollection = mapper.readValue(anImport.content(), FeatureCollection.class);

        TransactionStatus status = transactionManager.getTransaction(new DefaultTransactionDefinition());

        try {
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
                    anImport.count(),
                    anImport.created_at());

            importRepository.updateImport(anImport);

            transactionManager.commit(status);
        } catch (Exception e) {
            logger.error("Error importing {}: {}", anImport.id(), e.getMessage());
            transactionManager.rollback(status);
            throw e;
        }

        logger.info("Completed import {}", anImport.id());
    }

    public void importLocations(int userId, String source, String filename, InputStream content) {
        try {
            importRepository.fetch(userId, filename);
            throw new ConflictException("import review already exist");
        } catch (EmptyResultDataAccessException ex) {

        }

        saveImport(userId, source, filename, content);

        switch (source) {
            case "dawarich" ->
                BackgroundJob.enqueue(() -> importFromDawarich(userId, filename));
            default -> {

            }
        }
    }

    public List<Import> getImports(int userId) {
        return importRepository.getImports(userId);
    }
}
