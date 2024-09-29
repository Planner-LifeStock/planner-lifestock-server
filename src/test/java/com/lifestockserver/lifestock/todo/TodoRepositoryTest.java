package com.lifestockserver.lifestock.todo;

import com.lifestockserver.lifestock.company.domain.Company;
import com.lifestockserver.lifestock.company.domain.enums.CompanyLeastOperatePeriod;
import com.lifestockserver.lifestock.company.domain.enums.CompanyLevel;
import com.lifestockserver.lifestock.company.repository.CompanyRepository;
import com.lifestockserver.lifestock.todo.domain.Todo;
import com.lifestockserver.lifestock.todo.repository.TodoRepository;
import com.lifestockserver.lifestock.user.domain.User;
import com.lifestockserver.lifestock.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class TodoRepositoryTest {

    @Autowired
    private TodoRepository todoRepository;

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private UserRepository userRepository;

    private User user;
    private Company company;

    @BeforeEach
    public void setUp(){
        user = new User();
        user.setId(1L);
        //테스트를 위한 임의의 유저

        company = Company.builder()
                .user(user)
                .name("Test Company")
                .description("이것은 Test Company입니다.")
                .level(CompanyLevel.LOW)
                .leastOperatePeriod(CompanyLeastOperatePeriod.ONE_MONTH)
                .listedDate(LocalDate.now())
                .investmentAmount(10000L)
                .initialStockPrice(1000L)
                .build();
        companyRepository.save(company);
        //테스트르 위한 임의의 company
    }

    @Test
    void testFindAllByUserIdAndCompanyIdAndDate(){
        //When
        Todo todo = Todo.builder()
                .user(user)
                .company(company)
                .startDate(LocalDate.now().minusDays(1))
                .endDate(LocalDate.now().plusDays(1))
                .build();
        todoRepository.save(todo);

        //Then
        List<Todo> todos = todoRepository.findAllByUserIdAndCompanyIdAndDate(user.getId(), company.getId(), LocalDate.now());
        assertThat(todos).isNotEmpty(); //todo리스트는 비어있으면 안됨.
        assertThat(todos.get(0)).isEqualTo(todo); //todo리스트의 0번 인덱스에는 위에서 만든 todo객체가 들어가야함.
    }

    @Test
    void testFindAllByUserIdAndCompanyIdAndMonth(){
        //When
        Todo todo1 = Todo.builder()
                .user(user)
                .company(company)
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusDays(1))
                .build();
        //todo1 - 이번달

        Todo todo2 = Todo.builder()
                .user(user)
                .company(company)
                .startDate(LocalDate.now().minusMonths(2))
                .endDate(LocalDate.now().minusMonths(1))
                .build();
        //todo2 - 지난달

        todoRepository.save(todo1);
        todoRepository.save(todo2);

        //Then
        List<Todo> todos = todoRepository.findAllByUserIdAndCompanyIdAndMonth(user.getId(), company.getId(), LocalDate.now());
        assertThat(todos).contains(todo1); //이번달의 todo는 리스트에 포함되어야함
        assertThat(todos).doesNotContain(todo2); //지난달의 todo는 리스트에 포함되면 안됨
    }

    @Test
    void testFindByIdAndDeletedAtIsNull(){
        //When
        Todo todo = Todo.builder()
                .user(user)
                .company(company)
                .startDate(LocalDate.now().minusDays(1))
                .endDate(LocalDate.now().plusDays(1))
                .build();
        todoRepository.save(todo);

        Todo foundTodo = todoRepository.findByIdAndDeletedAtIsNull(todo.getId());
        assertThat(foundTodo).isNotNull();
        assertThat(foundTodo).isEqualTo(todo);
    }
}
