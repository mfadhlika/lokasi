package com.fadhlika.lokasi.repository;

import java.sql.ResultSet;
import java.time.ZonedDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import com.fadhlika.lokasi.dto.Stats;

@Repository
public class StatsRepository {
    @Autowired
    private JdbcClient jdbClient;

    private final RowMapper<Stats> rowMapper = (ResultSet rs, int rowNum) -> new Stats(
            rs.getInt("total_points"),
            rs.getInt("total_reverse_geocoded_points"),
            rs.getInt("total_cities_visited"),
            rs.getInt("total_countries_visited"),
            ZonedDateTime.parse(rs.getString("last_point_timestamp")));

    public Stats getStats(int userId) {
        return jdbClient.sql("""
                    SELECT
                        COUNT(1) AS total_points,
                        SUM(geocode != jsonb('null')) AS total_reverse_geocoded_points,
                        COUNT(DISTINCT geocode->'$.features[0].properties.city') AS total_cities_visited,
                        COUNT(DISTINCT geocode->'$.features[0].properties.country') AS total_countries_visited,
                        MAX(timestamp) AS last_point_timestamp
                    FROM location
                    WHERE user_id = ?
                """)
                .param(userId)
                .query(rowMapper)
                .single();
    }
}
