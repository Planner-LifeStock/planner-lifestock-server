package com.lifestockserver.lifestock.todo.service;

import org.springframework.stereotype.Service;
import com.lifestockserver.lifestock.todo.repository.TodoRepository;

@Service
public class TodoService {
  private final TodoRepository todoRepository;

  public TodoService(TodoRepository todoRepository) {
    this.todoRepository = todoRepository;
  }
}