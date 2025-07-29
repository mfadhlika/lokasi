package com.fadhlika.lokasi.config;

import org.jobrunr.configuration.JobRunr;
import org.jobrunr.scheduling.JobScheduler;
import org.jobrunr.server.JobActivator;
import org.jobrunr.storage.sql.common.SqlStorageProviderFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.sqlite.SQLiteDataSource;

@Configuration
public class JobRunrConfig {
    @Value("${lokasi.data_dir}")
    private String dataDir;

    @Bean
    public JobScheduler initJobRunr(JobActivator jobActivator) {
        SQLiteDataSource ds = new SQLiteDataSource();
        ds.setUrl(String.format("jdbc:sqlite:%s/jobrunr.db", dataDir));

        return JobRunr.configure()
                .useJobActivator(jobActivator)
                .useStorageProvider(SqlStorageProviderFactory
                        .using(ds))
                .useBackgroundJobServer()
                .useDashboard()
                .initialize()
                .getJobScheduler();
    }
}
