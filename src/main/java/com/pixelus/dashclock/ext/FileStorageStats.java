package com.pixelus.dashclock.ext;

import android.content.Context;
import android.text.format.Formatter;

public class FileStorageStats {

  long totalBytes;
  long freeBytes;
  private FileStorageStatsType fileStorageStatsType;

  public FileStorageStats(FileStorageStatsType fileStorageStatsType) {

    this.fileStorageStatsType = fileStorageStatsType;
  }

  public FileStorageStatsType getFileStorageStatsType() {
    return fileStorageStatsType;
  }

  public void setFileStorageStatsType(FileStorageStatsType fileStorageStatsType) {
    this.fileStorageStatsType = fileStorageStatsType;
  }

  public int calculatePercentageFree() {

    if (totalBytes <= 0) {
      return 0;
    }

    return (int) Math.ceil(((float) freeBytes / totalBytes) * 100);
  }

  public long getTotalBytes() {
    return totalBytes;
  }

  public void setTotalBytes(long totalBytes) {
    this.totalBytes = totalBytes;
  }

  public long getFreeBytes() {
    return freeBytes;
  }

  public void setFreeBytes(long freeBytes) {
    this.freeBytes = freeBytes;
  }

  public FileStorageStats withFreeBytes(long freeBytes) {
    this.freeBytes = freeBytes;
    return this;
  }

  public FileStorageStats withTotalBytes(long totalBytes) {
    this.totalBytes = totalBytes;
    return this;
  }

  public String toString(Context context) {
    return String.format("%s: %s of %s free (%s%%)", fileStorageStatsType.getDisplayName(),
        Formatter.formatShortFileSize(context, getFreeBytes()),
        Formatter.formatShortFileSize(context, getTotalBytes()),
        calculatePercentageFree());
  }
}
