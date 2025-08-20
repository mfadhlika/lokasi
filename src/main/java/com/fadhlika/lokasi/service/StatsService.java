package com.fadhlika.lokasi.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fadhlika.lokasi.dto.Stats;
import com.fadhlika.lokasi.repository.StatsRepository;

@Service
public class StatsService {
    @Autowired
    private StatsRepository statsRepository;

    public Stats getUserStats(int userId) {
        return statsRepository.getStats(userId);
    }
}
