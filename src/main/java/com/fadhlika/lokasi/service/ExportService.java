package com.fadhlika.lokasi.service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fadhlika.lokasi.dto.Feature;
import com.fadhlika.lokasi.dto.FeatureCollection;
import com.fadhlika.lokasi.dto.PointProperties;
import com.fadhlika.lokasi.exception.InternalErrorException;
import com.fadhlika.lokasi.model.Export;
import com.fadhlika.lokasi.repository.ExportRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class ExportService {
    private static final Logger logger = LoggerFactory.getLogger(ExportService.class);

    @Autowired
    private ExportRepository exportRepository;

    @Autowired
    private LocationService locationService;

    public Export exportLocations(Export export) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss");
        String filename = String.format("export_%s_to_%s.json", export.startAt().format(formatter),
                export.endAt().format(formatter));

        export = new Export(export.userId(), filename, export.startAt(), export.endAt());
        try {
            exportRepository.save(export);
        } catch (IOException e) {
            throw new InternalErrorException(e.getMessage());
        }

        return exportRepository.get(export.userId(), export.filename());
    }

    public List<Export> getExports(int userId) {
        return exportRepository.fetch(userId);
    }

    public Export getExport(int id) {
        return exportRepository.get(id);
    }

    public void deleteExport(int id) {
        exportRepository.delete(id);
    }

    @Async
    public void processExportLocations(Export export) {
        logger.info("start exporting {}", export.id());

        try {
            List<Feature> features = locationService.findLocations(export.userId(), export.startAt(), export.endAt())
                    .map((location) -> {
                        PointProperties props = new PointProperties(
                                location.getTimestamp(),
                                location.getAltitude(),
                                location.getSpeed(),
                                location.getCourse(),
                                location.getCourseAccuracy(),
                                location.getAccuracy(),
                                location.getVerticalAccuracy(),
                                location.getMotions(),
                                location.getBatteryState().toString(),
                                location.getBattery(),
                                location.getDeviceId(),
                                location.getSsid(),
                                location.getGeocode(),
                                location.getRawData());

                        return new Feature(location.getGeometry(), props);
                    }).toList();

            FeatureCollection featureCollection = new FeatureCollection(features);

            ObjectMapper mapper = new ObjectMapper();
            ByteArrayInputStream is = new ByteArrayInputStream(mapper.writeValueAsBytes(featureCollection));

            export = new Export(export.id(), export.userId(), export.filename(), export.startAt(), export.endAt(),
                    is, true,
                    export.createdAt());
            exportRepository.save(export);
        } catch (Exception e) {
            logger.error("Error running processExport", e);
            return;
        }

        logger.info("export {} completed", export.id());
    }
}
