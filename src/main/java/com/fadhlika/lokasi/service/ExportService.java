package com.fadhlika.lokasi.service;

import java.io.IOException;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.jobrunr.scheduling.BackgroundJobRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fadhlika.lokasi.dto.jobs.ExportLocationJobRequest;
import com.fadhlika.lokasi.exception.InternalErrorException;
import com.fadhlika.lokasi.model.Export;
import com.fadhlika.lokasi.repository.ExportRepository;

@Service
public class ExportService {
    private static final Logger logger = LoggerFactory.getLogger(ExportService.class);

    @Autowired
    private ExportRepository exportRepository;

    @Transactional
    public void exportLocations(Export export) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss");
        String filename = String.format("export_%s_to_%s.json", export.startAt().format(formatter),
                export.endAt().format(formatter));

        export = new Export(export.userId(), filename, export.startAt(), export.endAt());
        try {
            exportRepository.save(export);
        } catch (IOException e) {
            throw new InternalErrorException(e.getMessage());
        }

        Export createdExport = exportRepository.get(export.userId(), export.filename());
        int exportId = createdExport.id();
        BackgroundJobRequest.schedule(Instant.now().plus(1, ChronoUnit.MINUTES),
                new ExportLocationJobRequest(exportId));
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
}
