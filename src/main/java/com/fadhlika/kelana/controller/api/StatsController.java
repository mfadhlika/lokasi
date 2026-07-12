package com.fadhlika.kelana.controller.api;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fadhlika.kelana.dto.Response;
import com.fadhlika.kelana.dto.Stats;
import com.fadhlika.kelana.model.User;
import com.fadhlika.kelana.service.StatsService;

@RestController
@RequestMapping("/api/v1/stats")
public class StatsController {
    private final StatsService statsService;

    StatsController(StatsService statsService) {
        this.statsService = statsService;
    }

    @GetMapping
    public Response<Stats> getUserStats() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Stats stats = statsService.getUserStats(user.getId());

        return new Response<>(stats, "success");

    }
}
