package com.fadhlika.lokasi.repository;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.fadhlika.lokasi.model.Integration;

@Repository
public class IntegrationRepository {

    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<Integration> rowMapper = (ResultSet rs, int rowNum) -> new Integration(
            rs.getInt("user_id"),
            rs.getBoolean("owntracks_enable"),
            rs.getString("owntracks_username"),
            null,
            rs.getBoolean("overland_enable"),
            rs.getString("overland_api_key"));

    @Autowired
    public IntegrationRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void save(Integration integration) throws SQLException {
        jdbcTemplate.update("""
            UPDATE integration 
            SET 
                owntracks_enable = ?, 
                owntracks_username = ?, 
                owntracks_password = IFNULL(?, owntracks_password),
                overland_enable = ?,
                overland_api_key = ?
                WHERE user_id = ?""",
                integration.owntracksEnable(),
                integration.owntracksUsername(),
                integration.owntracksPassword(),
                integration.overlandEnable(),
                integration.overlandApiKey(),
                integration.userId());
    }

    public Integration get(int userId) throws SQLException {
        return jdbcTemplate.queryForObject("SELECT user_id, owntracks_enable, owntracks_username, overland_enable, overland_api_key FROM integration WHERE user_id = ?", rowMapper, userId);
    }

    public Integration getByOwntracksUsername(String username) throws SQLException {
        return jdbcTemplate.queryForObject("SELECT * FROM integration WHERE owntracks_username = ?", rowMapper, username);
    }

    public Integration getByOverlandApiKey(String apikey) throws SQLException {
        return jdbcTemplate.queryForObject("SELECT * FROM integration WHERE overland_api_key = ?", rowMapper, apikey);
    }
}
