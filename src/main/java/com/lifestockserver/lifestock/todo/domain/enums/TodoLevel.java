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

  public static float getWeight(TodoLevel todoLevel) {
    if (todoLevel == LEVEL_5) {
      return 0.035f;
    } else if (todoLevel == LEVEL_4) {
      return 0.03f;
    } else if (todoLevel == LEVEL_3) {
      return 0.025f;
    } else if (todoLevel == LEVEL_2) {
      return 0.02f;
    } else {
      return 0.015f;
    }
  }
}
