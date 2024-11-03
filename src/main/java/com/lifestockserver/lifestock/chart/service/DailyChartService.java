package com.lifestockserver.lifestock.chart.service;

import com.lifestockserver.lifestock.chart.repository.DailyChartRepository;
import com.lifestockserver.lifestock.chart.domain.DailyChart;
import com.lifestockserver.lifestock.company.domain.Company;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;
@Service
@Transactional(readOnly = true)
public class DailyChartService {
    private final DailyChartRepository dailyChartRepository;

    public DailyChartService(DailyChartRepository dailyChartRepository) {
        this.dailyChartRepository = dailyChartRepository;
    }

    @Transactional
    public void createDailyChart(Company company, LocalDate date, int todoCount, int completedCount) {
        boolean isDailyCompleted = todoCount >= (int)(completedCount * 7 / 10);

        DailyChart dailyChart = DailyChart.builder()
            .company(company)
            .date(date)
            .dailyCompleted(isDailyCompleted)
            .todoCount(todoCount)
            .completedCount(completedCount)
            .build();
        dailyChartRepository.save(dailyChart);
    }

    public Integer findLatestConsecutiveCompletedCountByCompanyId(Long companyId) {
        Optional<DailyChart> dailyChart = dailyChartRepository.findTop1ByCompanyIdOrderByDateDesc(companyId);
        if (dailyChart.isEmpty()) {
            return 0;
        }
        return dailyChart.get().getConsecutiveCompletedCount();
    }
}
