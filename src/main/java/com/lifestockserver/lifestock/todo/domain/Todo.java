package com.lifestockserver.lifestock.todo.domain;

import java.time.LocalDate;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@EqualsAndHashCode
@Table(name = "todo")
public class Todo {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String title;
  @Column(nullable = true)
  private String description;
  @Builder.Default
  private boolean completed = false;
  @Builder.Default
  private byte level = 3;
  
  private LocalDate startDate;
  private LocalDate endDate;
}