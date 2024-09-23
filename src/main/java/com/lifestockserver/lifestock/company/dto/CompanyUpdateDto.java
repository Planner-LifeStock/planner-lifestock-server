package com.lifestockserver.lifestock.company.dto;

import java.time.LocalDate;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CompanyUpdateDto extends CompanyCreateDto {
  private LocalDate listedDate;
}
