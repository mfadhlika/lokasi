package com.fadhlika.lokasi.dto.jobs;

import org.jobrunr.jobs.lambdas.JobRequest;

import com.fadhlika.lokasi.service.ImportJobHandler;

public record ImportLocationJobRequest(int importId) implements JobRequest {

    @Override
    public Class<ImportJobHandler> getJobRequestHandler() {
        return ImportJobHandler.class;
    }

}
