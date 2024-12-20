package com.lifestockserver.lifestock.chart.repository;

import java.time.LocalDate;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

import com.lifestockserver.lifestock.chart.domain.DailyChart;

public interface DailyChartRepository extends JpaRepository<DailyChart, Long> {
    Optional<DailyChart> findTop1ByCompanyIdOrderByDateDesc(Long companyId);

    Optional<DailyChart> findByCompanyIdAndDate(Long companyId, LocalDate date);
}
