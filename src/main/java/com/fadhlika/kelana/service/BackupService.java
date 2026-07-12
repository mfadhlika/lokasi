package com.fadhlika.kelana.service;

import java.sql.SQLException;

import org.springframework.stereotype.Service;

import com.fadhlika.kelana.exception.InternalErrorException;
import com.fadhlika.kelana.repository.BackupRepository;

@Service
public class BackupService {
    private final BackupRepository backupRepository;

    BackupService(BackupRepository backupRepository) {
        this.backupRepository = backupRepository;
    }

    public void createBackup() {
        try {
            backupRepository.createBackup();
        } catch (SQLException ex) {
            throw new InternalErrorException(ex.getMessage());
        }
    }
}
