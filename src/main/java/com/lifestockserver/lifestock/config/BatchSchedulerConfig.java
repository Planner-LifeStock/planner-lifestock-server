package com.lifestockserver.lifestock.config;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.rmi.server.ExportException;

@Configuration
@EnableScheduling
public class BatchSchedulerConfig {
    private final JobLauncher jobLauncher;
    private final Job updateUserAssetJob;

    @Autowired
    public BatchSchedulerConfig(JobLauncher jobLauncher, Job updateUserAssetJob) {
        this.jobLauncher = jobLauncher;
        this.updateUserAssetJob = updateUserAssetJob;
    }

    @Scheduled(cron = "0 */1 * * * ?") // 매일 자정에 실행
    public void runUserAssetUpdateJob() {
        try {
            JobParameters params = new JobParametersBuilder()
                    .addLong("time", System.currentTimeMillis()).toJobParameters();
            //jobLauncher.run(updateUserAssetJob, new JobParameters());
            jobLauncher.run(updateUserAssetJob, params);
        } catch (JobExecutionAlreadyRunningException e) {
            System.err.println("Error: Job is already running.");
            e.printStackTrace();
        } catch (JobRestartException e) {
            System.err.println("Error: Job restart failed.");
            e.printStackTrace();
        } catch (JobInstanceAlreadyCompleteException e) {
            System.err.println("Error: Job instance already complete.");
            e.printStackTrace();
        } catch (JobParametersInvalidException e) {
            System.err.println("Error: Invalid job parameters.");
            e.printStackTrace();
        }
    }
}
