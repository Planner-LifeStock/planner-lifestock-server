package com.lifestockserver.lifestock.chart.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.time.LocalDate;
import java.util.List;
import com.lifestockserver.lifestock.chart.domain.Chart;
import com.lifestockserver.lifestock.chart.dto.ChartResponseDto;

@Repository
public interface ChartRepository extends JpaRepository<Chart, Long> {
    @Query("SELECT c FROM Chart c WHERE c.company.id = :companyId AND DATE(c.createdAt) = :date ORDER BY c.createdAt DESC LIMIT 1")
    Optional<Chart> findLatestChartByCompanyIdAndDate(@Param("companyId") Long companyId, @Param("date") LocalDate date);

    @Query("SELECT c FROM Chart c WHERE c.company.id = :companyId ORDER BY c.createdAt DESC LIMIT 1")
    Optional<Chart> findLatestChartByCompanyId(@Param("companyId") Long companyId);
    
    @Query("SELECT c FROM Chart c " +
           "WHERE c.company.id = :companyId " +
           "AND FUNCTION('YEAR', c.createdAt) = FUNCTION('YEAR', :date) " +
           "AND FUNCTION('MONTH', c.createdAt) = FUNCTION('MONTH', :date) " +
           "AND c.id IN (SELECT MAX(c2.id) FROM Chart c2 " +
           "             WHERE c2.company.id = :companyId " +
           "             AND FUNCTION('YEAR', c2.createdAt) = FUNCTION('YEAR', :date) " +
           "             AND FUNCTION('MONTH', c2.createdAt) = FUNCTION('MONTH', :date) " +
           "             GROUP BY FUNCTION('DATE', c2.createdAt))")
    List<Chart> findLatestChartsForCompanyByMonth(@Param("companyId") Long companyId, @Param("date") LocalDate date);

    @Query("SELECT c FROM Chart c " +
           "WHERE c.company.id = :companyId " +
           "AND c.id IN (SELECT MAX(c2.id) FROM Chart c2 " +
           "             WHERE c2.company.id = :companyId " +
           "             GROUP BY FUNCTION('DATE', c2.createdAt))")
    Page<Chart> findLatestChartsPageByCompanyId(@Param("companyId") Long companyId, Pageable pageable);

    Chart findByTodoId(Long todoId);

    @Query("SELECT COUNT(c) FROM Chart c WHERE c.company.id = :companyId")
    Long countChartsByCompanyId(@Param("companyId") Long companyId);

//     @Query("SELECT new com.lifestockserver.lifestock.chart.dto.ChartResponseDto(c.id, c.user.id, c.company.id, c.todo.id, c.open, c.high, c.low, c.close) " +
//            "FROM Chart c WHERE c.company.id = :companyId ORDER BY c.createdAt DESC")
    @Query("SELECT c.id, c.user.id, c.company.id, c.todo.id, c.open, c.high, c.low, c.close FROM Chart c WHERE c.company.id = :companyId")
    List<?> findAllChartResponseDtosByCompanyId(@Param("companyId") Long companyId);

    @Query("SELECT c FROM Chart c " +
           "WHERE c.company.id IN (" +
           "    SELECT co.id FROM Company co " +
           "    JOIN co.user u " +
           "    WHERE u.id = :userId AND co.listedDate IS NULL" +
           ") AND c.id IN (" +
           "    SELECT MAX(c2.id) FROM Chart c2 " +
           "    GROUP BY c2.company.id" +
           ")")
    List<Chart> findLatestChartsForUnlistedCompaniesByUserId(@Param("userId") Long userId);

    @Query("SELECT c FROM Chart c " +
           "WHERE c.company.id = :companyId " +
           "AND c.id IN (SELECT MAX(c2.id) FROM Chart c2 " +
           "             WHERE c2.company.id = :companyId " +
           "             GROUP BY FUNCTION('DATE', c2.createdAt))")
    Page<Chart> findLatestDailyChartsByCompanyId(@Param("companyId") Long companyId, Pageable pageable);

    Chart findTopByCompany_IdOrderByCreatedAtDesc(Long companyId);

}
