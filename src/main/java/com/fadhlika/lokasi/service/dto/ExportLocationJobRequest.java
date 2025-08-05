package com.fadhlika.lokasi.service.dto;

import org.jobrunr.jobs.lambdas.JobRequest;

import com.fadhlika.lokasi.service.ExportJobRequestHandler;

public record ExportLocationJobRequest(int exportId) implements JobRequest {

    @Override
    public Class<ExportJobRequestHandler> getJobRequestHandler() {
        return ExportJobRequestHandler.class;
    }

}
