package com.fadhlika.lokasi.repository;

import java.io.IOException;
import java.sql.ResultSet;
import java.time.ZonedDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import com.fadhlika.lokasi.model.Export;

@Repository
public class ExportRepository {
    @Autowired
    private JdbcClient jdbcClient;

    private final RowMapper<Export> rowMapper = (ResultSet rs, int rowNum) -> {
        return new Export(
                rs.getInt("id"),
                rs.getInt("user_id"),
                rs.getString("filename"),
                ZonedDateTime.parse(rs.getString("start_at")),
                ZonedDateTime.parse(rs.getString("end_at")),
                rs.getAsciiStream("content"),
                rs.getBoolean("done"),
                ZonedDateTime.parse(rs.getString("created_at")));
    };

    public void save(Export export) throws IOException {
        jdbcClient.sql("""
                    INSERT INTO export(user_id, filename, start_at, end_at, content, done, created_at)
                    VALUES (?, ?, ?, ?, ?, ?, ?)
                    ON CONFLICT DO UPDATE SET content = excluded.content, done = excluded.done
                """)
                .param(export.userId())
                .param(export.filename())
                .param(export.startAt())
                .param(export.endAt())
                .param(export.content().readAllBytes())
                .param(export.done())
                .param(export.createdAt())
                .update();
    }

    public List<Export> fetch(int userId) {
        return jdbcClient.sql("""
                SELECT * FROM export WHERE user_id = ?
                """)
                .param(userId)
                .query(rowMapper)
                .list();
    }

    public Export get(int id) {
        return jdbcClient.sql("""
                SELECT * FROM export WHERE id = ?
                """)
                .param(id)
                .query(rowMapper)
                .single();
    }

    public Export get(int userId, String filename) {
        return jdbcClient.sql("""
                SELECT * FROM export WHERE user_id = ? AND filename = ?
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
