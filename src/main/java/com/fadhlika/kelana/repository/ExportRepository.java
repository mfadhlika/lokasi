package com.fadhlika.kelana.repository;

import java.io.IOException;
import java.sql.ResultSet;
import java.time.OffsetDateTime;
import java.util.List;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import com.fadhlika.kelana.model.Export;

@Repository
public class ExportRepository {
    private final JdbcClient jdbcClient;

    private final RowMapper<Export> rowMapper = (ResultSet rs, int rowNum) -> {
        return new Export(
                rs.getInt("id"),
                rs.getInt("user_id"),
                rs.getString("filename"),
                rs.getObject("start_at", OffsetDateTime.class).toZonedDateTime(),
                rs.getObject("end_at", OffsetDateTime.class).toZonedDateTime(),
                rs.getBinaryStream("content"),
                rs.getBoolean("done"),
                rs.getObject("created_at", OffsetDateTime.class).toZonedDateTime());
    };

    ExportRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    public void save(Export export) throws IOException {
        jdbcClient.sql("""
                    INSERT INTO "export"(user_id, filename, start_at, end_at, content, done, created_at)
                    VALUES (?, ?, ?, ?, ?, ?, ?)
                    ON CONFLICT (user_id, filename) DO UPDATE SET content = excluded.content, done = excluded.done
                """)
                .param(export.userId())
                .param(export.filename())
                .param(export.startAt().toOffsetDateTime())
                .param(export.endAt().toOffsetDateTime())
                .param(export.content().readAllBytes())
                .param(export.done())
                .param(export.createdAt().toOffsetDateTime())
                .update();
    }

    public List<Export> fetch(int userId) {
        return jdbcClient.sql(
                "SELECT id, user_id, filename, start_at, end_at, done, null AS content, created_at FROM \"export\" WHERE user_id = ?")
                .param(userId)
                .query(rowMapper)
                .list();
    }

    public Export get(int id) {
        return jdbcClient.sql("""
                SELECT * FROM "export" WHERE id = ?
                """)
                .param(id)
                .query(rowMapper)
                .single();
    }

    public Export get(int userId, String filename) {
        return jdbcClient.sql("""
                SELECT * FROM "export" WHERE user_id = ? AND filename = ?
                """)
                .param(userId)
                .param(filename)
                .query(rowMapper)
                .single();
    }

    public void delete(int id) {
        jdbcClient.sql(" DELETE FROM export WHERE id = ?")
                .param(id)
                .update();
    }
}
