package com.fadhlika.lokasi.dto.jobs;

import org.jobrunr.jobs.lambdas.JobRequest;

import com.fadhlika.lokasi.service.ImportJobRequestHandler;

public record ImportLocationJobRequest(int importId) implements JobRequest {

    @Override
    public Class<ImportJobRequestHandler> getJobRequestHandler() {
        return ImportJobRequestHandler.class;
    }

}
