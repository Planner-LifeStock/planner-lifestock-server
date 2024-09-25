package com.lifestockserver.lifestock.company.domain.enums;

public enum CompanyLeastOperatePeriod {
  ONE_WEEK(7),
  TWO_WEEK(14),
  ONE_MONTH(30);

  private final int days;

  CompanyLeastOperatePeriod(int days) {
    this.days = days;
  } 

  public int getDays() {
    return days;
  }
}
