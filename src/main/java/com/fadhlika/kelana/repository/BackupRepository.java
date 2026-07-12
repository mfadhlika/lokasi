package com.fadhlika.kelana.repository;

import java.sql.SQLException;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

@Repository
public class BackupRepository {
    @Value("${kelana.backup_dir}")
    private String backupDir;

    @SuppressWarnings("unused")
    private final DataSource ds;

    BackupRepository(DataSource ds) {
        this.ds = ds;
    }

    public void createBackup() throws SQLException {

    }
}
