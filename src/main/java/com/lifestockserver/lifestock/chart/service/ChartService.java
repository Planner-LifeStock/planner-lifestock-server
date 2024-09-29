package com.lifestockserver.lifestock.chart.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.lifestockserver.lifestock.chart.domain.Chart;
import com.lifestockserver.lifestock.user.domain.User;
import com.lifestockserver.lifestock.todo.domain.Todo;
import com.lifestockserver.lifestock.company.domain.Company;
import com.lifestockserver.lifestock.company.repository.CompanyRepository;
import com.lifestockserver.lifestock.user.repository.UserRepository;
import com.lifestockserver.lifestock.todo.repository.TodoRepository;
import com.lifestockserver.lifestock.chart.repository.ChartRepository;
import com.lifestockserver.lifestock.chart.mapper.ChartMapper;
import com.lifestockserver.lifestock.chart.dto.CompanyChartPageResponseDto;
import com.lifestockserver.lifestock.chart.dto.UserChartListResponseDto;
import com.lifestockserver.lifestock.chart.dto.CompanyChartMonthlyListResponseDto;
import com.lifestockserver.lifestock.chart.dto.ChartCreateDto;
import com.lifestockserver.lifestock.chart.dto.ChartResponseDto;
@Service
public class ChartService {
  private final CompanyRepository companyRepository;
  private final UserRepository userRepository;
  private final TodoRepository todoRepository;
  private final ChartRepository chartRepository;
  private final ChartMapper chartMapper;

  public ChartService(ChartRepository chartRepository, ChartMapper chartMapper, CompanyRepository companyRepository, UserRepository userRepository, TodoRepository todoRepository) {
    this.chartRepository = chartRepository;
    this.chartMapper = chartMapper;
    this.companyRepository = companyRepository;
    this.userRepository = userRepository;
    this.todoRepository = todoRepository;
  }

  @Transactional
  public ChartResponseDto saveChart(ChartCreateDto chartCreateDto) {
    Long open;
    Long high;
    Long low;
    Long close = chartCreateDto.getClose();
    
    Chart prevChart = chartRepository.findTopByCompany_IdOrderByCreatedAtDesc(chartCreateDto.getCompanyId());
    Company company = companyRepository.findByIdAndDeletedAtIsNull(chartCreateDto.getCompanyId());
    User user = userRepository.findById(chartCreateDto.getUserId()).orElseThrow(() -> new RuntimeException("User not found"));
    Todo todo = todoRepository.findById(chartCreateDto.getTodoId()).orElseThrow(() -> new RuntimeException("Todo not found"));

    if (prevChart == null) {
      open = company.getInitialStockPrice();
      high = open;
      low = open;
    } else if (prevChart.getCreatedAt().toLocalDate().isBefore(LocalDate.now())) {
      open = prevChart.getClose();
      high = open;
      low = open;
    } else {
      open = prevChart.getOpen();
      high = prevChart.getHigh();
      low = prevChart.getLow();
    }

    if (high < close) {
      high = close;
    }
    if (low > close) {
      low = close;
    }

    Chart chart = Chart.builder()
      .user(user)
      .todo(todo)
      .company(company)
      .open(open)
      .high(high)
      .low(low)
      .close(close)
      .build();

    Chart savedChart = chartRepository.save(chart);

    return chartMapper.toChartResponseDto(savedChart);
  }

  @Transactional(readOnly = true)
  public Long getLatestCloseByCompanyId(Long companyId) {
    return chartRepository.findLatestChartByCompanyId(companyId).orElseThrow(() -> new RuntimeException("There is no chart for the company: " + companyId)).getClose();
  }

  @Transactional(readOnly = true)
  public Long getLatestCloseByCompanyIdAndDate(Long companyId, LocalDate date) {
    return chartRepository.findLatestChartByCompanyIdAndDate(companyId, date).orElseThrow(() -> new RuntimeException("There is no chart for the company: " + companyId)).getClose();
  }

  @Transactional(readOnly = true)
  public Page<Chart> getCompanyChartPage(Long companyId, int page, int size) {
    Pageable pageable = PageRequest.of(page, size);
    Page<Chart> chartPage = chartRepository.findLatestChartsPageByCompanyId(companyId, pageable);
    
    // return chartMapper.toCompanyChartPageResponseDto(chartPage);
    return chartPage;
  }

  @Transactional(readOnly = true)
  public UserChartListResponseDto getUserChartList(Long userId) {
    List<Chart> chartList = chartRepository.findLatestChartsForUnlistedCompaniesByUserId(userId);
    UserChartListResponseDto userChartListResponseDto = new UserChartListResponseDto();
    
    userChartListResponseDto.setUserId(userId);
    userChartListResponseDto.setChartList(chartMapper.toUserChartElementResponseDtoList(chartList));
    
    return userChartListResponseDto;
  }

  @Transactional(readOnly = true)
  public CompanyChartMonthlyListResponseDto getCompanyMonthlyChartList(String date, Long companyId) {
    List<Chart> chartList = chartRepository.findLatestChartsForCompanyByMonth(companyId, LocalDate.parse(date));
    
    return chartMapper.toCompanyChartMonthlyListResponseDto(chartList, LocalDate.parse(date));
  }

  @Transactional(readOnly = true)
  public Long getCompanyChartCount(Long companyId) {
    return chartRepository.countChartsByCompanyId(companyId);
  }

  @Transactional(readOnly = true)
  public List<?> getCompanyChartList(Long companyId) {
    return chartRepository.findAllChartResponseDtosByCompanyId(companyId);
  }
}
