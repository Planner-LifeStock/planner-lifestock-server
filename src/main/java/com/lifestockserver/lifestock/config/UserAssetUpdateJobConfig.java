package com.lifestockserver.lifestock.config;

import com.lifestockserver.lifestock.chart.service.ChartService;
import com.lifestockserver.lifestock.rank.service.RankService;
import com.lifestockserver.lifestock.user.domain.User;
import com.lifestockserver.lifestock.user.dto.UserResponseDto;
import com.lifestockserver.lifestock.user.service.UserServiceImpl;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.List;
import java.util.stream.Collectors;

@Configuration
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

    @Bean
    public ItemProcessor<Long, Long> userItemProcessor(){
        return userId -> {
            Long totalAssets = chartService.getTotalStockPriceByUserId(userId); // 차트 서비스를 통해 유저의 총자산 반환
            if(totalAssets == null) {
                totalAssets = 0L;
            }
            rankService.updateUserAsset(userId, totalAssets);
            return userId;
        };
    }

    @Bean
    public ItemWriter<Long> userItemWriter(){
        return userIds -> {
            System.out.println("Processed" + userIds.size() + "users' assets.");
        };
    }

    @Bean
    public Step updateUserAssetStep(){
        return new StepBuilder("updateUserAssetStep", jobRepository)
                .<Long, Long>chunk(10, transactionManager)
                .reader(userItemReader())
                .processor(userItemProcessor())
                .writer(userItemWriter())
                .build();
    }

    @Bean
    public Job updateUserAssetJob(){
        return new JobBuilder("updateUserAssetJob", jobRepository)
                .start(updateUserAssetStep())
                .build();
    }
    /*
    @Bean
    public Job updateUserAssetJob(){
        return new JobBuilder("updateUserAssetJob", jobRepository)
                .start()
                .build();
    }*/
}
