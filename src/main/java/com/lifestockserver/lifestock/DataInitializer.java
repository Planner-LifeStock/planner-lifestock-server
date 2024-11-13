//package com.lifestockserver.lifestock;
//
//import com.lifestockserver.lifestock.chart.domain.Chart;
//import com.lifestockserver.lifestock.chart.repository.ChartRepository;
//import com.lifestockserver.lifestock.company.domain.Company;
//import com.lifestockserver.lifestock.company.repository.CompanyRepository;
//import com.lifestockserver.lifestock.todo.domain.Todo;
//import com.lifestockserver.lifestock.todo.repository.TodoRepository;
//import com.lifestockserver.lifestock.user.domain.User;
//import com.lifestockserver.lifestock.user.repository.UserRepository;
//import lombok.RequiredArgsConstructor;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.stereotype.Component;
//
//import java.time.LocalDate;
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.List;
//import java.util.Optional;
//
//@Component
//@RequiredArgsConstructor
//public class DataInitializer implements CommandLineRunner {
//
//    private final ChartRepository chartRepository;
//    private final CompanyRepository companyRepository;
//    private final UserRepository userRepository;
//    private final TodoRepository todoRepository;
//
//    @Override
//    public void run(String... args) throws Exception {
//        Long companyId = 36L;
//        Long userId = 139L;
//        List<Integer> todoIds = new ArrayList<>(List.of(460, 461, 462, 463, 464, 465, 466, 467, 468));
//
//        // 필요한 개수만큼 todoIds를 확장하여 총 15개로 만듭니다.
//        while (todoIds.size() < 15) {
//            todoIds.addAll(List.of(460, 461, 462, 463, 464, 465, 466, 467, 468));
//        }
//        todoIds = todoIds.subList(0, 15);
//
//        // 무작위 셔플
//        Collections.shuffle(todoIds);
//
//        Optional<Company> optionalCompany = companyRepository.findById(companyId);
//        Optional<User> optionalUser = userRepository.findById(userId);
//
//        if (optionalCompany.isPresent() && optionalUser.isPresent()) {
//            Company company = optionalCompany.get();
//            User user = optionalUser.get();
//
//            for (int i = 0; i < 15; i++) {
//                LocalDate date = LocalDate.of(2023, 10, 1).plusDays(i);
//                long high = (long) (Math.random() * 5000);
//                long low = (long) (Math.random() * high);
//                long open = (i % 2 == 0) ? high : low;
//                boolean isAfterMarketOpen = i == 14;
//
//                int todoIdValue = todoIds.get(i);
//                Optional<Todo> optionalTodo = todoRepository.findById((long) todoIdValue);
//
//                // 중복 확인: 동일한 날짜와 Todo 조합이 존재하면 건너뜁니다.
//                if (optionalTodo.isPresent()) {
//                    Todo todo = optionalTodo.get();
//
//                    // 존재 여부 확인
//                    boolean exists = chartRepository.existsByDateAndTodoAndCompanyAndUser(date, todo, company, user);
//
//                    if (!exists) {
//                        Chart chart = Chart.builder()
//                                .company(company)
//                                .user(user)
//                                .open(open)
//                                .high(high)
//                                .low(low)
//                                .close(open)
//                                .date(date)
//                                .isAfterMarketOpen(isAfterMarketOpen)
//                                .todo(todo)
//                                .build();
//
//                        chartRepository.save(chart);
//                    } else {
//                        System.out.printf("중복된 데이터 건너뜀: 날짜 %s, Todo ID %d 조합%n", date, todoIdValue);
//                    }
//                }
//            }
//
//            System.out.println("Custom Chart 데이터 생성 완료");
//        } else {
//            System.out.println("지정한 Company 또는 User를 찾을 수 없습니다.");
//        }
//    }
//}
