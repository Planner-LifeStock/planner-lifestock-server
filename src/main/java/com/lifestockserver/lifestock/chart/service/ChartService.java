package com.lifestockserver.lifestock.chart.service;

import org.springframework.stereotype.Service;
import com.lifestockserver.lifestock.chart.repository.ChartRepository;

@Service
public class ChartService {
  private final ChartRepository chartRepository;

  public ChartService(ChartRepository chartRepository) {
    this.chartRepository = chartRepository;
  }

  public Long getLatestHighByCompanyId(Long companyId) {
    return 100L;
    // return chartRepository.findLatestHighByCompanyId(companyId).orElse(null);
  }
}
