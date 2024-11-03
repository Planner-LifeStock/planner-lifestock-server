package com.lifestockserver.lifestock.company.mapper;

import com.lifestockserver.lifestock.company.domain.Company;
import com.lifestockserver.lifestock.company.dto.CompanyCreateDto;
import com.lifestockserver.lifestock.company.dto.CompanyResponseDto;
import com.lifestockserver.lifestock.file.dto.FileResponseDto;
import com.lifestockserver.lifestock.file.domain.File;
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
                .initialStockPrice(dto.getInitialStockPrice())
                .initialStockQuantity(dto.getInitialStockQuantity())
                // .logo(dto.getLogo()) logo는 따로 설정해야한다.
                .build();
    }

    public CompanyResponseDto toDto(Company company) {
        File logo = company.getLogo();

        return CompanyResponseDto.builder()
                .id(company.getId())
                .name(company.getName())
                .description(company.getDescription())
                .level(company.getLevel())
                .leastOperatePeriod(company.getLeastOperatePeriod())
                .listedDate(company.getListedDate())
                .investmentAmount(company.getInvestmentAmount())
                .initialStockPrice(company.getInitialStockPrice())
                .listedStockPrice(company.getListedStockPrice())
                .logo(FileResponseDto.builder()
                    .originalName(logo == null ? null : logo.getOriginalName())
                    .mimeType(logo == null ? null : logo.getMimeType())
                    .size(logo == null ? null : logo.getSize())
                    .meta(logo == null ? null : logo.getMeta())
                    .url(logo == null ? null : logo.getUrl())
                    .build())
                .build();
    }
}