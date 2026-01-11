package com.fadhlika.lokasi.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import com.fadhlika.lokasi.exception.InternalErrorException;
import com.fadhlika.lokasi.model.Region;
import com.fadhlika.lokasi.repository.RegionRepository;
import com.fasterxml.jackson.core.JsonProcessingException;

@Service
public class RegionService {
    @Autowired
    private RegionRepository regionRepository;

    public void createRegion(Region region) {
        try {
            regionRepository.createRegion(region);
        } catch (DataAccessException | JsonProcessingException e) {
            throw new InternalErrorException(e.getMessage());
        }
    }

    public List<Region> fetchRegions(int userId) {
        return regionRepository.fetchRegions(userId);
    }
}
