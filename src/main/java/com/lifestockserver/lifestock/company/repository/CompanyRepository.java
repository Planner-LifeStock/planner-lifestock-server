package com.lifestockserver.lifestock.company.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.lang.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.lifestockserver.lifestock.company.domain.Company;

@Repository
public interface CompanyRepository extends JpaRepository<Company, Long> {

  @NonNull Optional<Company> findById(@NonNull Long id);

  // 삭제되지 않은 모든 회사
  @Query("SELECT c FROM Company c WHERE c.user.id = :userId AND c.deletedAt IS NULL")
  List<Company> findAllByUserId(@Param("userId") Long userId);

  // 상장된 회사만 조회 (삭제된 회사)
  @Query("SELECT c FROM Company c WHERE c.user.id = :userId AND c.listedDate IS NOT NULL AND c.deletedAt IS NOT NULL")
  List<Company> findListedCompaniesByUserId(@Param("userId") Long userId);

  // 미상장된 회사만 조회 (삭제되지 않은 회사)
  @Query("SELECT c FROM Company c WHERE c.user.id = :userId AND c.listedDate IS NULL AND c.deletedAt IS NULL")
  List<Company> findUnlistedCompaniesByUserId(@Param("userId") Long userId);

  // 전체에서 미상장 상태인 회사 조회 (삭제되지 않은 것들)
  @Query("SELECT c FROM Company c WHERE c.listedDate IS NULL AND c.deletedAt IS NULL")
  List<Company> findAllUnlisted();
}
