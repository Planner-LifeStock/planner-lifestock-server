package com.lifestockserver.lifestock.todo.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.time.LocalDate;

import com.lifestockserver.lifestock.todo.domain.Todo;
import com.lifestockserver.lifestock.todo.service.TodoService;
import com.lifestockserver.lifestock.todo.dto.TodoResponseDto;

@RestController
@RequestMapping("/todo")
public class TodoController {
  private final TodoService todoService;

  public TodoController(TodoService todoService) {
    this.todoService = todoService;
  }

  @GetMapping
  public ResponseEntity<List<TodoResponseDto>> getAllTodos(
    @RequestParam(required = true, value = "userId") Long userId, 
    @RequestParam(required = true, value = "companyId") Long companyId, 
    @RequestParam(required = true, value = "date") LocalDate date
  ) {
    return ResponseEntity.ok(todoService.getAllTodosByDate(userId, companyId, date));
  }

  @GetMapping("/monthly")
  public ResponseEntity<List<TodoResponseDto>> getMonthlyTodos(
    @RequestParam(required = true, value = "userId") Long userId, 
    @RequestParam(required = true, value = "companyId") Long companyId, 
    @RequestParam(required = true, value = "date") LocalDate date
  ) {
    return ResponseEntity.ok(todoService.getMonthlyTodos(userId, companyId, date));
  }
}