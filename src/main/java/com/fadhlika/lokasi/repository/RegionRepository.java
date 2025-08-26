package com.fadhlika.lokasi.repository;

import java.io.IOException;
import java.sql.ResultSet;
import java.time.ZonedDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import com.fadhlika.lokasi.dto.FeatureCollection;
import com.fadhlika.lokasi.model.Region;
import com.fasterxml.jackson.databind.ObjectMapper;

@Repository
public class RegionRepository {
    @Autowired
    private JdbcClient jdbcClient;

    @Autowired
    private ObjectMapper mapper;

    private final RowMapper<Region> rowMapper = (ResultSet rs, int rowNum) -> {
        FeatureCollection geocode = null;
        try {
            geocode = mapper.readValue(rs.getBinaryStream("geocode"), FeatureCollection.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return new Region(
                rs.getInt("id"),
                rs.getInt("user_id"),
                rs.getString("desc"),
                rs.getDouble("lat"),
                rs.getDouble("lon"),
                rs.getInt("rad"),
                rs.getString("beacon_uuid"),
                rs.getInt("beacon_major"),
                rs.getInt("beacon_minor"),
                rs.getString("rid"),
                geocode,
                ZonedDateTime.parse(rs.getString("created_at")));
    };

    public void createRegion(Region region) {
        jdbcClient
                .sql("""
                            INSERT INTO region(user_id,desc, lat, lon, rad, beacon_uuid, beacon_major, beacon_minor, rid, geocode, created_at)
                            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                        """)
                .param(region.userId())
                .param(region.desc())
                .param(region.lat())
                .param(region.lon())
                .param(region.rad())
                .param(region.beaconUUID())
                .param(region.beaconMajor())
                .param(region.beaconMinor())
                .param(region.rid())
                .param(region.geocode())
                .param(region.createdAt())
                .update();
    }

    public List<Region> fetchRegions(int userId) {
        return jdbcClient.sql("SELECT * FROM region WHERE user_id = ?").param(userId).query(rowMapper).list();
    }
}
