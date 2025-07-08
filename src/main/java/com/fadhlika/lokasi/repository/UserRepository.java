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
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.fadhlika.lokasi.model.User;

/**
 *
 * @author fadhl
 */
@Repository
public class UserRepository {

    private static final Logger logger = LoggerFactory.getLogger(UserRepository.class);

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public UserRepository(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    private final RowMapper<User> userRowMapper = (ResultSet rs, int rowNum) -> new User(
            rs.getInt("id"),
            rs.getString("username"),
            rs.getString("password"),
            rs.getTimestamp("created_at")
    );

    public void createUser(User user) {
        jdbcTemplate.update("INSERT INTO user(username, password, created_at) VALUES(?, ?, ?)", user.getUsername(),
                user.getPassword(),
                user.getCreatedAt()
        );
    }

    public void updateUser(User user) {
        jdbcTemplate.update("UPDATE user SET username = ?, password = ? WHERE id = ?",
                user.getUsername(),
                user.getPassword(),
                user.getId()
        );
    }

    public User getUser(String username) {
        return jdbcTemplate.queryForObject("SELECT id, username, password, created_at FROM `user` WHERE username = ?", userRowMapper, username);
    }

    public boolean hasUsers() {
        return jdbcTemplate.queryForObject("SELECT COUNT(*) FROM `user`", Integer.class) > 0;
    }

    public List<String> getUserDevices(int userId) {
        return jdbcTemplate.queryForList("SELECT DISTINCT device_id FROM `location` WHERE user_id = ?", String.class, userId);
    }
}
