/*
package com.lifestockserver.lifestock;

import com.lifestockserver.lifestock.company.domain.Company;
import com.lifestockserver.lifestock.company.domain.enums.CompanyLevel;
import com.lifestockserver.lifestock.company.domain.enums.CompanyLeastOperatePeriod;
import com.lifestockserver.lifestock.company.repository.CompanyRepository;
import com.lifestockserver.lifestock.chart.domain.Chart;
import com.lifestockserver.lifestock.chart.repository.ChartRepository;
import com.lifestockserver.lifestock.user.domain.User;
import com.lifestockserver.lifestock.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final CompanyRepository companyRepository;
    private final ChartRepository chartRepository;
    private final UserRepository userRepository;

    @Override
    public void run(String... args) throws Exception {
        if (companyRepository.count() < 10) {
            // 더미 User를 가져옴
            List<User> users = userRepository.findAll();
            Random random = new Random();

            for (int i = 1; i <= 10; i++) {
                // User 중 랜덤으로 선택
                User user = users.get(random.nextInt(users.size()));

                // Company 엔티티 생성
                Company company = Company.builder()
                        .user(user)
                        .name("Company " + i)
                        .description("Description for company " + i)
                        .level(CompanyLevel.values()[random.nextInt(CompanyLevel.values().length)])
                        .leastOperatePeriod(CompanyLeastOperatePeriod.values()[random.nextInt(CompanyLeastOperatePeriod.values().length)])
                        .investmentAmount((long) (random.nextInt(10000) + 1000))
                        .initialStockPrice((long) (random.nextInt(500) + 100))
                        .initialStockQuantity((long) (random.nextInt(1000) + 100))
                        .listedDate(LocalDate.now().minusDays(random.nextInt(365)))
                        .build();

                // Company 저장
                Company savedCompany = companyRepository.save(company);

                // Chart 데이터 생성
                for (int j = 0; j < 100; j++) {
                    Chart chart = Chart.builder()
                            .company(savedCompany)
                            .user(user)
                            .open((long) (random.nextInt(500) + 100))
                            .high((long) (random.nextInt(600) + 100))
                            .low((long) (random.nextInt(400) + 100))
                            .close((long) (random.nextInt(500) + 100))
                            .date(LocalDateTime.now().minusDays(j))
                            .isAfterMarketOpen(true)
                            .build();

                    chartRepository.save(chart);
                }
            }
            System.out.println("Company와 Chart 더미 데이터 생성 완료");
        } else {
            System.out.println("이미 데이터가 존재합니다.");
        }
    }
}
*/