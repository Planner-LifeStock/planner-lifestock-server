package com.lifestockserver.lifestock.chart.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import com.lifestockserver.lifestock.chart.domain.Chart;

@Repository
public interface ChartRepository extends JpaRepository<Chart, Long> {
    @Query("SELECT c.high FROM Chart c WHERE c.company.id = :companyId ORDER BY c.updatedAt DESC")
    Optional<Long> findLatestHighByCompanyId(@Param("companyId") Long companyId);
}
