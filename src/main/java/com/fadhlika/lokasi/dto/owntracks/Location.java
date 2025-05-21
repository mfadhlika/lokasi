/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.fadhlika.lokasi.dto.owntracks;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 *
 * @author fadhl
 */
public class Location extends Message {

    public enum BatteryStatus {
        UNKNOWN(0),
        UNPLUGGED(1),
        CHARGING(2),
        FULL(3);

        @JsonValue
        public final int value;

        private BatteryStatus(int value) {
            this.value = value;
        }
    }

    public int acc;
    public int alt;
    public BatteryStatus bs;
    public int cog;
    public double lat;
    public double lon;
    public int rad;
    public String t;
    public String tid;
    public int tst;
    public int vac;
    public int vel;
    public double p;
    public String poi;
    public String image;
    @JsonProperty("imagename")
    public String imageName;
    public String conn;
    public String tag;
    public String topic;
    @JsonProperty("inregions")
    public List<String> inRegions;
    @JsonProperty("inrids")
    public List<String> inRids;
    @JsonProperty("SSID")
    public String ssid;
    @JsonProperty("BSSID")
    public String bssid;
    @JsonProperty("created_at")
    public int createdAt;
    public int m;
    @JsonProperty("_id")
    public String id;
}
