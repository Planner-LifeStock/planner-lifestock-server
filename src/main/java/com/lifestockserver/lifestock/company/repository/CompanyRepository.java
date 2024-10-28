package com.lifestockserver.lifestock.company.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.lang.NonNull;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.lifestockserver.lifestock.company.domain.Company;
import com.lifestockserver.lifestock.todo.domain.Todo;

@Repository
public interface CompanyRepository extends JpaRepository<Company, Long> {
  @NonNull Optional<Company> findById(@NonNull Long id);

  @Query("SELECT t FROM Company c JOIN c.todos t WHERE c.id = :companyId AND :date BETWEEN t.startDate AND t.endDate")
  List<Todo> findTodosByCompanyIdAndDate(@Param("companyId") Long companyId, @Param("date") LocalDate date);

  List<Company> findAllByUserId(@Param("userId") Long userId);
}