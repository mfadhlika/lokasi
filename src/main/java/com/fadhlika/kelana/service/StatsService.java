package com.fadhlika.kelana.service;

import org.springframework.stereotype.Service;

import com.fadhlika.kelana.dto.Stats;
import com.fadhlika.kelana.repository.StatsRepository;

@Service
public class StatsService {
    private final StatsRepository statsRepository;

    StatsService(StatsRepository statsRepository) {
        this.statsRepository = statsRepository;
    }

    public Stats getUserStats(int userId) {
        return statsRepository.getStats(userId);
    }
}
