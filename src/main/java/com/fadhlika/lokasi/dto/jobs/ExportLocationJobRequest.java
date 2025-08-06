package com.fadhlika.lokasi.dto.jobs;

import org.jobrunr.jobs.lambdas.JobRequest;

import com.fadhlika.lokasi.service.ExportJobRequestHandler;

public record ExportLocationJobRequest(int exportId) implements JobRequest {

    @Override
    public Class<ExportJobRequestHandler> getJobRequestHandler() {
        return ExportJobRequestHandler.class;
    }

}
