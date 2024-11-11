package com.lifestockserver.lifestock.chart.service;

import com.lifestockserver.lifestock.chart.dto.ChartCreateDto;
import com.lifestockserver.lifestock.chart.dto.ChartResponseDto;
import com.lifestockserver.lifestock.chart.dto.CompanyChartPageReponseDto;
import com.lifestockserver.lifestock.chart.dto.CompanyMonthlyChartListResponseDto;
import com.lifestockserver.lifestock.chart.dto.UserCurrentPriceListResponseDto;
import com.lifestockserver.lifestock.chart.domain.Chart;
import com.lifestockserver.lifestock.chart.repository.ChartRepository;
import com.lifestockserver.lifestock.chart.mapper.ChartMapperImpl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import com.lifestockserver.lifestock.user.domain.User;
import com.lifestockserver.lifestock.company.domain.Company;
import com.lifestockserver.lifestock.company.domain.enums.CompanyLevel;
import com.lifestockserver.lifestock.todo.domain.Todo;
import com.lifestockserver.lifestock.todo.domain.enums.TodoLevel;
import com.lifestockserver.lifestock.user.repository.UserRepository;

import lombok.extern.slf4j.Slf4j;

import com.lifestockserver.lifestock.company.repository.CompanyRepository;

import java.util.List;
import java.time.LocalDate;

@Slf4j
@Service
@Transactional(readOnly = true)
public class ChartService {

    private final ChartRepository chartRepository;
    private final ChartMapperImpl chartMapperImpl;
    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;

    public ChartService(ChartRepository chartRepository, ChartMapperImpl chartMapperImpl, UserRepository userRepository, CompanyRepository companyRepository) {
        this.chartRepository = chartRepository;
        this.chartMapperImpl = chartMapperImpl;
        this.userRepository = userRepository;
        this.companyRepository = companyRepository;
    }

    @Transactional
    public ChartResponseDto createInitialChart(Company company, User user, Long initialStockPrice) {
        ChartCreateDto chartCreateDto = ChartCreateDto.builder()
            .companyId(company.getId())
            .open(initialStockPrice)
            .high(initialStockPrice)
            .low(initialStockPrice)
            .close(initialStockPrice)
            .build();
        Chart chart = chartMapperImpl.toChart(chartCreateDto, user, company, null);
        Chart savedChart = chartRepository.save(chart);
        log.info("saved initial chart close: {}", savedChart.getClose());
        return chartMapperImpl.toChartResponseDto(savedChart);
    }

    @Transactional
    public void createDailyInitialChart(Company company, Chart latestChart, LocalDate date) {
        Chart chart = Chart.builder()
            .company(company)
            .user(latestChart.getUser())
            .date(date)
            .open(latestChart.getClose())
            .high(latestChart.getClose())
            .low(latestChart.getClose())
            .close((long) (latestChart.getClose() * (1 + (Math.random() * 0.1 - 0.05))))
            .build();
        log.info("created daily initial chart close: {}", chart.getClose());
        chartRepository.save(chart);
    }

    @Transactional
    public Chart createChart(Company company, Chart latestChart, LocalDate date, Long calculatedClose, boolean isAfterMarketOpen) {
        Chart chart = Chart.builder()
            .company(company)
            .user(latestChart.getUser())
            .open(latestChart.getClose())
            .high(latestChart.getClose())
            .low(latestChart.getClose())
            .close(calculatedClose)
            .date(date)
            .isAfterMarketOpen(isAfterMarketOpen)
            .build();
        return chartRepository.save(chart);
    }

    public Chart findLatestByCompanyId(Long companyId) {
        return chartRepository.findLatestByCompanyId(companyId);
    }

    @Transactional
    public ChartResponseDto createChart(Todo todo, LocalDate date, int consecutiveCompletedCount) {
        User user = userRepository.findById(todo.getUser().getId())
        .orElseThrow(() -> new IllegalArgumentException("Invalid user Id:" + todo.getUser().getId()));
        Company company = companyRepository.findById(todo.getCompany().getId())
        .orElseThrow(() -> new IllegalArgumentException("Invalid company Id:" + todo.getCompany().getId()));
        if (company.getUser().getId() != user.getId()) {
            throw new IllegalArgumentException("company user id is invalid:" + user.getId());
        }

        log.info("consecutive completed count: {}", consecutiveCompletedCount);
        Chart latestChart = findLatestByCompanyId(company.getId());
        if (latestChart.getDate().isBefore(date)
            || latestChart.isAfterMarketOpen() == false) {
            createDailyInitialChart(company, latestChart, date);
        }
        int dailyCompletedTodoCount = countCompletedByCompanyIdAndDate(todo.getCompany().getId(), date);
        Long calculatedClose = calculateChartClose(
            todo.isCompleted(), 
            dailyCompletedTodoCount, 
            consecutiveCompletedCount,
            CompanyLevel.getWeight(todo.getCompany().getLevel()), 
            TodoLevel.getWeight(todo.getLevel()), 
            latestChart.getClose());
        log.info("calculated close: {}", calculatedClose);
        Chart chart = Chart.builder()
            .company(company)
            .user(user)
            .todo(todo)
            .open(latestChart.getOpen())
            .high(latestChart.getHigh())
            .low(latestChart.getLow())
            .close(calculatedClose)
            .date(date)
            .isAfterMarketOpen(todo.isCompleted())
            .build();
        Chart savedChart = chartRepository.save(chart);
        log.info("created chart close: {}", savedChart.getClose());
        return chartMapperImpl.toChartResponseDto(savedChart);
    }

    // 정규분포. 최대 0.0511 최소 0.0111
    private double getNormalDistribution(int consecutiveCompletedCount) {
        double m = 15.0;      // 평균
        double sigma = 8.59;  // 표준 편차
        double a = consecutiveCompletedCount; 

        double density = (1 / (Math.sqrt(2 * Math.PI * Math.pow(sigma, 2)))) *
                         Math.exp(-Math.pow(a - m, 2) / (2 * Math.pow(sigma, 2)));
        log.info("normal distribution: {}", density);
        return density;
    }

    private Long calculateChartClose(boolean isCompleted, 
        int dailyCompletedTodoCount, 
        int consecutiveCompletedCount,
        float companyWeight, 
        float todoWeight, 
        Long latestChartClose) {
        // consecutiveCompletedCount는 15를 평균으로 정규분포값을 구한다.
        double normalDistribution = getNormalDistribution(consecutiveCompletedCount) * 10;
        // 완료 개수에 따라 제한이 생긴다.
        float completedLimit;

        if (dailyCompletedTodoCount <= 3) {
            completedLimit = 1.0f;
        } else if (dailyCompletedTodoCount <= 5) {
            completedLimit = 0.8f;
        } else if (dailyCompletedTodoCount <= 10) {
            completedLimit = 0.5f;
        } else {
            completedLimit = 0.05f;
        }
        log.info("completed limit: {}", completedLimit);
        log.info("normal distribution: {}", normalDistribution);
        log.info("latest chart close: {}", latestChartClose);
        log.info("is completed: {}", isCompleted);
        log.info("todo weight: {}", todoWeight);
        log.info("company weight: {}", companyWeight);
        double weight = (isCompleted ? 1 : -1) * todoWeight *
        (1 + companyWeight) * (1 + normalDistribution) * completedLimit;
        log.info("weight: {}", weight);

        Long calculatedClose = (long) (latestChartClose * (1 + weight));
        log.info("calculated close: {}", calculatedClose);
        return calculatedClose;
    }

    public ChartResponseDto getChart(Long id) {
        Chart chart = chartRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Invalid chart Id:" + id));
        return chartMapperImpl.toChartResponseDto(chart);
    }

    public Chart getLatestAfterMarketOpenChartByCompanyId(Long companyId) {
        return chartRepository.findLatestAfterMarketOpenChartByCompanyId(companyId)
            .orElseThrow(() -> new IllegalArgumentException("Latest after market open chart not found:" + companyId));
    }

    public Long getLatestCloseByCompanyId(Long companyId) {
        Chart chart = chartRepository.findLatestAfterMarketOpenChartByCompanyId(companyId)
            .orElseThrow(() -> new IllegalArgumentException("Latest after market open chart not found:" + companyId));
        log.info("chart: {}, {}", chart.getCompany().getId(), chart.getId());
        log.info("latest chart close: {}", chart.getClose());
        return chart.getClose();
    }

    public CompanyMonthlyChartListResponseDto getCompanyMonthlyChartList(Long companyId, int year, int month, Long userId) {
        Company company = companyRepository.findById(companyId)
            .orElseThrow(() -> new IllegalArgumentException("Invalid company Id:" + companyId));
        if (company.getUser().getId() - userId != 0) {
            throw new IllegalArgumentException("company user id is invalid:" + userId);
        }
        List<Chart> charts = chartRepository.findLatestAfterMarketOpenChartListByCompanyIdAndYearMonth(companyId, year, month);
        log.info("charts size: {}", charts.size());
        log.info("charts: {}", charts);
        return chartMapperImpl.toCompanyMonthlyChartListResponseDto(charts);
    }

    public CompanyChartPageReponseDto getCompanyChartPage(Long companyId, int page, int size, Long userId) {
        Company company = companyRepository.findById(companyId)
            .orElseThrow(() -> new IllegalArgumentException("Invalid company Id:" + companyId));
        if (company.getUser().getId() - userId != 0) {
            throw new IllegalArgumentException("company user id is invalid:" + userId);
        }
        Page<Chart> charts = chartRepository.findLatestAfterMarketOpenChartPageByCompanyId(companyId, PageRequest.of(page, size));
        log.info("charts size: {}", charts.getContent().size());
        log.info("charts: {}", charts.getContent());
        return chartMapperImpl.toCompanyChartPageReponseDto(charts);
    }

    public UserCurrentPriceListResponseDto getUserCurrentPriceList(Long userId) {
        List<Chart> charts = chartRepository.findLatestChartsByUserIdGroupedByCompany(userId);

        return chartMapperImpl.toUserCurrentPriceListResponseDto(charts);
    }

    public int countCompletedByCompanyIdAndDate(Long companyId, LocalDate date) {
        return chartRepository.countCompletedByCompanyIdAndDate(companyId, date);
    }

    public Long getTotalStockPriceByUserId(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid user Id:" + userId));

        Long totalStockPrice = chartRepository.getTotalStockPriceByUserId(user.getId());
        Long userAsset = user.getAsset();

        // null 값을 0L로 대체하여 계산
        totalStockPrice = (totalStockPrice != null) ? totalStockPrice : 0L;
        userAsset = (userAsset != null) ? userAsset : 0L;

        return totalStockPrice + userAsset;
    }
}
