package com.fadhlika.lokasi.dto.jobs;

import org.jobrunr.jobs.lambdas.JobRequest;

import com.fadhlika.lokasi.jobs.ExportJobHandler;

public record ExportLocationJobRequest(int exportId) implements JobRequest {

    @Override
    public Class<ExportJobHandler> getJobRequestHandler() {
        return ExportJobHandler.class;
    }

}
