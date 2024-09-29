package com.lifestockserver.lifestock.chart.repository;

import com.lifestockserver.lifestock.chart.domain.Chart;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChartRepository extends JpaRepository<Chart, Long> {
    @Query("SELECT c FROM Chart c WHERE c.company.id = :companyId " +
           "AND c.isAfterMarketOpen = true " +
           "ORDER BY c.date DESC")
    Chart findLatestAfterMarketOpenChartByCompanyId(@Param("companyId") Long companyId);

    @Query("SELECT c FROM Chart c WHERE c.company.id = :companyId " +
           "AND (c.date, c.isAfterMarketOpen) IN " +
           "(SELECT FUNCTION('DATE', c2.date), c2.isAfterMarketOpen " +
           "FROM Chart c2 " +
           "WHERE c2.company.id = :companyId " +
           "AND c2.isAfterMarketOpen = true " +
           "GROUP BY FUNCTION('DATE', c2.date), c2.isAfterMarketOpen " +
           "HAVING c2.date = MAX(c2.date)) " +
           "ORDER BY c.date DESC")
    Page<Chart> findLatestAfterMarketOpenChartPageByCompanyId(@Param("companyId") Long companyId, Pageable pageable);

    @Query("SELECT c FROM Chart c WHERE c.company.id = :companyId " +
    "AND FUNCTION('YEAR', c.date) = :year " +
    "AND FUNCTION('MONTH', c.date) = :month " +
    "AND (c.date, c.isAfterMarketOpen) IN " +
    "(SELECT FUNCTION('DATE', c2.date), c2.isAfterMarketOpen " +
    "FROM Chart c2 " +
    "WHERE c2.company.id = :companyId " +
    "AND FUNCTION('YEAR', c2.date) = :year " +
    "AND FUNCTION('MONTH', c2.date) = :month " +
    "AND c2.isAfterMarketOpen = true " +
    "GROUP BY FUNCTION('DATE', c2.date), c2.isAfterMarketOpen " +
    "HAVING c2.date = MAX(c2.date)) " +
    "ORDER BY c.date DESC")
    List<Chart> findLatestAfterMarketOpenChartListByCompanyIdAndYearMonth(
        @Param("companyId") Long companyId,
        @Param("year") int year,
        @Param("month") int month
    );

    @Query("SELECT c FROM Chart c WHERE c.id IN " +
           "(SELECT MAX(c2.id) FROM Chart c2 " +
           "WHERE c2.user.id = :userId " +
           "GROUP BY c2.company.id, FUNCTION('DATE', c2.date)) " +
           "ORDER BY c.company.id, c.date DESC")
    List<Chart> findLatestChartsByUserIdGroupedByCompany(@Param("userId") Long userId);
}
