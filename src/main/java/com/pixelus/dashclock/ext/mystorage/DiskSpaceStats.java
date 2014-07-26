package com.pixelus.dashclock.ext.mystorage;

import android.content.Context;

import static android.text.format.Formatter.formatShortFileSize;

public class DiskSpaceStats {

  private long totalBytes;
  private long freeBytes;
  private DiskSpaceStatsType diskSpaceStatsType;

  public DiskSpaceStats(final DiskSpaceStatsType diskSpaceStatsType) {

    this.diskSpaceStatsType = diskSpaceStatsType;
  }

  public DiskSpaceStatsType getDiskSpaceStatsType() {
    return diskSpaceStatsType;
  }

  public void setDiskSpaceStatsType(final DiskSpaceStatsType diskSpaceStatsType) {
    this.diskSpaceStatsType = diskSpaceStatsType;
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

  public void setTotalBytes(final long totalBytes) {
    this.totalBytes = totalBytes;
  }

  public long getFreeBytes() {
    return freeBytes;
  }

  public void setFreeBytes(final long freeBytes) {
    this.freeBytes = freeBytes;
  }

  public DiskSpaceStats withFreeBytes(final long freeBytes) {
    this.freeBytes = freeBytes;
    return this;
  }

  public DiskSpaceStats withTotalBytes(final long totalBytes) {
    this.totalBytes = totalBytes;
    return this;
  }

  public String toString(final Context context) {

    return context.getString(R.string.extension_expanded_body_storage_line,
        diskSpaceStatsType.getDisplayName(),
        calculatePercentageFree(),
        formatShortFileSize(context, getFreeBytes()),
        formatShortFileSize(context, getTotalBytes()));
  }
}
