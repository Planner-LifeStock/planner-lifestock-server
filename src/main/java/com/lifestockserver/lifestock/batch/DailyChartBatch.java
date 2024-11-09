package com.lifestockserver.lifestock.batch;

import com.lifestockserver.lifestock.chart.service.DailyChartService;
import com.lifestockserver.lifestock.company.service.CompanyService;
import com.lifestockserver.lifestock.chart.domain.Chart;
import com.lifestockserver.lifestock.chart.domain.DailyChart;
import com.lifestockserver.lifestock.chart.service.ChartService;
import com.lifestockserver.lifestock.todo.service.TodoService;
import com.lifestockserver.lifestock.company.domain.enums.CompanyLevel;

import org.springframework.stereotype.Component;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.LocalDate;
import java.util.Optional;

@Component
public class DailyChartBatch {
    private final DailyChartService dailyChartService;
    private final CompanyService companyService;
    private final ChartService chartService;
    private final TodoService todoService;

    public DailyChartBatch(DailyChartService dailyChartService, 
        CompanyService companyService, 
        ChartService chartService, 
        TodoService todoService) {
        this.dailyChartService = dailyChartService; 
        this.companyService = companyService;
        this.chartService = chartService;
        this.todoService = todoService;
    }

    /**
     * 매일 23:59에 실행되는 batch. 해당 날짜를 a라 하자.
     * Company.listedDate == null인 Company에 대해 진행.
     * dailyChartService.createDailtChart(Company, a + 1)로 다음날 DailyChart를 만들고. 
     * T odo.companyId 가 Company.id와 같으며 Todo.done = false AND endDate <= a인 Todo를 
     * 모두 TodoService.updateTodo(Todo.getId())를 시행해. 
     * 그 후에는 ChartService.getLatestChart(Company)를 통해 가져온 Chart를 가지고 
     * 전날에 만들어진 DailyChart (날짜 a에 해당)의 id값을 가져와서 
     * dailyChartService.updateDailChart(DailyChart, Company, TodoService.countAllByCompanyAndDate(Company, a), TodoService.countCompletedByCompanyAndDate(Company, a))를 호출해줘.
     */
    @Scheduled(cron = "0 59 23 * * *")
    public void executeDailyChartBatch() {
        LocalDate today = LocalDate.now();

        companyService.findAllUnlisted().stream()
            .forEach(company -> {
                // 완료되지 않은 Todo들을 완료로 업데이트
                todoService.findAllByCompanyIdAndDoneFalseAndEndDateLessThanEqual(company.getId(), today)
                    .forEach(todo -> {
                        todoService.updateTodoDone(todo.getId());
                    });

                // 최근 차트 가져오기
                Chart latestChart = chartService.findLatestByCompanyId(company.getId());
                
                // 해당 날에 대한 Todo 개수와 완료된 Todo 개수 가져오기
                int todoCount = todoService.getTodoCountByCompanyIdAndDate(company.getId(), today);
                int completedCount = todoService.getCompletedCountByCompanyIdAndDate(company.getId(), today);
                boolean isDailyCompleted = completedCount >= (int)(todoCount * 7 / 10);
                if (completedCount == 0) {
                    isDailyCompleted = false;
                    // 주가 하락: 1 - 0.05 * 회사 가중치
                    latestChart = chartService.createChart(company, latestChart, today, (long)(latestChart.getClose() * (1 - 0.05 * CompanyLevel.getWeight(company.getLevel()))), false);
                } else if (todoCount >= 3 && todoCount == completedCount) {
                    // 주가 상승: 1.1
                    latestChart = chartService.createChart(company, latestChart, today, (long)(latestChart.getClose() * 1.1), false);
                }

                // 연속 완료 횟수
                int consecutiveCompletedCount = dailyChartService.findLatestConsecutiveCompletedCountByCompanyId(company.getId());
                int updateConsecutiveCompletedCount = isDailyCompleted ? consecutiveCompletedCount + 1 : 0;

                // 주간 완료 횟수
                Optional<DailyChart> yesterdayDailyChart = dailyChartService.findByCompanyIdAndDate(company.getId(), today.minusDays(1));
                int weeklyCompletedCount = isDailyCompleted ? 1 : 0;
                if (yesterdayDailyChart.isPresent()) {
                    weeklyCompletedCount += yesterdayDailyChart.get().getWeeklyCompletedCount();
                }
                
                // 주간 완료 여부 확인
                LocalDate companyCreatedDate = company.getCreatedAt().toLocalDate();
                boolean isWeeklyDone = (today.getDayOfWeek() == companyCreatedDate.getDayOfWeek()) && (today.getDayOfWeek() == companyCreatedDate.getDayOfWeek());
                /**
                 * 해당 주차의 회사 난이도에 따라 주 1회 반영+ 5 / 3 / 1%
                 * 현재 주가 * (100%  +- 5 / 3 / 1%)
                 */
                if (isWeeklyDone) {
                    if (CompanyLevel.getTodoCount(company.getLevel()) > weeklyCompletedCount) {
                        // 주가 하락
                        latestChart = chartService.createChart(company, latestChart, today, (long)(latestChart.getClose() * (1 - 0.1 * CompanyLevel.getWeight(company.getLevel()))), false);
                    } else {
                        // 주가 상승
                        latestChart = chartService.createChart(company, latestChart, today, (long)(latestChart.getClose() * (1 + 0.1 * CompanyLevel.getWeight(company.getLevel()))), false);
                    }
                    weeklyCompletedCount = 0;
                }
                // DailyChart 생성
                DailyChart dailyChart = DailyChart.builder()
                    .company(company)
                    .date(today)
                    .open(latestChart.getOpen())
                    .high(latestChart.getHigh())
                    .low(latestChart.getLow())
                    .close(latestChart.getClose())
                    .todoCount(todoCount)
                    .completedCount(completedCount)
                    .dailyCompleted(isDailyCompleted)
                    .consecutiveCompletedCount(updateConsecutiveCompletedCount)
                    .weeklyCompletedCount(weeklyCompletedCount)
                    .build();
                dailyChartService.save(dailyChart);

                // 매일 새로운 initial chart 생성
                chartService.createDailyInitialChart(company, latestChart, today.plusDays(1));
            });
    }
}
