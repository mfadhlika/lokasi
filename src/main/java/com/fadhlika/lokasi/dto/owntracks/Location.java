/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.fadhlika.lokasi.dto.owntracks;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;

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
        @JsonProperty("imagename")
        String imageName,
        String conn,
        String tag,
        String topic,
        @JsonProperty("inregions")
        List<String> inRegions,
        @JsonProperty("inrids")
        List<String> inRids,
        @JsonProperty("SSID")
        String ssid,
        @JsonProperty("BSSID")
        String bssid,
        @JsonProperty("created_at")
        int createdAt,
        int m,
        @JsonProperty("_id")
        String id) implements Message {
}
