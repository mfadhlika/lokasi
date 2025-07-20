package com.fadhlika.lokasi.repository;

import java.sql.ResultSet;
import java.time.ZonedDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import com.fadhlika.lokasi.model.Trip;

@Repository
public class TripRepository {
    @Autowired
    private JdbcClient jdbcClient;

    public void saveTrip(Trip trip) {
        jdbcClient
                .sql("INSERT INTO trip(user_id, title, start_at, end_at, created_at) VALUES(?, ?, ?, ?, ?)")
                .param(trip.userId())
                .param(trip.title())
                .param(trip.startAt())
                .param(trip.endAt())
                .param(trip.createdAt())
                .update();
    }

    public List<Trip> getTrips(int userId) {
        return jdbcClient.sql("SELECT * FROM trip WHERE user_id = ?")
                .param(userId)
                .query((ResultSet rs, int rowNum) -> new Trip(
                        rs.getInt("user_id"),
                        rs.getString("title"),
                        ZonedDateTime.parse(rs.getString("start_at")),
                        ZonedDateTime.parse(rs.getString("end_at")),
                        ZonedDateTime.parse(rs.getString("created_at"))))
                .list();
    }
}
