package com.lifestockserver.lifestock.config;

import com.lifestockserver.lifestock.chart.service.ChartService;
import com.lifestockserver.lifestock.rank.service.RankService;
import com.lifestockserver.lifestock.user.domain.User;
import com.lifestockserver.lifestock.user.dto.UserResponseDto;
import com.lifestockserver.lifestock.user.service.UserServiceImpl;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.List;
import java.util.stream.Collectors;

@Configuration
@EnableBatchProcessing
public class UserAssetUpdateJobConfig {

    private final RankService rankService;
    private final UserServiceImpl userService;
    private final ChartService chartService;
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    public UserAssetUpdateJobConfig(RankService rankService, UserServiceImpl userService, ChartService chartService, JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        this.rankService = rankService;
        this.userService = userService;
        this.chartService = chartService;
        this.jobRepository = jobRepository;
        this.transactionManager = transactionManager;
    }

    @Bean
    public ListItemReader<Long> userItemReader(){
        List<Long> userIds = userService.findAllUsers()
                .stream()
                .map(UserResponseDto::getId)
                .collect(Collectors.toList());

        return new ListItemReader<>(userIds);
    }


    /*
    @Bean
    public Job updateUserAssetJob(){
        return new JobBuilder("updateUserAssetJob", jobRepository)
                .start()
                .build();
    }*/
}
