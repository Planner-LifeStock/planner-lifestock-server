package com.lifestockserver.lifestock.todo;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lifestockserver.lifestock.TestSecurityConfig;
import com.lifestockserver.lifestock.auth.service.TokenServiceImpl;
import com.lifestockserver.lifestock.todo.controller.TodoController;
import com.lifestockserver.lifestock.todo.domain.Todo;
import com.lifestockserver.lifestock.todo.domain.enums.TodoLevel;
import com.lifestockserver.lifestock.todo.dto.TodoCreateDto;
import com.lifestockserver.lifestock.todo.dto.TodoResponseDto;
import com.lifestockserver.lifestock.todo.service.TodoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@WebMvcTest(TodoController.class)
@AutoConfigureMockMvc
public class TodoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TokenServiceImpl tokenService;

    @MockBean
    private TodoService todoService;

    @Test
    @DisplayName("Todo Create 테스트")
    @WithMockUser(username = "user1", roles = {"USER"})
    void testCreateTodo() throws Exception {

        TodoCreateDto todoCreateDto = TodoCreateDto.builder()
                .userId(1L)
                .companyId(1L)
                .title("Test Todo")
                .description("Test Todo입니다")
                .level(TodoLevel.LEVEL_1) // TodoLevel Enum 값 설정
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusDays(1))
                .days(Set.of(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY))
                .build();

        TodoResponseDto mockResponse = TodoResponseDto.builder()
                .userId(1L)
                .companyId(1L)
                .title("Test Todo")
                .description("Test Todo입니다")
                .level(TodoLevel.LEVEL_1)
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusDays(1))
                .days(Set.of(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY))
                .build();

        when(todoService.createTodo(any())).thenReturn(mockResponse);

        // MockMvc를 통해 POST 요청을 보내고 응답을 검증
        mockMvc.perform(post("/todo")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(todoCreateDto)) // JSON 데이터로 전송
                        .with(httpBasic("user1", "password1"))) // HTTP Basic 인증
                .andExpect(status().isOk()) // 상태 코드가 200인지 확인
                .andExpect(jsonPath("$.title").value("Test Todo")) // 응답 본문에서 title 필드 확인
                .andExpect(jsonPath("$.description").value("Test Todo입니다")); // 응답 본문에서 description 필드 확인

        verify(todoService, times(1)).createTodo(any(TodoCreateDto.class)); // 서비스 메서드 호출 검증
    }

    @Test
    @DisplayName("Todo 완료 상태 업데이트 테스트")
    @WithMockUser(username = "user1", roles = {"USER"})
    void testUpdateTodoCompleted() throws Exception {
        Long todoId = 1L;

        TodoResponseDto mockResponse = TodoResponseDto.builder()
                .userId(1L)
                .companyId(1L)
                .title("Test Todo")
                .description("Test Todo입니다")
                .level(TodoLevel.LEVEL_1)
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusDays(1))
                .days(Set.of(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY))
                .build();

        //When
        when(todoService.updateTodoCompleted(todoId)).thenReturn(mockResponse);

        //Then
        mockMvc.perform(put("/todo/complete/" + todoId) // PUT 요청
                        .with(csrf()) // CSRF 토큰 추가
                        .with(httpBasic("user1", "password1")) // 기본 인증 추가
                        .contentType(MediaType.APPLICATION_JSON)) // JSON 형식으로 Content-Type 설정
                .andExpect(status().isOk()) // 상태 코드 200 확인
                .andExpect(jsonPath("$.title").value("Test Todo")) // title 필드 확인
                .andExpect(jsonPath("$.description").value("Test Todo입니다")); // description 필드 확인

        verify(todoService, times(1)).updateTodoCompleted(todoId);
    }

    @Test
    @DisplayName("Todo 삭제 테스트")
    @WithMockUser(username = "user1", roles = {"USER"})
    void testDeleteTodo() throws Exception {
        Long todoId = 1L;
        String deletedReason = "필요 없어서 삭제했어요~";

        doNothing().when(todoService).deleteTodo(todoId, deletedReason);

        mockMvc.perform(delete("/todo/" + todoId) // DELETE 요청
                        .with(csrf()) // CSRF 토큰 추가
                        .with(httpBasic("user1", "password1")) // HTTP 기본 인증
                        .param("deletedReason", deletedReason) // 쿼리 파라미터 추가
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()); // 상태 코드 200 확인

        verify(todoService, times(1)).deleteTodo(todoId, deletedReason);
    }

    @Test
    @DisplayName("Todo 목록 조회 테스트")
    @WithMockUser(username = "user1", roles = {"USER"})
    void testGetAllTodos() throws Exception {
        Long userId = 1L;
        Long companyId = 1L;
        LocalDate date = LocalDate.now();

        TodoResponseDto todo1 = TodoResponseDto.builder()
                .userId(userId)
                .companyId(companyId)
                .title("Test Todo 1")
                .description("Test Todo 1입니다")
                .level(TodoLevel.LEVEL_1)
                .startDate(date)
                .endDate(date.plusDays(1))
                .days(Set.of(DayOfWeek.MONDAY))
                .build();

        TodoResponseDto todo2 = TodoResponseDto.builder()
                .userId(userId)
                .companyId(companyId)
                .title("Test Todo 2")
                .description("Test Todo 2입니다")
                .level(TodoLevel.LEVEL_2)
                .startDate(date)
                .endDate(date.plusDays(2))
                .days(Set.of(DayOfWeek.WEDNESDAY))
                .build();

        List<TodoResponseDto> mockTodoList = List.of(todo1, todo2);
        when(todoService.getAllTodosByDate(userId, companyId, date)).thenReturn(mockTodoList);

        // MockMvc를 사용해 GET 요청을 보내고 응답을 검증
        mockMvc.perform(get("/todo")
                        .with(csrf()) // CSRF 토큰 추가
                        .with(httpBasic("user1", "password1")) // HTTP 기본 인증
                        .param("userId", userId.toString()) // 쿼리 파라미터 userId
                        .param("companyId", companyId.toString()) // 쿼리 파라미터 companyId
                        .param("date", date.toString()) // 쿼리 파라미터 date
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()) // 상태 코드 200 확인
                .andExpect(jsonPath("$[0].title").value("Test Todo 1")) // 첫 번째 할 일의 title 필드 확인
                .andExpect(jsonPath("$[1].title").value("Test Todo 2")) // 두 번째 할 일의 title 필드 확인
                .andExpect(jsonPath("$[0].description").value("Test Todo 1입니다")) // 첫 번째 할 일의 description 필드 확인
                .andExpect(jsonPath("$[1].description").value("Test Todo 2입니다")); // 두 번째 할 일의 description 필드 확인

        // 서비스 메서드가 올바르게 호출되었는지 검증
        verify(todoService, times(1)).getAllTodosByDate(userId, companyId, date);
    }

    @Test
    @DisplayName("월별 Todo 목록 조회 테스트")
    @WithMockUser(username = "user1", roles = {"USER"})
    void testGetMonthlyTodos() throws Exception{
        Long userId = 1L;
        Long companyId = 1L;
        LocalDate date = LocalDate.now();

        TodoResponseDto todo1 = TodoResponseDto.builder()
                .userId(userId)
                .companyId(companyId)
                .title("Monthly Todo 1")
                .description("월별 Todo 1입니다")
                .level(TodoLevel.LEVEL_1)
                .startDate(date)
                .endDate(date.plusDays(5))
                .days(Set.of(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY))
                .build();

        TodoResponseDto todo2 = TodoResponseDto.builder()
                .userId(userId)
                .companyId(companyId)
                .title("Monthly Todo 2")
                .description("월별 Todo 2입니다")
                .level(TodoLevel.LEVEL_2)
                .startDate(date.plusDays(10))
                .endDate(date.plusDays(15))
                .days(Set.of(DayOfWeek.FRIDAY))
                .build();

        List<TodoResponseDto> mockTodoList = List.of(todo1, todo2);

        // 서비스의 getMonthlyTodos 메서드가 호출되면 mock 데이터를 반환하도록 설정
        when(todoService.getMonthlyTodos(userId, companyId, date)).thenReturn(mockTodoList);

        // MockMvc를 사용해 GET 요청을 보내고 응답을 검증
        mockMvc.perform(get("/todo/monthly")
                        .with(csrf()) // CSRF 토큰 추가
                        .with(httpBasic("user1", "password1")) // HTTP 기본 인증
                        .param("userId", userId.toString()) // 쿼리 파라미터 userId
                        .param("companyId", companyId.toString()) // 쿼리 파라미터 companyId
                        .param("date", date.toString()) // 쿼리 파라미터 date
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()) // 상태 코드 200 확인
                .andExpect(jsonPath("$[0].title").value("Monthly Todo 1")) // 첫 번째 할 일의 title 필드 확인
                .andExpect(jsonPath("$[1].title").value("Monthly Todo 2")) // 두 번째 할 일의 title 필드 확인
                .andExpect(jsonPath("$[0].description").value("월별 Todo 1입니다")) // 첫 번째 할 일의 description 필드 확인
                .andExpect(jsonPath("$[1].description").value("월별 Todo 2입니다")); // 두 번째 할 일의 description 필드 확인

        // 서비스 메서드가 올바르게 호출되었는지 검증
        verify(todoService, times(1)).getMonthlyTodos(userId, companyId, date);
    }
}