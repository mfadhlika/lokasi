package com.fadhlika.lokasi.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import com.fadhlika.lokasi.model.Import;

@Repository
public class ImportRepository {

    private final JdbcClient jdbcClient;

    @Autowired
    public ImportRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcClient = JdbcClient.create(jdbcTemplate);
    }

    public int saveImport(Import anImport) {
        jdbcClient.sql(
                "INSERT INTO import(user_id, source, filename, path, content, checksum, created_at) VALUES (?, ?, ?, ?, ?, ?, ?)")
                .param(anImport.userId())
                .param(anImport.source())
                .param(anImport.filename())
                .param(anImport.path())
                .param(anImport.content())
                .param(anImport.checksum())
                .update();
        return jdbcClient.sql("SELECT last_insert_rowid()").query(Integer.class).single();
    }

    public void deleteImport(int importId) {
        jdbcClient.sql("DELETE FROM import WHERE id = ?").param(importId).update();
    }

    public void updateImportStatus(int importId, boolean status) {
        jdbcClient.sql("UPDATE import SET done = ? WHERE id = ?").param(status).param(importId).update();
    }
}
