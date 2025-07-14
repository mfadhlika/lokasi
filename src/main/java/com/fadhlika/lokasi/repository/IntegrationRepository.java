package com.fadhlika.lokasi.repository;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import com.fadhlika.lokasi.model.Integration;

@Repository
public class IntegrationRepository {

    private final JdbcClient jdbcClient;

    private final RowMapper<Integration> rowMapper = (ResultSet rs, int rowNum) -> new Integration(
            rs.getInt("user_id"),
            rs.getBoolean("owntracks_enable"),
            rs.getString("owntracks_username"),
            null,
            rs.getBoolean("overland_enable"),
            rs.getString("overland_api_key"));

    @Autowired
    public IntegrationRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcClient = JdbcClient.create(jdbcTemplate);
    }

    public void save(Integration integration) throws SQLException {
        jdbcClient.sql("""
                UPDATE integration
                SET
                    owntracks_enable = ?,
                    owntracks_username = ?,
                    owntracks_password = IFNULL(?, owntracks_password),
                    overland_enable = ?,
                    overland_api_key = ?
                    WHERE user_id = ?""")
                .param(integration.owntracksEnable())
                .param(integration.owntracksUsername())
                .param(integration.owntracksPassword())
                .param(integration.overlandEnable())
                .param(integration.overlandApiKey())
                .param(integration.userId())
                .update();
    }

    public Integration get(int userId) throws SQLException {
        return jdbcClient.sql(
                "SELECT user_id, owntracks_enable, owntracks_username, overland_enable, overland_api_key FROM integration WHERE user_id = ?")
                .param(userId)
                .query(rowMapper)
                .single();
    }

    public Integration getByOwntracksUsername(String username) throws SQLException {
        return jdbcClient.sql("SELECT * FROM integration WHERE owntracks_username = ?")
                .param(username)
                .query(rowMapper)
                .single();
    }

    public Integration getByOverlandApiKey(String apikey) throws SQLException {
        return jdbcClient.sql("SELECT * FROM integration WHERE overland_api_key = ?")
                .param(apikey)
                .query(rowMapper)
                .single();
    }
}
