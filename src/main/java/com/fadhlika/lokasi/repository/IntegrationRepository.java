package com.fadhlika.lokasi.repository;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import com.fadhlika.lokasi.model.Integration;

@Repository
public class IntegrationRepository {

    @Autowired
    private JdbcClient jdbcClient;

    private final RowMapper<Integration> rowMapper = (ResultSet rs, int rowNum) -> new Integration(
            rs.getInt("user_id"),
            rs.getString("owntracks_username"),
            rs.getString("owntracks_password"),
            rs.getString("overland_api_key"));

    public void save(Integration integration) throws SQLException {
        jdbcClient
                .sql("""
                        INSERT INTO integration(user_id, owntracks_username, owntracks_password, overland_api_key) VALUES (?, ?, ?, ?)
                        ON CONFLICT(user_id) DO UPDATE SET
                            owntracks_username=excluded.owntracks_username,
                            owntracks_password=IFNULL(excluded.owntracks_password, owntracks_password),
                            overland_api_key=excluded.overland_api_key""")
                .param(integration.userId())
                .param(integration.owntracksUsername())
                .param(integration.owntracksPassword())
                .param(integration.overlandApiKey())
                .update();
    }

    public Integration get(int userId) throws SQLException {
        return jdbcClient.sql(
                "SELECT * FROM integration WHERE user_id = ?")
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
