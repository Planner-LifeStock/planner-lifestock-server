package com.lifestockserver.lifestock.todo.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.lifestockserver.lifestock.todo.domain.Todo;

public interface TodoRepository extends JpaRepository<Todo, Long> {
  @Query("SELECT t FROM Todo t WHERE t.user.id = :userId AND t.company.id = :companyId " +
         "AND :date BETWEEN t.startDate AND t.endDate " +
         "AND (t.days IS NULL OR FUNCTION('MOD', FUNCTION('DAYOFWEEK', :date) + 5, 7) + 1 MEMBER OF t.days)")
  List<Todo> findAllByUserIdAndCompanyIdAndDate(Long userId, Long companyId, LocalDate date);
}