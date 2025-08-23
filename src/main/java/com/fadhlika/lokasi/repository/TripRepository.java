package com.fadhlika.lokasi.repository;

import java.sql.ResultSet;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import com.fadhlika.lokasi.model.Trip;

@Repository
public class TripRepository {
    @Autowired
    private JdbcClient jdbcClient;

    private final RowMapper<Trip> rowMapper = (ResultSet rs, int rowNum) -> new Trip(
            rs.getInt("user_id"),
            rs.getString("title"),
            ZonedDateTime.parse(rs.getString("start_at")),
            ZonedDateTime.parse(rs.getString("end_at")),
            ZonedDateTime.parse(rs.getString("created_at")),
            null,
            UUID.fromString(rs.getString("uuid")),
            rs.getBoolean("is_public"));

    public void saveTrip(Trip trip) {
        jdbcClient
                .sql("INSERT INTO trip(user_id, title, start_at, end_at, created_at, uuid, is_public) VALUES(?, ?, ?, ?, ?, ?, ?)")
                .param(trip.userId())
                .param(trip.title())
                .param(trip.startAt())
                .param(trip.endAt())
                .param(trip.createdAt())
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
}
