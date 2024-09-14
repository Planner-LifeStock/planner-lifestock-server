package com.lifestockserver.lifestock.company.domain.enums;

public enum CompanyLevel {
  LOW(1),
  MEDIUM(2),
  HIGH(3);

  private final int level;

  CompanyLevel(int level) {
    this.level = level;
  }

  public int getLevel() {
    return level;
  }
}