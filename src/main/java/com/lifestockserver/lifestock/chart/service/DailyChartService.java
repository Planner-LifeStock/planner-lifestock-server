package com.lifestockserver.lifestock.chart.service;

import com.lifestockserver.lifestock.chart.repository.DailyChartRepository;
import com.lifestockserver.lifestock.chart.domain.DailyChart;

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
    public void save(DailyChart dailyChart) {
        dailyChartRepository.save(dailyChart);
    }

    public int findLatestConsecutiveCompletedCountByCompanyId(Long companyId) {
        Optional<DailyChart> dailyChart = dailyChartRepository.findTop1ByCompanyIdOrderByDateDesc(companyId);
        if (dailyChart.isEmpty()) {
            return 0;
        }
        return dailyChart.get().getConsecutiveCompletedCount();
    }

    public Optional<DailyChart> findByCompanyIdAndDate(Long companyId, LocalDate date) {
        return dailyChartRepository.findByCompanyIdAndDate(companyId, date);
    }
}
