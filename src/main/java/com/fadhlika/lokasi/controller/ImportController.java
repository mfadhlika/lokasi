package com.fadhlika.lokasi.controller;

import com.fadhlika.lokasi.dto.FeatureCollection;
import com.fadhlika.lokasi.model.Location;
import com.fadhlika.lokasi.model.User;
import com.fadhlika.lokasi.service.LocationService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.List;

@Controller
public class ImportController {
    private final LocationService locationService;

    @Autowired
    public ImportController(LocationService locationService) {
        this.locationService = locationService;
    }

    @GetMapping("/import")
    public String getImport(Model model) {

        return "import";
    }

    @PostMapping("/import")
    public String postImport(@RequestParam String source, @RequestParam MultipartFile content) throws IOException {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        List<Location> locations = null;
        if (source.equals("Dawarich")) {
            ObjectMapper mapper = new ObjectMapper().findAndRegisterModules();
            FeatureCollection featureCollection = mapper.readValue(content.getInputStream(), FeatureCollection.class);

            locations = featureCollection.features().stream().map(feature -> {
                Location l = new Location();

                l.setUserId(user.getId());
                l.setGeometry(feature.getGeometry());

                HashMap<String, Object> properties = feature.convertProperties();
                l.setTimestamp(Instant.ofEpochSecond((int) properties.get("timestamp")).atZone(ZoneOffset.UTC).toLocalDateTime());
                if (properties.get("altitude") != null) l.setAltitude((int) properties.get("altitude"));
                if (properties.get("ssid") != null) l.setSsid((String) properties.get("ssid"));
                if (properties.get("accuracy") != null) l.setAccuracy((int) properties.get("accuracy"));
                if (properties.get("vertical_accuracy") != null)
                    l.setVerticalAccuracy((int) properties.get("vertical_accuracy"));
                if (properties.get("tracker_id") != null) l.setDeviceId((String) properties.get("tracker_id"));
                if (properties.get("battery") != null) l.setBattery((int) properties.get("battery"));
                if (properties.get("battery_state") != null)
                    l.setBatteryState((String) properties.get("battery_state"));
                if (properties.get("velocity") != null)
                    l.setSpeed(Double.parseDouble((String) properties.get("velocity")));

                try {
                    l.setRawData(featureCollection);
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }

                return l;
            }).toList();
        }

        if (locations == null || locations.isEmpty()) {
            // TODO throws error
        }

        this.locationService.createPoints(locations);
        return "redirect:/map";
    }
}
