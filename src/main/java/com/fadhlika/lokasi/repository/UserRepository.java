/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.fadhlika.lokasi.repository;

import java.sql.ResultSet;
import java.util.List;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import com.fadhlika.lokasi.model.User;

/**
 *
 * @author fadhl
 */
@Repository
public class UserRepository {

    private static final Logger logger = LoggerFactory.getLogger(UserRepository.class);

    private final JdbcClient jdbcClient;

    @Autowired
    public UserRepository(DataSource dataSource) {
        this.jdbcClient = JdbcClient.create(dataSource);
    }

    private final RowMapper<User> userRowMapper = (ResultSet rs, int rowNum) -> new User(
            rs.getInt("id"),
            rs.getString("username"),
            rs.getString("password"),
            rs.getTimestamp("created_at"));

    public void createUser(User user) {
        jdbcClient.sql("INSERT INTO user(username, password, created_at) VALUES(?, ?, ?)")
                .param(user.getUsername())
                .param(user.getPassword())
                .param(user.getCreatedAt())
                .update();
    }

    public void updateUser(User user) {
        jdbcClient.sql("UPDATE user SET username = ?, password = ? WHERE id = ?")
                .param(user.getUsername())
                .param(user.getPassword())
                .param(user.getId())
                .update();
    }

    public User getUser(String username) {
        return jdbcClient.sql("SELECT id, username, password, created_at FROM `user` WHERE username = ?")
                .param(username)
                .query(userRowMapper)
                .single();
    }

    public User getUser(int userId) {
        return jdbcClient.sql("SELECT id, username, password, created_at FROM `user` WHERE id = ?")
                .param(userId)
                .query(userRowMapper)
                .single();
    }

    public boolean hasUsers() {
        return jdbcClient.sql("SELECT COUNT(*) FROM `user`").query(Integer.class).single() > 0;
    }

    public List<String> getUserDevices(int userId) {
        return jdbcClient.sql("SELECT DISTINCT device_id FROM `location` WHERE user_id = ?")
                .param(userId)
                .query(String.class)
                .list();
    }
}
