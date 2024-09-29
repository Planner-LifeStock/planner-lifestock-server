package com.lifestockserver.lifestock.chart.mapper;

import com.lifestockserver.lifestock.chart.dto.ChartResponseDto;
import com.lifestockserver.lifestock.chart.dto.CompanyChartPageReponseDto;
import com.lifestockserver.lifestock.chart.dto.CompanyMonthlyChartListResponseDto;
import com.lifestockserver.lifestock.chart.dto.CompanyCurrentPriceResponseDto;
import com.lifestockserver.lifestock.chart.dto.UserCurrentPriceListResponseDto;
import com.lifestockserver.lifestock.chart.dto.ChartCreateDto;
import com.lifestockserver.lifestock.chart.domain.Chart;
import com.lifestockserver.lifestock.user.domain.User;
import com.lifestockserver.lifestock.company.domain.Company;
import com.lifestockserver.lifestock.todo.domain.Todo;

import java.util.List;
import org.springframework.data.domain.Page;

public interface ChartMapper {
  Chart toChart(ChartCreateDto chartCreateDto, User user, Company company, Todo todo);

  ChartResponseDto toChartResponseDto(Chart chart);

  List<ChartResponseDto> toChartResponseDtoList(List<Chart> chartList);

  CompanyChartPageReponseDto toCompanyChartPageReponseDto(Page<Chart> chartPage);

  CompanyMonthlyChartListResponseDto toCompanyMonthlyChartListResponseDto(List<Chart> chartList);

  CompanyCurrentPriceResponseDto toCompanyCurrentPriceResponseDto(Chart chart);

  UserCurrentPriceListResponseDto toUserCurrentPriceListResponseDto(List<Chart> chartList);
}
