package com.lifestockserver.lifestock.chart.mapper;

import com.lifestockserver.lifestock.chart.dto.ChartResponseDto;
import com.lifestockserver.lifestock.chart.dto.CompanyChartPageReponseDto;
import com.lifestockserver.lifestock.chart.dto.CompanyMonthlyChartListResponseDto;
import com.lifestockserver.lifestock.chart.dto.UserCurrentPriceListResponseDto;
import com.lifestockserver.lifestock.chart.dto.CompanyCurrentPriceResponseDto;
import com.lifestockserver.lifestock.chart.dto.ChartCreateDto;
import com.lifestockserver.lifestock.chart.domain.Chart;
import com.lifestockserver.lifestock.user.domain.User;
import com.lifestockserver.lifestock.company.domain.Company;
import com.lifestockserver.lifestock.todo.domain.Todo;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.ArrayList;
import java.time.LocalDate;

@Component
public class ChartMapperImpl implements ChartMapper {
  @Override
  public Chart toChart(ChartCreateDto chartCreateDto, User user, Company company, Todo todo) {
    Chart chart = Chart.builder()
      .user(user)
      .company(company)
      .todo(todo)
      .open(chartCreateDto.getOpen())
      .high(chartCreateDto.getHigh())
      .low(chartCreateDto.getLow())
      .close(chartCreateDto.getClose())
      .date(chartCreateDto.getDate())
      .build();

    return chart;
  }

  @Override
  public ChartResponseDto toChartResponseDto(Chart chart) {
    ChartResponseDto chartResponseDto = ChartResponseDto.builder()
      .high(chart.getHigh())
      .low(chart.getLow())
      .open(chart.getOpen())
      .close(chart.getClose())
      .date(chart.getDate())
      .changeRate((double) (chart.getClose() - chart.getOpen()) / chart.getOpen() * 100)
      .build();
    return chartResponseDto;
  }

  @Override
  public List<ChartResponseDto> toChartResponseDtoList(List<Chart> chartList) {
    List<ChartResponseDto> chartResponseDtoList = new ArrayList<>(chartList.size());
    for (Chart chart : chartList) {
      chartResponseDtoList.add(toChartResponseDto(chart));
    }
    return chartResponseDtoList;
  }

  @Override
  public CompanyChartPageReponseDto toCompanyChartPageReponseDto(Page<Chart> chartPage) {
    List<ChartResponseDto> chartResponseDtos = toChartResponseDtoList(chartPage.getContent());

    return CompanyChartPageReponseDto.builder()
      .companyId(chartPage.getContent().get(0).getCompany().getId())
      .chartList(chartResponseDtos)
      .totalPages(chartPage.getTotalPages())
      .totalElements(chartPage.getTotalElements())
      .pageSize(chartPage.getSize())
      .pageNumber(chartPage.getNumber())
      .hasNext(chartPage.hasNext())
      .hasPrevious(chartPage.hasPrevious())
      .build();
  }

  @Override
  public CompanyMonthlyChartListResponseDto toCompanyMonthlyChartListResponseDto(List<Chart> chartList) {
    if (chartList.isEmpty()) {
      return null;
    }
    LocalDate date = chartList.get(0).getDate();
    int year = date.getYear();
    int month = date.getMonthValue();

    return CompanyMonthlyChartListResponseDto.builder()
      .companyId(chartList.get(0).getCompany().getId())
      .monthlyChartList(toChartResponseDtoList(chartList))
      .year(year)
      .month(month)
      .build();
  }

  @Override
  public CompanyCurrentPriceResponseDto toCompanyCurrentPriceResponseDto(Chart chart) {
    return CompanyCurrentPriceResponseDto.builder()
      .companyId(chart.getCompany().getId())
      .currentPrice(chart.getClose())
      .changeRate((double) (chart.getClose() - chart.getOpen()) / chart.getOpen() * 100)
      .build();
  }

  @Override
  public UserCurrentPriceListResponseDto toUserCurrentPriceListResponseDto(List<Chart> chartList) {

    double totalChangeRate = 0;
    Long totalCurrentPrice = 0L;
    List<CompanyCurrentPriceResponseDto> companyCurrentPriceList = new ArrayList<>(chartList.size());
    for (Chart chart : chartList) {
      CompanyCurrentPriceResponseDto companyCurrentPrice = toCompanyCurrentPriceResponseDto(chart);
      companyCurrentPriceList.add(companyCurrentPrice);
      totalChangeRate += companyCurrentPrice.getChangeRate();
      totalCurrentPrice += companyCurrentPrice.getCurrentPrice();
    }

    for (CompanyCurrentPriceResponseDto companyCurrentPrice : companyCurrentPriceList) {
      totalChangeRate += companyCurrentPrice.getChangeRate();
      totalCurrentPrice += companyCurrentPrice.getCurrentPrice();
    }

    return UserCurrentPriceListResponseDto.builder()
      .companyCurrentPriceList(companyCurrentPriceList)
      .totalChangeRate(totalChangeRate)
      .totalCurrentPrice(totalCurrentPrice)
      .build();
  }
}
