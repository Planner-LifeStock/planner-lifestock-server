package com.lifestockserver.lifestock.company.domain;

import java.util.ArrayList;

import com.lifestockserver.lifestock.common.domain.Base;
import com.lifestockserver.lifestock.company.domain.enums.CompanyLevel;
import com.lifestockserver.lifestock.user.domain.User;
import com.lifestockserver.lifestock.todo.domain.Todo;
import com.lifestockserver.lifestock.file.domain.File;
import com.lifestockserver.lifestock.config.AppConfig;

import java.time.LocalDate;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.beans.factory.annotation.Autowired;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Table(name = "company")
public class Company extends Base {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @OneToMany(mappedBy = "company", fetch = FetchType.LAZY)
  private ArrayList<Todo> todos;

  private String name;
  
  @Column(nullable = true)
  private String description;
  
  @Enumerated(EnumType.STRING)
  private CompanyLevel level;

  // 매각날짜
  private LocalDate sellDate;
  // 투자 금액
  private Long investmentAmount;
  // 주식 상장가
  private Long initialStockPrice;
  // 주식 발행수
  @Builder.Default
  private Long initialStockQuantity = 100L;

  @OneToOne
  @JoinColumn(name = "file_id")
  private File logo;
  private String logoFilePath;
  
  @Transient
  @Autowired
  private AppConfig appConfig;

  @PrePersist
  private void prePersist() {
      setInitialStockPrice();
      setLogoFilePath();
  }

  @PreUpdate
  private void preUpdate() {
    setLogoFilePath();
  }

  private void setInitialStockPrice() {
    this.initialStockPrice = this.initialStockPrice * 3 / 4 / this.initialStockQuantity;
  }

  private void setLogoFilePath() {
    if (logo != null) {
      this.logoFilePath = logo.getPath();
    } else {
      this.logoFilePath = appConfig.getDefaultLogoPath();
    }
  }
  
  @Transient
  private Long currentStockPrice;

  public void setCurrentStockPrice(Long currentStockPrice) {
    this.currentStockPrice = currentStockPrice;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public void setLevel(CompanyLevel level) {
    this.level = level;
  }
  
  public void setSellDate(LocalDate sellDate) {
    this.sellDate = sellDate;
  }
}