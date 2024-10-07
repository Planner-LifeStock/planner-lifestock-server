//package com.lifestockserver.lifestock;
//
//import com.lifestockserver.lifestock.company.domain.Company;
//import com.lifestockserver.lifestock.company.domain.enums.CompanyLevel;
//import com.lifestockserver.lifestock.company.domain.enums.CompanyLeastOperatePeriod;
//import com.lifestockserver.lifestock.company.repository.CompanyRepository;
//import com.lifestockserver.lifestock.chart.domain.Chart;
//import com.lifestockserver.lifestock.chart.repository.ChartRepository;
//import com.lifestockserver.lifestock.user.domain.User;
//import com.lifestockserver.lifestock.user.repository.UserRepository;
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.stereotype.Component;
//
//import java.time.LocalDate;
//import java.util.Random;
//import java.util.List;
//import java.util.stream.Collectors;
//
//@Component
//@RequiredArgsConstructor
//public class DataInitializer implements CommandLineRunner {
//
//    private final CompanyRepository companyRepository;
//    private final ChartRepository chartRepository;
//    private final UserRepository userRepository;
//
//    @Override
//    public void run(String... args) throws Exception {
//        List<User> users = userRepository.findAll();
//
//        if (users.size() > 0 && companyRepository.count() < 10) {
//            // 10개의 회사 생성
//            for (int i = 1; i <= 10; i++) {
//                User user = users.get(i % users.size()); // 유저 목록 중 하나 선택
//                Company company = Company.builder()
//                        .user(user)
//                        .name("Company " + i)
//                        .description("Description for Company " + i)
//                        .level(CompanyLevel.values()[new Random().nextInt(CompanyLevel.values().length)])
//                        .leastOperatePeriod(CompanyLeastOperatePeriod.values()[new Random().nextInt(CompanyLeastOperatePeriod.values().length)])
//                        .investmentAmount((long) (new Random().nextInt(100000) + 10000))
//                        .initialStockPrice((long) (new Random().nextInt(1000) + 100))
//                        .initialStockQuantity(100L)
//                        .listedDate(LocalDate.now().minusDays(new Random().nextInt(365)))
//                        .build();
//
//                Company savedCompany = companyRepository.save(company);
//
//                // 각 회사에 대해 100개의 차트 생성
//                for (int j = 1; j <= 100; j++) {
//                    Chart chart = Chart.builder()
//                            .company(savedCompany)
//                            .user(user)
//                            .open((long) (new Random().nextInt(1000) + 100))
//                            .high((long) (new Random().nextInt(1000) + 100))
//                            .low((long) (new Random().nextInt(100) + 50))
//                            .close((long) (new Random().nextInt(1000) + 100))
//                            .build();
//
//                    chartRepository.save(chart);
//                }
//            }
//            System.out.println("회사 및 차트 더미 데이터 생성 완료");
//        } else {
//            System.out.println("이미 데이터가 충분히 존재합니다.");
//        }
//    }
//}
