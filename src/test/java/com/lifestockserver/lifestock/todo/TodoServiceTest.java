package com.lifestockserver.lifestock.todo;

import com.lifestockserver.lifestock.company.domain.Company;
import com.lifestockserver.lifestock.company.repository.CompanyRepository;
import com.lifestockserver.lifestock.todo.domain.Todo;
import com.lifestockserver.lifestock.todo.dto.TodoCreateDto;
import com.lifestockserver.lifestock.todo.dto.TodoResponseDto;
import com.lifestockserver.lifestock.todo.mapper.TodoMapper;
import com.lifestockserver.lifestock.todo.repository.TodoRepository;
import com.lifestockserver.lifestock.todo.service.TodoService;
import com.lifestockserver.lifestock.user.domain.User;
import com.lifestockserver.lifestock.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.test.context.support.WithMockUser;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class TodoServiceTest {

    @Mock
    private TodoRepository todoRepository;

    @Mock
    private TodoMapper todoMapper;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CompanyRepository companyRepository;

    @InjectMocks
    private TodoService todoService;

    private TodoCreateDto todoCreateDto;
    private TodoResponseDto todoResponseDto;

    @BeforeEach
    void setUp(){
        MockitoAnnotations.openMocks(this);
        todoCreateDto = TodoCreateDto.builder()
                .userId(1L)
                .companyId(1L)
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusDays(1))
                .build();
        todoResponseDto = TodoResponseDto.builder()
                .id(1L)
                .userId(1L)
                .companyId(1L)
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusDays(1))
                .build();
    }

    @WithMockUser(username = "user1", roles = {"USER"})
    @Test
    void testCreateTodo(){
        User user = new User();
        user.setId(1L);

        Company company = Company.builder()
                .id(1L)
                .user(user)
                .build();

        //user 모킹
        when(userRepository.findById(todoCreateDto.getUserId())).thenReturn(Optional.of(user));

        //company 모킹
        when(companyRepository.findById(todoCreateDto.getCompanyId())).thenReturn(Optional.of(company));

        // TodoMapper의 toEntity 모킹 설정
        when(todoMapper.toEntity(any(TodoCreateDto.class), any(User.class), any(Company.class))).thenReturn(Todo.builder()
                .id(1L)
                .user(user)
                .company(company)
                .startDate(todoCreateDto.getStartDate())
                .endDate(todoCreateDto.getEndDate())
                .build());

        //to do 객체 저장 모킹 설정
        when(todoRepository.save(any(Todo.class))).thenReturn(Todo.builder()
                .id(1L)
                .user(user)
                .company(company)
                .startDate(todoCreateDto.getStartDate())
                .endDate(todoCreateDto.getEndDate())
                .build());

        //TodoMapper 모킹 설정
        when(todoMapper.toDto(any(Todo.class))).thenReturn(todoResponseDto);

        TodoResponseDto result = todoService.createTodo(todoCreateDto);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(todoRepository, times(1)).save(any(Todo.class));
    }
}
