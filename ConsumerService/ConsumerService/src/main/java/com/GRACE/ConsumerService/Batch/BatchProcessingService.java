package com.GRACE.ConsumerService.Batch;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BatchProcessingService {

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private Job job;

    public void startBatchJob() throws Exception {
        jobLauncher.run(job, new JobParameters());
    }
}

