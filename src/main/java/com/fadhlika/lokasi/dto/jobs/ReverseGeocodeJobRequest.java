package com.fadhlika.lokasi.dto.jobs;

import org.jobrunr.jobs.lambdas.JobRequest;

import com.fadhlika.lokasi.jobs.ReverseGeocodeJob;

public record ReverseGeocodeJobRequest() implements JobRequest {
    public static final String id = "6b4a6411-2f09-434d-9f05-6429a89103ed";

    @Override
    public Class<ReverseGeocodeJob> getJobRequestHandler() {
        return ReverseGeocodeJob.class;
    }
}
