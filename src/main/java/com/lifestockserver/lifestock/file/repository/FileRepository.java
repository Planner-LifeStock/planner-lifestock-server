package com.lifestockserver.lifestock.file.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.lifestockserver.lifestock.file.domain.File;

@Repository
public interface FileRepository extends JpaRepository<File, String> {
  
}