package com.lifestockserver.lifestock.todo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.lifestockserver.lifestock.todo.domain.Todo;

public interface TodoRepository extends JpaRepository<Todo, Long> {
}