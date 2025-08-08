package com.fadhlika.lokasi.dto.jobs;

import org.jobrunr.jobs.lambdas.JobRequest;

import com.fadhlika.lokasi.jobs.ReverseGeocodeJobHandler;

public record ReverseGeocodeJobRequest(int locationId) implements JobRequest {
    @Override
    public Class<ReverseGeocodeJobHandler> getJobRequestHandler() {
        return ReverseGeocodeJobHandler.class;
    }
}
