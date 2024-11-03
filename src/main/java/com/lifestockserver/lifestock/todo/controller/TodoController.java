package com.lifestockserver.lifestock.todo.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.DeleteMapping;
import java.util.List;
import java.time.LocalDate;

import com.lifestockserver.lifestock.todo.dto.TodoCreateDto;
import com.lifestockserver.lifestock.todo.service.TodoService;
import com.lifestockserver.lifestock.todo.dto.TodoResponseDto;
import com.lifestockserver.lifestock.user.domain.CustomUserDetails;

import org.springframework.security.core.annotation.AuthenticationPrincipal;

@RestController
@RequestMapping("/todo")
public class TodoController {
  private final TodoService todoService;

  public TodoController(TodoService todoService) {
    this.todoService = todoService;
  }

  @GetMapping
  public ResponseEntity<List<TodoResponseDto>> getAllTodos(
    @AuthenticationPrincipal CustomUserDetails userDetails,
    @RequestParam(required = true, value = "companyId") Long companyId, 
    @RequestParam(required = true, value = "date") LocalDate date
  ) {
    return ResponseEntity.ok(todoService.getAllTodosByDate(userDetails.getUserId(), companyId, date));
  }

  @GetMapping("/monthly")
  public ResponseEntity<List<TodoResponseDto>> getMonthlyTodos(
    @AuthenticationPrincipal CustomUserDetails userDetails,
    @RequestParam(required = true, value = "companyId") Long companyId, 
    @RequestParam(required = true, value = "date") LocalDate date
  ) {
    return ResponseEntity.ok(todoService.getMonthlyTodos(userDetails.getUserId(), companyId, date));
  }

  @PostMapping
  public ResponseEntity<TodoResponseDto> createTodo(
    @AuthenticationPrincipal CustomUserDetails userDetails,
    @RequestBody TodoCreateDto todoCreateDto
  ) {
    return ResponseEntity.ok(todoService.createTodo(userDetails.getUserId(), todoCreateDto));
  }

  @PutMapping("/complete/{id}")
  public ResponseEntity<?> updateTodoCompleted(
    @PathVariable Long id
  ) {
    todoService.updateTodoCompleted(id);
    return ResponseEntity.ok().build();
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteTodo(
    @PathVariable Long id,
    @RequestParam(required = false, value = "deletedReason") String deletedReason
  ) {
    todoService.deleteTodo(id, deletedReason);
    return ResponseEntity.ok().build();
  }
}