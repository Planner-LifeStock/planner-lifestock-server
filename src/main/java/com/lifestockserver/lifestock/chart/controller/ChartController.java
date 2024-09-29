package com.lifestockserver.lifestock.chart.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.lifestockserver.lifestock.chart.dto.CompanyChartPageReponseDto;
import com.lifestockserver.lifestock.chart.dto.CompanyMonthlyChartListResponseDto;
import com.lifestockserver.lifestock.chart.dto.UserCurrentPriceListResponseDto;
import com.lifestockserver.lifestock.chart.service.ChartService;
import com.lifestockserver.lifestock.chart.dto.ChartCreateDto;
import com.lifestockserver.lifestock.chart.dto.ChartResponseDto;


@RestController
public class ChartController {

  @Autowired
  private ChartService chartService;

  public ChartController(ChartService chartService) {
    this.chartService = chartService;
  }

  @PostMapping("chart")
  public ResponseEntity<?> createChart(@RequestBody ChartCreateDto chartCreateDto) {
    ChartResponseDto chartResponseDto = chartService.createChart(chartCreateDto);
    return ResponseEntity.ok(chartResponseDto);
  }

  @GetMapping("/company/{companyId}/charts")
  public ResponseEntity<?> getCompanyCharts(@PathVariable Long companyId,
    @RequestParam(required = false) Integer year,
    @RequestParam(required = false) Integer month,
    @RequestParam(required = false, defaultValue = "0") Integer page,
    @RequestParam(required = false, defaultValue = "30") Integer size) {
      if (year != null && month != null) {
        CompanyMonthlyChartListResponseDto chartResponseDtos = chartService.getCompanyMonthlyChartList(companyId, year, month);
        return ResponseEntity.ok(chartResponseDtos);
      } else if (year != null || month != null) {
        throw new IllegalArgumentException("year and month must be provided together");
      } else {
        CompanyChartPageReponseDto chartResponseDtos = chartService.getCompanyChartPage(companyId, page, size);
        return ResponseEntity.ok(chartResponseDtos);
      }
  }

  @GetMapping("/user/{userId}/current-price")
  public ResponseEntity<?> getUserCurrentPrice(@PathVariable Long userId) {
    UserCurrentPriceListResponseDto currentPrice = chartService.getUserCurrentPriceList(userId);
    return ResponseEntity.ok(currentPrice);
  }
}
