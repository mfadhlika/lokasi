package com.fadhlika.lokasi.controller.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fadhlika.lokasi.dto.Response;
import com.fadhlika.lokasi.dto.Stats;
import com.fadhlika.lokasi.model.User;
import com.fadhlika.lokasi.service.StatsService;

@RestController
@RequestMapping("/api/v1/stats")
public class StatsController {
    @Autowired
    private StatsService statsService;

    @GetMapping
    public Response<Stats> getUserStats() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Stats stats = statsService.getUserStats(user.getId());

        return new Response<>(stats, "success");

    }
}
