package com.pixelus.dashclock.ext.mystorage;

import android.content.Context;

import static android.text.format.Formatter.formatShortFileSize;

public class MyStorageStats {

  private long totalBytes;
  private long freeBytes;
  private MyStorageStatsType myStorageStatsType;

  public MyStorageStats(MyStorageStatsType myStorageStatsType) {

    this.myStorageStatsType = myStorageStatsType;
  }

  public MyStorageStatsType getMyStorageStatsType() {
    return myStorageStatsType;
  }

  public void setMyStorageStatsType(MyStorageStatsType myStorageStatsType) {
    this.myStorageStatsType = myStorageStatsType;
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

  public MyStorageStats withFreeBytes(long freeBytes) {
    this.freeBytes = freeBytes;
    return this;
  }

  public MyStorageStats withTotalBytes(long totalBytes) {
    this.totalBytes = totalBytes;
    return this;
  }

  public String toString(Context context) {

    String expandedBodyLine = context.getString(R.string.extension_expanded_body_storage_line);
    return String.format(expandedBodyLine, myStorageStatsType.getDisplayName(),
        calculatePercentageFree(),
        formatShortFileSize(context, getFreeBytes()),
        formatShortFileSize(context, getTotalBytes()));
  }
}
