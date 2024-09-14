package com.lifestockserver.lifestock.company.repository;

import java.time.LocalDate;
import java.util.ArrayList;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.lifestockserver.lifestock.company.domain.Company;
import com.lifestockserver.lifestock.todo.domain.Todo;

@Repository
public interface CompanyRepository extends JpaRepository<Company, Long> {
  @Query("SELECT t FROM Company c JOIN c.todoList t WHERE c.id = :companyId AND t.date = :date")
  ArrayList<Todo> findTodosByCompanyIdAndDate(@Param("companyId") Long companyId, @Param("date") LocalDate date);
}