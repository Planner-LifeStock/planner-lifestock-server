package com.lifestockserver.lifestock.common.domain.enums;

public enum FileFolder {
  DEFAULT("default"),
  TODO("todo"),
  COMPANY("company"),
  USER("user"),
  FILE("file"),
  RANKING("ranking");

  private final String folderName;

  FileFolder(String folderName) {
    this.folderName = folderName;
  }

  public String getFolderName() {
    return folderName;
  }
  
  
}