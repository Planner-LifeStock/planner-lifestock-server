package com.lifestockserver.lifestock.company.domain;

import com.lifestockserver.lifestock.common.domain.Base;
import com.lifestockserver.lifestock.company.domain.enums.CompanyLevel;
import com.lifestockserver.lifestock.user.domain.User;
import com.lifestockserver.lifestock.file.domain.File;
import com.lifestockserver.lifestock.company.domain.enums.CompanyLeastOperatePeriod;

import java.time.LocalDate;

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
@Table(name = "company")
@Filter(name = "deletedCompanyFilter", condition = "deletedAt IS NULL")
public class Company extends Base {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  private String name;
  
  @Column(nullable = true)
  private String description;
  
  @Enumerated(EnumType.STRING)
  private CompanyLevel level;

  // 최소 운영 기간
  @Enumerated(EnumType.STRING)
  private CompanyLeastOperatePeriod leastOperatePeriod;
  // 매각날짜
  @Column(nullable = true)
  private LocalDate listedDate;
  // 투자 금액
  private Long investmentAmount;
  // 주식 상장가
  private Long initialStockPrice;
  // 주식 발행수
  @Builder.Default
  private Long initialStockQuantity = 100L;
  // 매각 시 주가
  @Column(nullable = true)
  private Long listedStockPrice;

  @ManyToOne
  @JoinColumn(name = "logo_file_id")
  private File logo;

  @PrePersist
  private void setInvestmentAmount() {
    this.investmentAmount = initialStockQuantity * initialStockPrice;
  }
  
  public void setDescription(String description) {
    this.description = description;
  }
  
  public void setListed(LocalDate listedDate, Long listedStockPrice) {
    this.listedDate = listedDate;
    this.listedStockPrice = listedStockPrice;
  }

  public void setLogo(File logo) {
    this.logo = logo;
  }
}