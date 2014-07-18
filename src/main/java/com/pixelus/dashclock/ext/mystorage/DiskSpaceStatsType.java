package com.pixelus.dashclock.ext.mystorage;

public enum DiskSpaceStatsType {

  INTERNAL("Internal"),
  EXTERNAL("External");

  private String displayName;

  private DiskSpaceStatsType(String displayName) {
    this.displayName = displayName;
  }

  public String getDisplayName() {
    return displayName;
  }
}
