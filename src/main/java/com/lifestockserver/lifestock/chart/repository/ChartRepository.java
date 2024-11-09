package com.lifestockserver.lifestock.chart.repository;

import com.lifestockserver.lifestock.chart.domain.Chart;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.time.LocalDate;
@Repository
public interface ChartRepository extends JpaRepository<Chart, Long> {
    @Query("SELECT c FROM Chart c WHERE c.company.id = :companyId " +
           "AND c.isAfterMarketOpen = true " +
           "ORDER BY c.date DESC LIMIT 1")
    Optional<Chart> findLatestAfterMarketOpenChartByCompanyId(@Param("companyId") Long companyId);

       @Query("SELECT c FROM Chart c " +
              "JOIN (SELECT c2.company.id as companyId, MAX(c2.createdAt) as latestCreatedAt " +
              "      FROM Chart c2 " +
              "      WHERE c2.company.id = :companyId AND c2.isAfterMarketOpen = true " +
              "      GROUP BY c2.date) latestDates " +
              "ON c.company.id = latestDates.companyId " +
              "AND c.createdAt = latestDates.latestCreatedAt " +
              "WHERE c.company.id = :companyId AND c.isAfterMarketOpen = true " +
              "ORDER BY c.date DESC")
       Page<Chart> findLatestAfterMarketOpenChartPageByCompanyId(@Param("companyId") Long companyId, Pageable pageable);

       @Query("SELECT c FROM Chart c " +
              "JOIN (SELECT c2.company.id as companyId, MAX(c2.createdAt) as latestCreatedAt " +
              "      FROM Chart c2 " +
              "      WHERE c2.company.id = :companyId AND c2.isAfterMarketOpen = true " +
              "      AND FUNCTION('YEAR', c2.date) = :year " +
              "      AND FUNCTION('MONTH', c2.date) = :month " +
              "      GROUP BY c2.date) latestDates " +
              "ON c.company.id = latestDates.companyId " +
              "AND c.createdAt = latestDates.latestCreatedAt " +
              "WHERE c.company.id = :companyId AND c.isAfterMarketOpen = true " +
              "ORDER BY c.date DESC")
       List<Chart> findLatestAfterMarketOpenChartListByCompanyIdAndYearMonth(
       @Param("companyId") Long companyId,
       @Param("year") int year,
       @Param("month") int month
       );

       @Query("SELECT c FROM Chart c " +
       "JOIN (SELECT c2.company.id as companyId, MAX(c2.date) as maxDate " +
       "      FROM Chart c2 " +
       "      WHERE c2.user.id = :userId " +
       "      GROUP BY c2.company.id, FUNCTION('DATE', c2.date)) latestDates " +
       "ON c.company.id = latestDates.companyId " +
       "AND c.date = latestDates.maxDate " +
       "WHERE c.user.id = :userId " +
       "ORDER BY c.company.id, c.date DESC")
       List<Chart> findLatestChartsByUserIdGroupedByCompany(@Param("userId") Long userId);

       @Query("SELECT c FROM Chart c WHERE c.company.id = :companyId ORDER BY c.date DESC LIMIT 1")
       Chart findLatestByCompanyId(@Param("companyId") Long companyId);

       int countByCompanyId(Long companyId);

       @Query("SELECT COUNT(c) FROM Chart c WHERE c.company.id = :companyId AND c.date = :date AND c.isAfterMarketOpen = true")
       int countCompletedByCompanyIdAndDate(Long companyId, LocalDate date);

       @Query("SELECT SUM((c.close - c.company.initialStockPrice) * c.company.initialStockQuantity) FROM Chart c " +
              "WHERE c.user.id = :userId " +
              "AND c.date = (SELECT MAX(c2.date) FROM Chart c2 WHERE c2.company.id = c.company.id AND c2.user.id = :userId)")
       Long getTotalStockPriceByUserId(Long userId);

}
