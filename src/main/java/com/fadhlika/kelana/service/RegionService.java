package com.fadhlika.kelana.service;

import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import com.fadhlika.kelana.exception.InternalErrorException;
import com.fadhlika.kelana.model.Region;
import com.fadhlika.kelana.repository.RegionRepository;
import com.fasterxml.jackson.core.JsonProcessingException;

@Service
public class RegionService {
    private final RegionRepository regionRepository;

    RegionService(RegionRepository regionRepository) {
        this.regionRepository = regionRepository;
    }

    public void createRegions(List<Region> regions) {
        try {
            regionRepository.createRegions(regions);
        } catch (DataAccessException e) {
            throw new InternalErrorException(e.getMessage());
        } catch (JsonProcessingException e) {
            throw new InternalErrorException(String.format("failed to serialize geocoding: %s", e.getMessage()));
        }
    }

    public List<Region> fetchRegions(int userId) {
        try {
            return regionRepository.fetchRegions(userId);
        } catch (DataAccessException e) {
            throw new InternalErrorException(e.getMessage());
        }
    }
}
