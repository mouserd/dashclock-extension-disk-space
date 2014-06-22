package com.pixelus.dashclock.ext.mystorage;

public enum MyStorageStatsType {

  INTERNAL("Internal"),
  EXTERNAL("External");

  private String displayName;

  private MyStorageStatsType(String displayName) {
    this.displayName = displayName;
  }

  public String getDisplayName() {
    return displayName;
  }
}
