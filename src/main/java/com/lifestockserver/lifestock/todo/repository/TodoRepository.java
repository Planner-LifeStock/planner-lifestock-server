package com.lifestockserver.lifestock.todo.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.lifestockserver.lifestock.todo.domain.Todo;

@Repository
public interface TodoRepository extends JpaRepository<Todo, Long> {
  // 해당 날짜에 포함되는 삭제되지 않은 모든 todo 반환
  @Query("SELECT t FROM Todo t WHERE t.user.id = :userId AND t.company.id = :companyId " +
         "AND :date BETWEEN t.startDate AND t.endDate " +
         "AND t.deletedAt IS NULL")
  List<Todo> findAllByUserIdAndCompanyIdAndDate(Long userId, Long companyId, LocalDate date);
  
  // 해당 달에 포함되는 삭제되지 않은 모든 todo 반환
  @Query("SELECT t FROM Todo t WHERE t.user.id = :userId AND t.company.id = :companyId " +
         "AND ((FUNCTION('YEAR', t.startDate) = FUNCTION('YEAR', :date) AND FUNCTION('MONTH', t.startDate) = FUNCTION('MONTH', :date)) " +
         "OR (FUNCTION('YEAR', t.endDate) = FUNCTION('YEAR', :date) AND FUNCTION('MONTH', t.endDate) = FUNCTION('MONTH', :date)) " +
         "OR (t.startDate <= :date AND t.endDate >= FUNCTION('LAST_DAY', :date))) " +
         "AND t.deletedAt IS NULL")
  List<Todo> findAllByUserIdAndCompanyIdAndMonth(Long userId, Long companyId, LocalDate date);
  
  // 해당 id의 todo 반환
  Optional<Todo> findByIdAndDeletedAtIsNull(Long id);

  // 해당 날짜 이후의 모든 todo 반환
  @Query("SELECT t FROM Todo t " + 
         "WHERE t.company.id = :companyId " + 
         "AND t.endDate >= :date " + 
         "AND t.done = false " + 
         "AND t.deletedAt IS NULL")
  List<Todo> findAllByCompanyIdAndDateAfter(Long companyId, LocalDate date);

  // 해당 날짜의 모든 todo 개수 반환
  @Query("SELECT COUNT(t) FROM Todo t " + 
         "WHERE t.company.id = :companyId " + 
         "AND t.endDate = :date " + 
         "AND t.deletedAt IS NULL")
  int countByCompanyIdAndDate(Long companyId, LocalDate date);

  // 해당 날짜의 모든 완료된 todo 개수 반환
  @Query("SELECT COUNT(t) FROM Todo t " + 
         "WHERE t.company.id = :companyId " + 
         "AND t.endDate = :date " + 
         "AND t.completed = true " + 
         "AND t.deletedAt IS NULL")
  int countCompletedByCompanyIdAndDate(Long companyId, LocalDate date);

  List<Todo> findAllByCompanyIdAndDoneFalseAndEndDateLessThanEqual(Long companyId, LocalDate date);
}
