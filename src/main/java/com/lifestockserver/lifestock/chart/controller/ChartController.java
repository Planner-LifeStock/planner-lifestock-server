package com.lifestockserver.lifestock.chart.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;

import com.lifestockserver.lifestock.chart.dto.CompanyChartPageResponseDto;
import com.lifestockserver.lifestock.chart.dto.CompanyChartMonthlyListResponseDto;
import com.lifestockserver.lifestock.chart.dto.UserChartListResponseDto;
import com.lifestockserver.lifestock.chart.dto.ChartResponseDto;
import com.lifestockserver.lifestock.chart.dto.ChartCreateDto;
import com.lifestockserver.lifestock.chart.service.ChartService;
import com.lifestockserver.lifestock.chart.domain.Chart;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.data.domain.Page;

@RestController
@RequestMapping("/chart")
public class ChartController {
    private final ChartService chartService;

    public ChartController(ChartService chartService) {
        this.chartService = chartService;
    }

    @PostMapping()
    public ResponseEntity<ChartResponseDto> saveChart(@RequestBody ChartCreateDto chartCreateDto) {
        return ResponseEntity.ok(chartService.saveChart(chartCreateDto));
    }

    @GetMapping("/company/{companyId}")
    public ResponseEntity<?> getCompanyChartPage(@PathVariable Long companyId, @RequestParam(required = true, value = "userId") Long userId, @RequestParam(required = false, value = "page", defaultValue = "1") int page, @RequestParam(required = false, value = "size", defaultValue = "30") int size) {
        return ResponseEntity.ok(chartService.getCompanyChartPage(companyId, page, size));
    }
    // public ResponseEntity<CompanyChartPageResponseDto> getCompanyChartPage(@PathVariable Long companyId, @RequestParam(required = true, value = "userId") Long userId, @RequestParam(required = false, value = "page", defaultValue = "1") int page, @RequestParam(required = false, value = "size", defaultValue = "30") int size) {
    //     return ResponseEntity.ok(chartService.getCompanyChartPage(companyId, page, size));
    // }

    @GetMapping("/company/all/{companyId}")
    public ResponseEntity<?> getCompanyChartList(@PathVariable Long companyId) {
        return ResponseEntity.ok(chartService.getCompanyChartList(companyId));
    }

    @GetMapping("/company/count/{companyId}")
    public ResponseEntity<Long> getCompanyChartCount(@PathVariable Long companyId) {
        return ResponseEntity.ok(chartService.getCompanyChartCount(companyId));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<UserChartListResponseDto> getUserChartList(@PathVariable Long userId) {
        return ResponseEntity.ok(chartService.getUserChartList(userId));
    }

    @GetMapping("/monthly/{date}")
    public ResponseEntity<CompanyChartMonthlyListResponseDto> getCompanyMonthlyChartList(@PathVariable String date, @RequestParam(required = true, value = "companyId") Long companyId) {
        return ResponseEntity.ok(chartService.getCompanyMonthlyChartList(date, companyId));
    }
}
