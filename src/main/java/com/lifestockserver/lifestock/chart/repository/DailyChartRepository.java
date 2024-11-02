package com.lifestockserver.lifestock.chart.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lifestockserver.lifestock.chart.domain.DailyChart;

public interface DailyChartRepository extends JpaRepository<DailyChart, Long> {
    
}
