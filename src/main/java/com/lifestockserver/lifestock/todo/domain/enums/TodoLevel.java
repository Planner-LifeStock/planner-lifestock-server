package com.lifestockserver.lifestock.todo.domain.enums;

public enum TodoLevel {
  LEVEL_1(1),
  LEVEL_2(2),
  LEVEL_3(3),
  LEVEL_4(4),
  LEVEL_5(5);

  private final int level;

  TodoLevel(int level) {
    this.level = level;
  }

  public int getLevel() {
    return level;
  }
}