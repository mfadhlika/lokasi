package com.fadhlika.lokasi.repository;

import java.sql.ResultSet;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import com.fadhlika.lokasi.model.MqttMessage;

@Repository
public class MqttRepository {
    @Autowired
    private JdbcClient jdbcClient;

    public void createMessage(UUID uuid, String topic, String payload) {
        jdbcClient.sql("INSERT INTO owntracks_mqtt_message(uuid, topic, payload, created_at) VALUES (?, ?, ?, ?)")
                .param(uuid)
                .param(topic)
                .param(payload)
                .param(ZonedDateTime.now(ZoneOffset.UTC))
                .update();
    }

    public void updateMessageStatus(UUID uuid, MqttMessage.Status status) {
        jdbcClient.sql("UPDATE owntracks_mqtt_message SET status = ? WHERE uuid = ?")
                .param(status)
                .param(uuid)
                .update();
    }

    public void updateMessageStatus(UUID uuid, MqttMessage.Status status, String reason) {
        jdbcClient.sql("UPDATE owntracks_mqtt_message SET status = ?, reason = ? WHERE uuid = ?")
                .param(status)
                .param(reason)
                .param(uuid)
                .update();
    }

    public List<MqttMessage> fetchMessages(Integer limit, Integer offset) {
        return jdbcClient.sql("SELECT * FROM owntracks_mqtt_message LIMIT ? OFFSET ?")
                .param(limit)
                .param(offset)
                .query((ResultSet rs, int rowNum) -> {
                    return new MqttMessage(rs.getInt("id"), rs.getString("uuid"), rs.getString("topic"),
                            rs.getString("payload"), MqttMessage.Status.valueOf(rs.getString("status")),
                            rs.getString("reason"), ZonedDateTime.parse(rs.getString("created_at")));
                })
                .list();
    }
}
