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

  List<Company> findAllByUserId(@Param("userId") Long userId);

  @Query("SELECT c FROM Company c WHERE c.user.id = :userId AND c.listedDate IS NOT NULL")
  List<Company> findListedCompaniesByUserId(@Param("userId") Long userId);

  @Query("SELECT c FROM Company c WHERE c.user.id = :userId AND c.listedDate IS NULL")
  List<Company> findUnlistedCompaniesByUserId(@Param("userId") Long userId);
}
