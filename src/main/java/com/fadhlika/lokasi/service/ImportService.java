package com.fadhlika.lokasi.service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.zip.CRC32;
import java.util.zip.Checksum;

import org.jobrunr.scheduling.BackgroundJobRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import com.fadhlika.lokasi.dto.jobs.ImportLocationJobRequest;
import com.fadhlika.lokasi.exception.ConflictException;
import com.fadhlika.lokasi.model.Import;
import com.fadhlika.lokasi.repository.ImportRepository;

@Service
public class ImportService {

    private static final Logger logger = LoggerFactory.getLogger(ImportService.class);

    @Autowired
    private ImportRepository importRepository;

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

    public void importLocations(int userId, String source, String filename, InputStream content) {
        try {
            importRepository.fetch(userId, filename);
            throw new ConflictException("import review already exist");
        } catch (EmptyResultDataAccessException ex) {

        }

        saveImport(userId, source, filename, content);

        Import anImport = importRepository.fetch(userId, filename);

        BackgroundJobRequest.enqueue(new ImportLocationJobRequest(anImport.id()));
    }

    public List<Import> getImports(int userId) {
        return importRepository.getImports(userId);
    }

    public Import getImport(int importId) {
        return importRepository.fetch(importId);
    }
}
