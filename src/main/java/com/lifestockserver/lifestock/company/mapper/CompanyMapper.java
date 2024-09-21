package com.lifestockserver.lifestock.company.mapper;

import com.lifestockserver.lifestock.company.domain.Company;
import com.lifestockserver.lifestock.company.dto.CompanyCreateDto;
import com.lifestockserver.lifestock.company.dto.CompanyResponseDto;
import org.springframework.stereotype.Component;

@Component
public class CompanyMapper {

    public Company toEntity(CompanyCreateDto dto) {
        return Company.builder()
                .user(dto.getUser())
                .name(dto.getName())
                .description(dto.getDescription())
                .level(dto.getLevel())
                .leastOperatePeriod(dto.getLeastOperatePeriod())
                .investmentAmount(dto.getInvestmentAmount())
                .initialStockPrice(dto.getInitialStockPrice())
                .build();
    }

    public CompanyResponseDto toDto(Company company) {
        return CompanyResponseDto.builder()
                .name(company.getName())
                .description(company.getDescription())
                .level(company.getLevel())
                .leastOperatePeriod(company.getLeastOperatePeriod())
                .listedDate(company.getListedDate())
                .investmentAmount(company.getInvestmentAmount())
                .initialStockPrice(company.getInitialStockPrice())
                .currentStockPrice(company.getInitialStockPrice())
                .build();
    }
}