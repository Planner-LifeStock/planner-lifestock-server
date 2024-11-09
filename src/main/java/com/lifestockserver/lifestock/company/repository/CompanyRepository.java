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

  // 모든 회사 조회 (상장 여부와 관계없이)
  @Query("SELECT c FROM Company c WHERE c.user.id = :userId")
  List<Company> findAllByUserId(@Param("userId") Long userId);

  // 상장된 회사만 조회 (listedDate가 NULL이 아닌 회사들)
  @Query("SELECT c FROM Company c WHERE c.user.id = :userId AND c.listedDate IS NOT NULL")
  List<Company> findListedCompaniesByUserId(@Param("userId") Long userId);

  // 미상장된 회사만 조회 (listedDate가 NULL인 회사들)
  @Query("SELECT c FROM Company c WHERE c.user.id = :userId AND c.listedDate IS NULL")
  List<Company> findUnlistedCompaniesByUserId(@Param("userId") Long userId);

  // 전체 미상장 회사 조회 (상관없이 미상장 회사만)
  @Query("SELECT c FROM Company c WHERE c.listedDate IS NULL")
  List<Company> findAllUnlisted();
}
