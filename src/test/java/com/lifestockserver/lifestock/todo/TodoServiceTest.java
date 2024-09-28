package com.lifestockserver.lifestock.todo;

import com.lifestockserver.lifestock.company.domain.Company;
import com.lifestockserver.lifestock.company.repository.CompanyRepository;
import com.lifestockserver.lifestock.todo.domain.Todo;
import com.lifestockserver.lifestock.todo.domain.enums.TodoLevel;
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

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
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

    @Test
    void  testUpdateTodoCompleted(){

        //Given
        Long todoId = 1L;
        User user = new User();
        user.setId(1L);

        Company company = Company.builder()
                .id(1L)
                .user(user)
                .build();

        Todo todo = Todo.builder()
                .id(1L)
                .user(user)
                .company(company)
                .completed(false)
                .build();

        when(todoRepository.findByIdAndDeletedAtIsNull(todoId)).thenReturn(todo);
        when(todoRepository.save(any(Todo.class))).thenReturn(todo);
        when(todoMapper.toDto(any(Todo.class))).thenReturn(TodoResponseDto.builder()
                .id(todoId)
                .completed(true)  // 완료 상태로 변경된 상태를 반환
                .build());

        //when
        TodoResponseDto result = todoService.updateTodoCompleted(todoId);

        //Then
        assertNotNull(result);
        assertEquals(todoId, result.getId());
        assertTrue(result.isCompleted());  // 완료 상태가 true인지 확인
        verify(todoRepository, times(1)).save(todo);
    }

    @Test
    void testDeleteTodo(){
        //Given
        Long todoId = 1L;
        String deleteReason = "테스트 삭제 사유";
        User user = new User();
        user.setId(1L);

        Company company = Company.builder()
                .id(1L)
                .user(user)
                .build();

        Todo todo = Todo.builder()
                .id(1L)
                .title("테스트 Todo")
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusDays(1))
                .days(EnumSet.of(DayOfWeek.MONDAY, DayOfWeek.TUESDAY))
                .build();

        when(todoRepository.findByIdAndDeletedAtIsNull(todoId)).thenReturn(todo);

        //when
        todoService.deleteTodo(todoId, deleteReason);

        //then
        assertNotNull(todo.getDeletedAt());
        assertEquals(deleteReason, todo.getDeletedReason());
        verify(todoRepository, times(1)).save(todo);
    }

    @Test
    void testGetAllTodosByDate(){
        Long userId = 1L;
        Long companyId = 1L;
        LocalDate date = LocalDate.now().with(DayOfWeek.MONDAY);

        Todo todo = Todo.builder()
                .id(1L)
                .title("테스트 Todo")
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusDays(1))
                .days(EnumSet.of(DayOfWeek.MONDAY, DayOfWeek.TUESDAY))
                .build();

        todoResponseDto = TodoResponseDto.builder()
                .id(1L)
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusDays(1))
                .build();

        List<Todo> todos = Arrays.asList(todo);
        when(todoRepository.findAllByUserIdAndCompanyIdAndDate(userId, companyId, date)).thenReturn(todos);
        when(todoMapper.toDto(any(Todo.class))).thenReturn(todoResponseDto);

        List<TodoResponseDto> result = todoService.getAllTodosByDate(userId, companyId, date);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(todoResponseDto.getId(), result.get(0).getId());

        verify(todoRepository, times(1)).findAllByUserIdAndCompanyIdAndDate(userId, companyId, date);
        verify(todoMapper, times(1)).toDto(todo);
    }

    @Test
    void testGetMonthlyTodos(){
        Long userId = 1L;
        Long companyId = 1L;
        LocalDate date = LocalDate.now();

        Todo todo = Todo.builder()
                .id(1L)
                .title("테스트 Todo")
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusDays(1))
                .days(EnumSet.of(DayOfWeek.MONDAY, DayOfWeek.TUESDAY))
                .build();

        List<Todo> todos = Arrays.asList(todo);

        when(todoRepository.findAllByUserIdAndCompanyIdAndMonth(userId, companyId, date)).thenReturn(todos);
        when(todoMapper.toDto(any(Todo.class))).thenReturn(todoResponseDto);

        List<TodoResponseDto> result = todoService.getMonthlyTodos(userId, companyId, date);

        // 결과 확인
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(todoResponseDto.getId(), result.get(0).getId());

        verify(todoRepository, times(1)).findAllByUserIdAndCompanyIdAndMonth(userId, companyId, date);
        verify(todoMapper, times(1)).toDto(todo);
    }
}
