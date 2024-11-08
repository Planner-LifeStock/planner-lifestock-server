package com.lifestockserver.lifestock.chart.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import com.lifestockserver.lifestock.chart.dto.CompanyChartPageReponseDto;
import com.lifestockserver.lifestock.chart.dto.CompanyMonthlyChartListResponseDto;
import com.lifestockserver.lifestock.chart.service.ChartService;
import com.lifestockserver.lifestock.user.domain.CustomUserDetails;

@RestController
public class ChartController {

  @Autowired
  private ChartService chartService;

  public ChartController(ChartService chartService) {
    this.chartService = chartService;
  }

  @GetMapping("/company/{companyId}/charts")
  public ResponseEntity<?> getCompanyCharts(@PathVariable Long companyId,
    @RequestParam(required = false) Integer year,
    @RequestParam(required = false) Integer month,
    @RequestParam(required = false, defaultValue = "0") Integer page,
    @RequestParam(required = false, defaultValue = "30") Integer size,
    @AuthenticationPrincipal CustomUserDetails userDetails) {
      if (year != null && month != null) {
        CompanyMonthlyChartListResponseDto chartResponseDtos = chartService.getCompanyMonthlyChartList(companyId, year.intValue(), month.intValue(), userDetails.getUserId());
        return ResponseEntity.ok(chartResponseDtos);
      } else {
        CompanyChartPageReponseDto chartResponseDtos = chartService.getCompanyChartPage(companyId, page.intValue(), size.intValue(), userDetails.getUserId());
        return ResponseEntity.ok(chartResponseDtos);
      }
  }

  @GetMapping("/user/asset")
  public ResponseEntity<Long> getUserAsset(@AuthenticationPrincipal CustomUserDetails userDetails) {
    return ResponseEntity.ok(chartService.getAssetByUserId(userDetails.getUserId()));
  }
}
