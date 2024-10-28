package com.lifestockserver.lifestock.company.mapper;

import com.lifestockserver.lifestock.company.domain.Company;
import com.lifestockserver.lifestock.company.dto.CompanyCreateDto;
import com.lifestockserver.lifestock.company.dto.CompanyResponseDto;
import com.lifestockserver.lifestock.file.dto.FileResponseDto;
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
                .initialStockQuantity(dto.getInitialStockQuantity())
                // .logo(dto.getLogo()) logo는 따로 설정해야한다.
                .build();
    }

    public CompanyResponseDto toDto(Company company) {
        return CompanyResponseDto.builder()
                .id(company.getId())
                .name(company.getName())
                .description(company.getDescription())
                .level(company.getLevel())
                .leastOperatePeriod(company.getLeastOperatePeriod())
                .listedDate(company.getListedDate())
                .investmentAmount(company.getInvestmentAmount())
                .initialStockPrice(company.getInitialStockPrice())
                .currentStockPrice(company.getInitialStockPrice())
                .logo(FileResponseDto.builder()
                    .originalName(company.getLogo().getOriginalName())
                    .mimeType(company.getLogo().getMimeType())
                    .size(company.getLogo().getSize())
                    .meta(company.getLogo().getMeta())
                    .url(company.getLogo().getUrl())
                    .build())
                .build();
    }
}