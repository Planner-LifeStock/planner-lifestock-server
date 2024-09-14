package com.lifestockserver.lifestock.todo.domain.enums;

public enum Day {
  MON(1),
  TUES(2),
  WEDNES(3),
  THURS(4),
  FRI(5),
  SATUR(6),
  SUN(7);

  private final int day;

  Day(int day) {
    this.day = day;
  }

  public int getDay() {
    return day;
  }
}