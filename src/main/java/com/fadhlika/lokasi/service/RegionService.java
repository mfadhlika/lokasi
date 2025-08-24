package com.fadhlika.lokasi.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fadhlika.lokasi.model.Region;
import com.fadhlika.lokasi.repository.RegionRepository;

@Service
public class RegionService {
    @Autowired
    private RegionRepository regionRepository;

    public void createRegion(Region region) {
        regionRepository.createRegion(region);
    }
}
