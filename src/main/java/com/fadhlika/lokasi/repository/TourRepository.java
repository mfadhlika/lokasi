package com.fadhlika.lokasi.repository;

import java.sql.ResultSet;
import java.time.ZonedDateTime;
import java.util.UUID;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import com.fadhlika.lokasi.model.Tour;

@Repository
public class TourRepository {
    @Autowired
    private JdbcClient jdbcClient;

    private RowMapper<Tour> rowMapper = (ResultSet rs, int rowNum) -> new Tour(
            rs.getInt("id"),
            rs.getInt("user_id"),
            UUID.fromString(rs.getString("uuid")),
            rs.getString("label"),
            ZonedDateTime.parse(rs.getString("from")),
            ZonedDateTime.parse(rs.getString("to")),
            ZonedDateTime.parse(rs.getString("created_at")));

    public void createTour(Tour tour) {
        jdbcClient.sql("""
                    INSERT INTO owntracks_tour(user_id, uuid, label, `from`, `to`, created_at)
                    VALUES(?, ?, ?, ?, ?, ?)
                """)
                .param(tour.userId())
                .param(tour.uuid().toString())
                .param(tour.label())
                .param(tour.from())
                .param(tour.to())
                .param(tour.createdAt())
                .update();
    }

    public Stream<Tour> fetchTours(int userId) {
        return jdbcClient
                .sql("SELECT * FROM owntracks_tour WHERE user_id = ?")
                .param(userId)
                .query(rowMapper)
                .stream();
    }

    public Tour fetchTour(UUID uuid) {
        return jdbcClient
                .sql("SELECT * FROM owntracks_tour WHERE uuid = ?")
                .param(uuid)
                .query(rowMapper)
                .single();
    }

    public void deleteTour(UUID uuid) {
        jdbcClient
                .sql("DEKETE FROM owntracks_tour WHERE uuid = ?")
                .param(uuid)
                .update();
    }
}
