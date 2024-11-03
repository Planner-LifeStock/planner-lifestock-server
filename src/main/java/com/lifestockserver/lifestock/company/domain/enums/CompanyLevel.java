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

  public static float getWeight(CompanyLevel companyLevel) {
    if (companyLevel == LOW) {
      return (float) (Math.random() * 0.1);
    } else if (companyLevel == MEDIUM) {
      return (float) (Math.random() * 0.3 + 0.3);
    } else {
      return (float) (Math.random() * 0.3 + 0.7);
    }
  }

  public static int getTodoCount(CompanyLevel companyLevel) {
    if (companyLevel == LOW) {
      return 1;
    } else if (companyLevel == MEDIUM) {
      return 2;
    } else {
      return 3;
    }
  }
}
