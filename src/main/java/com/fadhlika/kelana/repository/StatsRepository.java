package com.fadhlika.kelana.repository;

import com.fadhlika.kelana.dto.Stats;
import java.sql.ResultSet;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

@Repository
public class StatsRepository {

    @Autowired
    private JdbcClient jdbClient;

    private final RowMapper<Stats> rowMapper = (ResultSet rs, int rowNum) -> {

        OffsetDateTime lastPointTimestampOS = rs.getObject("last_point_timestamp", OffsetDateTime.class);
        ZonedDateTime lastPointTimestamp = null;
        if (lastPointTimestampOS != null) {
            lastPointTimestamp = lastPointTimestampOS.toZonedDateTime();
        }
        return new Stats(
                rs.getInt("total_points"),
                rs.getInt("total_reverse_geocoded_points"),
                rs.getInt("total_cities_visited"),
                rs.getInt("total_countries_visited"),
                lastPointTimestamp);
    };

    public Stats getStats(int userId) {
        return jdbClient
                .sql(
                        """
                                    SELECT
                                        COUNT(1) AS total_points,
                                        COUNT(CASE WHEN geocode != 'null' THEN 1 END) AS total_reverse_geocoded_points,
                                        COUNT(DISTINCT geocode->'features'->0->'properties'->>'city') AS total_cities_visited,
                                        COUNT(DISTINCT geocode->'features'->0->'properties'->>'country') AS total_countries_visited,
                                        MAX(timestamp) AS last_point_timestamp
                                    FROM location
                                    WHERE user_id = ?
                                """)
                .param(userId)
                .query(rowMapper)
                .single();
    }
}
