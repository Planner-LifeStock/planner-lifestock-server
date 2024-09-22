package com.lifestockserver.lifestock.todo.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.lifestockserver.lifestock.todo.service.TodoService;

@RestController
@RequestMapping("/todo")
public class TodoController {
  private final TodoService todoService;

  public TodoController(TodoService todoService) {
    this.todoService = todoService;
  }
}