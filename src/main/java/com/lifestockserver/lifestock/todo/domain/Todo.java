package com.lifestockserver.lifestock.todo.domain;

import java.time.LocalDate;

import com.lifestockserver.lifestock.common.domain.Base;
import com.lifestockserver.lifestock.todo.domain.enums.TodoLevel;
import com.lifestockserver.lifestock.company.domain.Company;
import com.lifestockserver.lifestock.user.domain.User;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Filter;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Table(name = "todo")
@Filter(name = "deletedFilter", condition = "deletedAt is null")
public class Todo extends Base { 
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "user_id")
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

  @PrePersist
  @PreUpdate
  private void setDefaultDay() {
      if (startDate == null) {
        startDate = LocalDate.now();
      }
      if (endDate == null) { 
        endDate = startDate;
      }
  }

  public void setDone(boolean done) {
    if (done) {
      this.done = done;
    }
  }
  
  public void setCompleted(boolean completed) {
    if (completed) {
      this.completed = completed;
      setDone(true);
    }
  }

  public void setDescription(String description) {
    if (done) {
      throw new IllegalStateException("Todo is done");
    }
    if (description != null) {
      this.description = description;
    }
  }
}