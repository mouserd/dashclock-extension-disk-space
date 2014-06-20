package com.pixelus.dashclock.ext.filestorage;

public enum FileStorageStatsType {

  INTERNAL("Internal"),
  EXTERNAL("External");

  private String displayName;

  private FileStorageStatsType(String displayName) {
    this.displayName = displayName;
  }

  public String getDisplayName() {
    return displayName;
  }
}
