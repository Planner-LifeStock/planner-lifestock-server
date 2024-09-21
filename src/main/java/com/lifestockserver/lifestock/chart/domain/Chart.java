package com.lifestockserver.lifestock.chart.domain;

import com.lifestockserver.lifestock.common.domain.Base;
import com.lifestockserver.lifestock.company.domain.Company;
import com.lifestockserver.lifestock.todo.domain.Todo;
import com.lifestockserver.lifestock.user.domain.User;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "charts")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Chart extends Base {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "company_id")
  private Company company;

  @ManyToOne
  @JoinColumn(name = "user_id")
  private User user;

  @ManyToOne
  @JoinColumn(name = "todo_id")
  private Todo todo;

  private Long open;
  private Long high;
  private Long low;
  private Long close;
}
