package com.fadhlika.lokasi.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.fadhlika.lokasi.model.Import;

@Repository
public class ImportRepository {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public ImportRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public int saveImport(Import anImport) {
        jdbcTemplate.update(
                "INSERT INTO import(user_id, source, filename, path, content, checksum, created_at) VALUES (?, ?, ?, ?, ?, ?, ?)",
                anImport.userId(), anImport.source(), anImport.filename(), anImport.path(), anImport.content(), anImport.checksum());
        return jdbcTemplate.queryForObject("SELECT last_insert_rowid()", Integer.class);
    }

    public void deleteImport(int importId) {
        jdbcTemplate.update("DELETE FROM import WHERE id = ?", importId);
    }

    public void updateImportStatus(int importId, boolean status) {
        jdbcTemplate.update("UPDATE import SET done = ? WHERE id = ?", status, importId);
    }
}
