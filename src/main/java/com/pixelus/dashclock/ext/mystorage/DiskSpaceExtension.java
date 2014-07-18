package com.pixelus.dashclock.ext.mystorage;

import android.os.StatFs;
import android.util.Log;
import com.crashlytics.android.Crashlytics;
import com.google.android.apps.dashclock.api.DashClockExtension;
import com.google.android.apps.dashclock.api.ExtensionData;

import java.io.File;

import static android.text.format.Formatter.formatShortFileSize;
import static com.pixelus.dashclock.ext.mystorage.DiskSpaceStatsType.EXTERNAL;
import static com.pixelus.dashclock.ext.mystorage.DiskSpaceStatsType.INTERNAL;
import static java.lang.String.format;

public class DiskSpaceExtension extends DashClockExtension {

  public static final String TAG = DiskSpaceExtension.class.getName();

  private File detectedSDCardPath = null;
  private boolean crashlyticsStarted = false;

  private String[] sdPaths = {
      "/data/sdext",
      "/data/sdext2",
      "/data/sdext3",
      "/data/sdext4",
      "/storage/extSdCard/",
      "/storage/extsdcard",
      "/storage/ext_sd",
      "/storage/sdcard0/external_sdcard",
      "/mnt/emmc",
      "/mnt/external_sd/",
      "/mnt/extsdcard",
      "/mnt/media_rw/sdcard1",
      "/mnt/sdcard/ext_sd",
      "/mnt/sdcard/external_sd",
      "/removable/microsd",
      "/storage/external_SD",
      "/storage/sdcard1/",
      "/storage/usbcard1/",
      "/storage/removable/sdcard1",
      "/mnt/sdcard/",
      "/storage/sdcard0/"
  };

  @Override
  protected void onUpdateData(int i) {

    if (!crashlyticsStarted) {
      Crashlytics.start(this);
      crashlyticsStarted = true;
    }

    DiskSpaceStats intDiskSpaceStats = getMyStorageStats(getFilesDir(), INTERNAL);
    long totalBytes = intDiskSpaceStats.getTotalBytes();
    long freeBytes = intDiskSpaceStats.getFreeBytes();

    Log.d(TAG, format("Internal Storage [total: %d, free: %d, free %%: %d]",
        intDiskSpaceStats.getTotalBytes(), intDiskSpaceStats.getFreeBytes(),
        intDiskSpaceStats.calculatePercentageFree()));

    String bodyInternal = intDiskSpaceStats.toString(this);
    String bodyExternal = "";
    File sdCardPath = findSDCardPath();
    if (sdCardPath != null) {
      Log.d(TAG, "SD Card Path: " + sdCardPath.getPath());

      DiskSpaceStats extDiskSpaceStats = getMyStorageStats(sdCardPath, EXTERNAL);
      // Make our best attempt to ensure that the path used for the internal storage isn't the same that
      // we've identified for our external storage.
      if (extDiskSpaceStats.getTotalBytes() != intDiskSpaceStats.getTotalBytes()) {
        totalBytes += extDiskSpaceStats.getTotalBytes();
        freeBytes += extDiskSpaceStats.getFreeBytes();

        Log.d(TAG, format("External Storage [total: %d, free: %d, free %%: %d]",
            extDiskSpaceStats.getTotalBytes(), extDiskSpaceStats.getFreeBytes(),
            extDiskSpaceStats.calculatePercentageFree()));

        bodyExternal = "\n" + extDiskSpaceStats.toString(this);
      }
    }

    int totalPercentageFree = (int) Math.ceil(((float) freeBytes / totalBytes) * 100);
    Log.d(TAG, format("Total Free: %d%%", totalPercentageFree));

    String title = getString(R.string.extension_expanded_title, totalPercentageFree);
    String status = getString(R.string.extension_status, totalPercentageFree, formatShortFileSize(this, freeBytes));

    publishUpdate(new ExtensionData()
            .visible(true)
            .icon(R.drawable.ic_launcher)
            .status(status)
            .expandedTitle(title)
            .expandedBody(bodyInternal + bodyExternal)
        //.clickIntent(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.google.com"))));
    );
  }

  private DiskSpaceStats getMyStorageStats(File filePath, DiskSpaceStatsType diskSpaceStatsType) {

    final StatFs stat = new StatFs(filePath.getPath());
    long blockSize = stat.getBlockSize();

    return new DiskSpaceStats(diskSpaceStatsType)
        .withFreeBytes(blockSize * stat.getAvailableBlocks())
        .withTotalBytes(blockSize * stat.getBlockCount());
  }

  private File findSDCardPath() {

    if (detectedSDCardPath != null) {
      return detectedSDCardPath;
    }

    for (String sdPath : sdPaths) {
      File path = new File(sdPath);
//      Log.d(TAG, "Path: "+ sdPath +" exists? " + path.exists());
      if (path.exists() && path.getTotalSpace() > 0) {
        detectedSDCardPath = path;
        return path;
      }
    }

    return null; // No sdcard?
  }
}