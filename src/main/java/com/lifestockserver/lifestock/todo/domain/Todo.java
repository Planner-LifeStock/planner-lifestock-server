package com.lifestockserver.lifestock.todo.domain;

import java.time.LocalDate;
import java.time.DayOfWeek;
import java.util.Set;
import java.util.EnumSet;

import com.lifestockserver.lifestock.common.domain.Base;
import com.lifestockserver.lifestock.todo.domain.enums.TodoLevel;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Table(name = "todo")
public class Todo extends Base { 
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String title;
  @Column(nullable = true)
  private String description;
  @Builder.Default
  private boolean completed = false;
  @Builder.Default
  @Enumerated(EnumType.STRING)
  private TodoLevel level = TodoLevel.LEVEL_3;
  
  private LocalDate startDate;
  private LocalDate endDate;
  @Builder.Default
  private Set<DayOfWeek> days = EnumSet.noneOf(DayOfWeek.class);

  @PrePersist
  @PreUpdate
  private void setDefaultDay() {
      if (startDate == null) {
        startDate = LocalDate.now();
      }
      if (endDate == null) { 
        endDate = startDate;
      }
      if (days.isEmpty()) {
          days = EnumSet.of(startDate.getDayOfWeek());
      }
  }
}