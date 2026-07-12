package com.fadhlika.kelana.service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.fadhlika.kelana.dto.Feature;
import com.fadhlika.kelana.dto.FeatureCollection;
import com.fadhlika.kelana.dto.PointProperties;
import com.fadhlika.kelana.exception.InternalErrorException;
import com.fadhlika.kelana.model.Export;
import com.fadhlika.kelana.repository.ExportRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class ExportService {
    private static final Logger logger = LoggerFactory.getLogger(ExportService.class);

    private final ExportRepository exportRepository;

    private final LocationService locationService;

    ExportService(ExportRepository exportRepository, LocationService locationService) {
        this.exportRepository = exportRepository;
        this.locationService = locationService;
    }

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
                    .stream().map((location) -> {
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
                                location.getPressure(),
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
