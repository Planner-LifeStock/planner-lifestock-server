package com.lifestockserver.lifestock.todo.domain;

import java.time.LocalDate;
import java.time.DayOfWeek;
import java.util.Set;
import java.util.EnumSet;

import com.lifestockserver.lifestock.common.domain.Base;
import com.lifestockserver.lifestock.todo.domain.enums.TodoLevel;
import com.lifestockserver.lifestock.company.domain.Company;
import com.lifestockserver.lifestock.user.domain.User;

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

  @ManyToOne
  @JoinColumn(name = "member_id")
  private User user;

  @ManyToOne
  @JoinColumn(name = "company_id")
  private Company company;

  private String title;
  @Column(nullable = true)
  private String description;
  @Builder.Default
  private boolean completed = false;
  @Builder.Default
  private boolean done = false;
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

  void setDone(boolean done) {
    if (done) {
      this.done = done;
    }
  }
  
  void setCompleted(boolean completed) {
    if (completed) {
      this.completed = completed;
      setDone(true);
    }
  }

  void setTitle(String title) {
    if (done) {
      throw new IllegalStateException("Todo is done");
    }
    if (title != null) {
      this.title = title;
    }
  }

  void setDescription(String description) {
    if (done) {
      throw new IllegalStateException("Todo is done");
    }
    if (description != null) {
      this.description = description;
    }
  }

  void setStartDate(LocalDate startDate) {
    if (done) {
      throw new IllegalStateException("Todo is done");
    }
    if (startDate != null) {
      this.startDate = startDate;
    }
  }

  void setEndDate(LocalDate endDate) {
    if (done) {
      throw new IllegalStateException("Todo is done");
    }
    if (endDate != null) {
      this.endDate = endDate;
    }
  }
  
  void setDays(Set<DayOfWeek> days) {
    if (done) {
      throw new IllegalStateException("Todo is done");
    }
    if (days != null) {
      this.days = days;
    }
  }
  
}