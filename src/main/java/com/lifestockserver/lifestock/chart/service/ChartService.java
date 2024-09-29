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
import com.lifestockserver.lifestock.todo.domain.Todo;
import com.lifestockserver.lifestock.user.repository.UserRepository;
import com.lifestockserver.lifestock.company.repository.CompanyRepository;
import com.lifestockserver.lifestock.todo.repository.TodoRepository;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class ChartService {

    private final ChartRepository chartRepository;
    private final ChartMapperImpl chartMapperImpl;
    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;
    private final TodoRepository todoRepository;

    public ChartService(ChartRepository chartRepository, ChartMapperImpl chartMapperImpl, UserRepository userRepository, CompanyRepository companyRepository, TodoRepository todoRepository) {
        this.chartRepository = chartRepository;
        this.chartMapperImpl = chartMapperImpl;
        this.userRepository = userRepository;
        this.companyRepository = companyRepository;
        this.todoRepository = todoRepository;
    }

    @Transactional
    public ChartResponseDto createChart(ChartCreateDto chartCreateDto) {
        User user = userRepository.findById(chartCreateDto.getUserId())
            .orElseThrow(() -> new IllegalArgumentException("Invalid user Id:" + chartCreateDto.getUserId()));
        Company company = companyRepository.findById(chartCreateDto.getCompanyId())
            .orElseThrow(() -> new IllegalArgumentException("Invalid company Id:" + chartCreateDto.getCompanyId()));
        Todo todo = null;
        if (chartCreateDto.getTodoId() != null) {
            todo = todoRepository.findById(chartCreateDto.getTodoId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid todo Id:" + chartCreateDto.getTodoId()));
        }

        if (company.getUser().getId() != user.getId() || (todo != null && todo.getUser().getId() != user.getId())) {
            throw new IllegalArgumentException("company or todo user id is invalid:" + user.getId());
        }
        if (todo != null && todo.getCompany().getId() != company.getId()) {
            throw new IllegalArgumentException("todo company id is invalid:" + company.getId());
        }

        Chart chart = chartMapperImpl.toChart(chartCreateDto, user, company, todo);
        Chart savedChart = chartRepository.save(chart);
        return chartMapperImpl.toChartResponseDto(savedChart);
    }
    
    public ChartResponseDto getChart(Long id) {
        Chart chart = chartRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Invalid chart Id:" + id));
        return chartMapperImpl.toChartResponseDto(chart);
    }

    public Long getLatestCloseByCompanyId(Long companyId) {
        Chart chart = chartRepository.findLatestAfterMarketOpenChartByCompanyId(companyId);
        return chart.getClose();
    }

    public CompanyMonthlyChartListResponseDto getCompanyMonthlyChartList(Long companyId, int year, int month) {
        List<Chart> charts = chartRepository.findLatestAfterMarketOpenChartListByCompanyIdAndYearMonth(companyId, year, month);
        return chartMapperImpl.toCompanyMonthlyChartListResponseDto(charts);
    }

    public CompanyChartPageReponseDto getCompanyChartPage(Long companyId, int page, int size) {
        Page<Chart> charts = chartRepository.findLatestAfterMarketOpenChartPageByCompanyId(companyId, PageRequest.of(page, size));
        return chartMapperImpl.toCompanyChartPageReponseDto(charts);
    }

    public UserCurrentPriceListResponseDto getUserCurrentPriceList(Long userId) {
        List<Chart> charts = chartRepository.findLatestChartsByUserIdGroupedByCompany(userId);
        return chartMapperImpl.toUserCurrentPriceListResponseDto(charts);
    }

  }
