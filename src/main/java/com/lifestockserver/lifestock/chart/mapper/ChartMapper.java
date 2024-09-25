package com.lifestockserver.lifestock.chart.mapper;

import org.mapstruct.Mapper;
import org.springframework.data.domain.Page;
import java.util.List;
import java.util.stream.Collectors;
import java.time.LocalDate;
// import org.springframework.data.domain.PageImpl;

import org.mapstruct.Mapping;

import com.lifestockserver.lifestock.chart.dto.ChartResponseDto;
import com.lifestockserver.lifestock.chart.domain.Chart;
import com.lifestockserver.lifestock.chart.dto.CompanyChartElementResponseDto;
import com.lifestockserver.lifestock.chart.dto.UserChartElementResponseDto;
import com.lifestockserver.lifestock.chart.dto.CompanyChartPageResponseDto;
import com.lifestockserver.lifestock.chart.dto.CompanyChartMonthlyListResponseDto;

@Mapper(componentModel = "spring")
public interface ChartMapper {
  @Mapping(source = "company.id", target = "companyId")
  @Mapping(source = "user.id", target = "userId")
  @Mapping(source = "todo.id", target = "todoId")
  ChartResponseDto toChartResponseDto(Chart chart);

  @Mapping(source = "company.id", target = "companyId")
  @Mapping(source = "user.id", target = "userId")
  @Mapping(source = "todo.id", target = "todoId")
  List<ChartResponseDto> toChartResponseDtoList(List<Chart> chartList);

  CompanyChartElementResponseDto toCompanyChartElementResponseDto(Chart chart);

  List<CompanyChartElementResponseDto> toCompanyChartElementResponseDtoList(List<Chart> chartList);

  @Mapping(source = "company.id", target = "companyId")
  UserChartElementResponseDto toUserChartElementResponseDto(Chart chart);

  List<UserChartElementResponseDto> toUserChartElementResponseDtoList(List<Chart> chartList);

  // @Mapping(target = "pageable", source = "pageable")
  // @Mapping(target = "totalElements", source = "totalElements")
  // PageImpl<ChartResponseDto> toChartResponseDtoPage(Page<Chart> chartPage);

  default List<CompanyChartElementResponseDto> toCompanyChartElementResponseDtoList(Page<Chart> chartList) {
    return chartList.getContent().stream()
      .map(this::toCompanyChartElementResponseDto)
      .collect(Collectors.toList());
  }

  @Mapping(source = "content", target = "chartList")
  @Mapping(source = "number", target = "pageNumber")
  @Mapping(source = "size", target = "pageSize")
  CompanyChartPageResponseDto toCompanyChartPageResponseDto(Page<Chart> chartPage);

  CompanyChartMonthlyListResponseDto toCompanyChartMonthlyListResponseDto(List<Chart> chartList, LocalDate month);

  default LocalDate mapMonthToLocalDate(java.time.Month month) {
      return LocalDate.of(LocalDate.now().getYear(), month, 1);
  }
}
