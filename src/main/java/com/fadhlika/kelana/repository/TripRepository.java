package com.fadhlika.kelana.repository;

import java.sql.ResultSet;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import com.fadhlika.kelana.model.Trip;

@Repository
public class TripRepository {
    private final JdbcClient jdbcClient;

    private final RowMapper<Trip> rowMapper = (ResultSet rs, int rowNum) -> {
        UUID uuid = null;
        if (rs.getString("uuid") != null)
            uuid = UUID.fromString(rs.getString("uuid"));
        return new Trip(
                rs.getInt("id"),
                rs.getInt("user_id"),
                rs.getString("title"),
                rs.getObject("start_at", OffsetDateTime.class).toZonedDateTime(),
                rs.getObject("end_at", OffsetDateTime.class).toZonedDateTime(),
                rs.getObject("created_at", OffsetDateTime.class).toZonedDateTime(),
                null,
                uuid,
                rs.getBoolean("is_public"));
    };

    TripRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    public void saveTrip(Trip trip) {
        jdbcClient
                .sql("""
                        INSERT INTO trip(user_id, title, start_at, end_at, created_at, uuid, is_public)
                        VALUES(?, ?, ?, ?, ?, ?, ?)
                        ON CONFLICT(uuid) DO UPDATE SET
                            user_id = excluded.user_id,
                            title = excluded.title,
                            start_at = excluded.start_at,
                            end_at = excluded.end_at,
                            created_at = excluded.created_at,
                            is_public = excluded.is_public
                        """)
                .param(trip.userId())
                .param(trip.title())
                .param(trip.startAt().toOffsetDateTime())
                .param(trip.endAt().toOffsetDateTime())
                .param(trip.createdAt().toOffsetDateTime())
                .param(trip.uuid())
                .param(trip.isPublic())
                .update();
    }

    public List<Trip> getTrips(int userId, Optional<Boolean> isPublic) {
        List<String> where = new ArrayList<>() {
            {
                add("user_id = ?");
            }
        };

        List<Object> args = new ArrayList<Object>() {
            {
                add(userId);
            }
        };

        isPublic.ifPresent((v) -> {
            where.add("is_public = ?");
            args.add(v);
        });

        String sql = "SELECT * FROM trip WHERE " + String.join(" AND ", where);

        return jdbcClient.sql(sql)
                .params(args)
                .query(rowMapper)
                .list();
    }

    public Trip getTrip(UUID uuid) {
        return jdbcClient.sql("SELECT * FROM trip WHERE uuid = ?")
                .param(uuid)
                .query(rowMapper)
                .single();
    }

    public void deleteTrip(UUID uuid) {
        jdbcClient.sql("DELETE FROM trip WHERE uuid = ?")
                .param(uuid)
                .update();
    }

    public void deleteTrip(int id) {
        jdbcClient.sql("DELETE FROM trip WHERE id = ?")
                .param(id)
                .update();
    }
}
