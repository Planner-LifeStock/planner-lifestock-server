package com.lifestockserver.lifestock.Company;

import com.lifestockserver.lifestock.company.domain.Company;
import com.lifestockserver.lifestock.company.domain.enums.CompanyLeastOperatePeriod;
import com.lifestockserver.lifestock.company.domain.enums.CompanyLevel;
import com.lifestockserver.lifestock.company.repository.CompanyRepository;
import com.lifestockserver.lifestock.todo.domain.Todo;
import com.lifestockserver.lifestock.todo.repository.TodoRepository;
import com.lifestockserver.lifestock.user.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.security.Key;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class CompanyRepositoryTest {

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private TodoRepository todoRepository;

    private User user;
    private Company company;

    @BeforeEach
    public void setUp(){
        user = new User();
        user.setId(1L);

        company = Company.builder()
                .user(user)
                .name("Test Company")
                .description("This is a test company")
                .level(CompanyLevel.LOW)
                .leastOperatePeriod(CompanyLeastOperatePeriod.ONE_WEEK)
                .listedDate(LocalDate.now())
                .investmentAmount(100000L)
                .initialStockPrice(1000L)
                .build();
        companyRepository.save(company);
    }

    @Test
    void testFindByIdAndDeletedAtIsNull(){
        Company foundCompany = companyRepository.findByIdAndDeletedAtIsNull(company.getId());
        assertThat(foundCompany).isNotNull();
        assertThat(foundCompany.getName()).isEqualTo("Test Company");
    }

    @Test
    void testFindAllByUserId(){
        List<Company> companies = companyRepository.findAllByUserId(user.getId());

        assertThat(companies).isNotEmpty();
        assertThat(companies.get(0).getName()).isEqualTo("Test Company");
    }

    @Test
    void testFindAllByUserIdAndDeletedAtIsNull(){

        company.setDeletedAt(LocalDate.now().atStartOfDay());
        companyRepository.save(company);

        List<Company> companies = companyRepository.findAllByUserIdAndDeletedAtIsNull(user.getId());

        assertThat(companies).isEmpty();
    }

    @Test
    void testFindTodosByCompanyIdAndDAte(){
        Todo todo = Todo.builder()
                .company(company)
                .startDate(LocalDate.now().minusDays(1))
                .endDate(LocalDate.now().plusDays(1))
                .build();

        todoRepository.save(todo);

        List<Todo> todos = companyRepository.findTodosByCompanyIdAndDate(company.getId(), LocalDate.now());

        assertThat(todos).isNotEmpty();
        assertThat(todos.get(0).getCompany()).isEqualTo(company);
    }
}
