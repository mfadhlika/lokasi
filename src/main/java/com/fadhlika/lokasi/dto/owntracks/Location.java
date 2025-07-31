/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.fadhlika.lokasi.dto.owntracks;

import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.core.JsonProcessingException;

/**
 * @author fadhl
 */
@JsonTypeName("location")
public record Location(
        int acc,
        int alt,
        int bs,
        int cog,
        double lat,
        double lon,
        int rad,
        String t,
        String tid,
        int tst,
        int vac,
        int vel,
        double p,
        String poi,
        String image,
        @JsonProperty("imagename") String imageName,
        String conn,
        String tag,
        String topic,
        @JsonProperty("inregions") List<String> inRegions,
        @JsonProperty("inrids") List<String> inRids,
        @JsonProperty("motionactivities") List<String> motions,
        @JsonProperty("SSID") String ssid,
        @JsonProperty("BSSID") String bssid,
        @JsonProperty("created_at") int createdAt,
        int m,
        @JsonProperty("_id") String id) implements Message {

    public com.fadhlika.lokasi.model.Location toLocation(int userId, String deviceId) throws JsonProcessingException {
        com.fadhlika.lokasi.model.Location l = new com.fadhlika.lokasi.model.Location();

        l.setUserId(userId);
        l.setDeviceId(deviceId);
        l.setGeometry(this.lat(), this.lon());
        l.setAltitude(this.alt());
        l.setBatteryState(this.bs());
        l.setCourse(this.cog());
        l.setAccuracy(this.acc());
        l.setVerticalAccuracy(this.vac());
        l.setSpeed(this.vel());
        l.setSsid(this.ssid());
        l.setTimestamp(Instant.ofEpochSecond(this.tst()).atZone(ZoneOffset.UTC));

        l.setRawData(this);

        return l;
    }
}
