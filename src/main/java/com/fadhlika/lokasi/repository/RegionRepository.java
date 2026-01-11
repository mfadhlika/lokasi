package com.fadhlika.lokasi.repository;

import java.io.IOException;
import java.io.InputStream;
import java.sql.ResultSet;
import java.time.ZonedDateTime;
import java.util.List;

import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKBReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import com.fadhlika.lokasi.dto.FeatureCollection;
import com.fadhlika.lokasi.model.Region;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Repository
public class RegionRepository {
    @Autowired
    private JdbcClient jdbcClient;

    @Autowired
    private ObjectMapper mapper;

    private final WKBReader wkbReader = new WKBReader();

    private final RowMapper<Region> rowMapper = (ResultSet rs, int rowNum) -> {
        FeatureCollection geocode = null;
        InputStream geocodeIS = rs.getBinaryStream("geocode");
        if (geocodeIS != null) {
            try {
                geocode = mapper.readValue(geocodeIS, FeatureCollection.class);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        byte[] point = rs.getBytes("geometry");
        Geometry geometry;
        try {
            geometry = wkbReader.read(point);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        return new Region(rs.getInt("id"), rs.getInt("user_id"), rs.getString("desc"), geometry,
                rs.getString("beacon_uuid"), rs.getInt("beacon_major"),
                rs.getInt("beacon_minor"), rs.getString("rid"), geocode,
                ZonedDateTime.parse(rs.getString("created_at")));
    };

    public void createRegion(Region region) throws JsonProcessingException {
        jdbcClient
                .sql("""
                            INSERT INTO region(user_id, desc, geometry, beacon_uuid, beacon_major, beacon_minor, rid, geocode, created_at)
                            VALUES (?, ?, ST_GeomFromText(?), ?, ?, ?, ?, jsonb(?), ?)
                        """)
                .param(region.getUserId())
                .param(region.getDesc())
                .param(region.getGeometry())
                .param(region.getBeaconUUID())
                .param(region.getBeaconMajor())
                .param(region.getBeaconMinor())
                .param(region.getRid())
                .param(mapper.writeValueAsString(region.getGeocode()))
                .param(region.getCreatedAt())
                .update();
    }

    public List<Region> fetchRegions(int userId) {
        return jdbcClient.sql(
                "SELECT id, user_id, desc, ST_AsBinary(geometry) AS geometry,  beacon_uuid, beacon_major, beacon_minor, rid, geocode, created_at FROM region WHERE user_id = ?")
                .param(userId).query(rowMapper).list();
    }
}
