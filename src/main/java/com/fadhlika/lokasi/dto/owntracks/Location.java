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
        Integer acc,
        Integer alt,
        Integer batt,
        Integer bs,
        Integer cog,
        double lat,
        double lon,
        Integer rad,
        String t,
        String tid,
        int tst,
        Integer vac,
        Integer vel,
        Double p,
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
        Integer m,
        @JsonProperty("_id") String id) implements Message {

    public com.fadhlika.lokasi.model.Location toLocation(int userId, String deviceId) throws JsonProcessingException {
        com.fadhlika.lokasi.model.Location l = new com.fadhlika.lokasi.model.Location();

        l.setUserId(userId);
        if (deviceId != null)
            l.setDeviceId(deviceId);
        l.setGeometry(this.lon(), this.lat());
        if (this.alt() != null)
            l.setAltitude(this.alt());
        if (this.bs() != null)
            l.setBatteryState(this.bs());
        if (this.batt() != null)
            l.setBattery(this.batt());
        if (this.cog() != null)
            l.setCourse(this.cog());
        if (this.acc() != null)
            l.setAccuracy(this.acc());
        if (this.vac() != null)
            l.setVerticalAccuracy(this.vac());
        if (this.vel() != null)
            l.setSpeed(this.vel());
        if (this.ssid() != null)
            l.setSsid(this.ssid());
        if (this.motions() != null)
            l.setMotions(this.motions());
        l.setTimestamp(Instant.ofEpochSecond(this.tst()).atZone(ZoneOffset.UTC));

        l.setRawData(this);

        return l;
    }
}
