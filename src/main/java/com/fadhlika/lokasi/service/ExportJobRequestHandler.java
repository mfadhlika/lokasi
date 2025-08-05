package com.fadhlika.lokasi.service;

import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.List;

import org.jobrunr.jobs.lambdas.JobRequestHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.fadhlika.lokasi.dto.Feature;
import com.fadhlika.lokasi.dto.FeatureCollection;
import com.fadhlika.lokasi.dto.PointProperties;
import com.fadhlika.lokasi.model.Export;
import com.fadhlika.lokasi.model.Location;
import com.fadhlika.lokasi.repository.ExportRepository;
import com.fadhlika.lokasi.service.dto.ExportLocationJobRequest;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ExportJobRequestHandler implements JobRequestHandler<ExportLocationJobRequest> {
    private static final Logger logger = LoggerFactory.getLogger(ExportJobRequestHandler.class);

    @Autowired
    private LocationService locationService;

    @Autowired
    private ExportRepository exportRepository;

    @Override
    public void run(ExportLocationJobRequest args) throws Exception {
        logger.info("start exporting {}", args.exportId());

        try {
            Export export = exportRepository.get(args.exportId());
            List<Location> locations = locationService.findLocations(export.userId(), export.startAt(), export.endAt());

            List<Feature> features = locations.stream().map((location) -> {
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
                        location.getRawData());

                return new Feature(location.getGeometry(), props);
            }).toList();

            FeatureCollection featureCollection = new FeatureCollection(features);

            ObjectMapper mapper = new ObjectMapper();
            PipedInputStream is = new PipedInputStream();
            PipedOutputStream os = new PipedOutputStream(is);
            mapper.writeValue(os, featureCollection);

            export = new Export(export.id(), export.userId(), export.filename(), export.startAt(), export.endAt(),
                    is, true,
                    export.createdAt());
            exportRepository.save(export);
        } catch (Exception e) {
            logger.error("Error running processExport", e);
            throw e;
        }

        logger.info("export {} completed", args.exportId());
    }

}
