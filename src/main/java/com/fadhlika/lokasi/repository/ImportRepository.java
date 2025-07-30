package com.fadhlika.lokasi.repository;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.ResultSet;
import java.time.ZonedDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
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

    private final RowMapper<Import> importRowMapper = (ResultSet rs, int rowNum) -> new Import(
            rs.getInt("id"),
            rs.getInt("user_id"),
            rs.getString("source"),
            rs.getString("filename"),
            rs.getBinaryStream("content"),
            rs.getString("checksum"),
            rs.getBoolean("done"),
            rs.getInt("count"),
            ZonedDateTime.parse(rs.getString("created_at")));

    public void saveImport(Import anImport) throws IOException {
        jdbcClient.sql(
                "INSERT INTO import(user_id, source, filename, content, checksum, count, created_at) VALUES (?, ?, ?, ?, ?, ?, ?)")
                .param(anImport.userId())
                .param(anImport.source())
                .param(anImport.filename())
                .param(new String(anImport.content().readAllBytes(), StandardCharsets.UTF_8))
                .param(anImport.count())
                .param(anImport.checksum())
                .param(anImport.created_at())
                .update();
    }

    public Import fetch(int userId, String filename) {
        return jdbcClient
                .sql("SELECT * FROM import WHERE user_id = ? AND filename = ? LIMIT 1")
                .param(userId)
                .param(filename)
                .query(importRowMapper)
                .single();
    }

    public Import fetch(int id) {
        return jdbcClient
                .sql("SELECT * FROM import WHERE id = ? LIMIT 1")
                .param(id)
                .query(importRowMapper)
                .single();
    }

    public void deleteImport(int importId) {
        jdbcClient.sql("DELETE FROM import WHERE id = ?").param(importId).update();
    }

    public void updateImport(Import anImport) {
        jdbcClient.sql("""
                UPDATE
                    import
                SET
                    count = ?,
                    done = ?
                WHERE
                    id = ?""")
                .param(anImport.count())
                .param(anImport.done())
                .param(anImport.id())
                .update();
    }
}
