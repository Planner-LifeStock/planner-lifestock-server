package com.lifestockserver.lifestock.rank.batch;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class ManualBatchRunner {

    private final JobLauncher jobLauncher;
    private final Job updateUserAssetJob;

    @Autowired
    public ManualBatchRunner(JobLauncher jobLauncher, Job updateUserAssetJob) {
        this.jobLauncher = jobLauncher;
        this.updateUserAssetJob = updateUserAssetJob;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void runJob(){
        try{
            JobExecution execution = jobLauncher.run(updateUserAssetJob, new JobParameters());
            System.out.println("Job status : " + execution.getStatus());
            System.out.println("Job completed");
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
