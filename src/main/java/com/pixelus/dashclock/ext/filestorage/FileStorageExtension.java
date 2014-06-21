package com.pixelus.dashclock.ext.filestorage;

import android.os.Environment;
import android.os.StatFs;
import android.util.Log;
import com.google.android.apps.dashclock.api.DashClockExtension;
import com.google.android.apps.dashclock.api.ExtensionData;

import java.io.File;

import static java.lang.String.format;

public class FileStorageExtension extends DashClockExtension {

  public static final String TAG = FileStorageExtension.class.getName();

  private String[] sdPaths = {
      "/storage/extSdCard/",
      "/storage/sdcard1/",
      "/storage/usbcard1/",
      "/storage/sdcard0/"
  };

  @Override
  protected void onUpdateData(int i) {

    FileStorageStats intFileStorageStats = getFileStorageStats(Environment.getExternalStorageDirectory(), FileStorageStatsType.INTERNAL);
    long totalBytes = intFileStorageStats.getTotalBytes();
    long freeBytes = intFileStorageStats.getFreeBytes();

    Log.d(TAG, format("Internal Storage [total: %d, free: %d, free %%: %d]",
        intFileStorageStats.getTotalBytes(), intFileStorageStats.getFreeBytes(),
        intFileStorageStats.calculatePercentageFree()));

    String bodyExternal = "";
    File sdCardPath = findSDCardPath();
    Log.d(TAG, "SD Card Path: " + sdCardPath.getPath());
    if (sdCardPath != null) {

      FileStorageStats extFileStorageStats = getFileStorageStats(sdCardPath, FileStorageStatsType.EXTERNAL);
      // Make our best attempt to ensure that the path used for the internal storage isn't the same that
      // we've identified for our external storage.
      if (extFileStorageStats.getTotalBytes() != intFileStorageStats.getTotalBytes()) {
        totalBytes += extFileStorageStats.getTotalBytes();
        freeBytes += extFileStorageStats.getFreeBytes();

        Log.d(TAG, format("External Storage [total: %d, free: %d, free %%: %d]",
            extFileStorageStats.getTotalBytes(), extFileStorageStats.getFreeBytes(),
            extFileStorageStats.calculatePercentageFree()));

        bodyExternal = "\n" + extFileStorageStats.toString(this);
      }
    }

    int totalPercentageFree = (int) Math.ceil(((float) freeBytes / totalBytes) * 100);
    Log.d(TAG, format("Total Free: %d%%", totalPercentageFree));

    String bodyInternal = intFileStorageStats.toString(this);
    String title = format(getString(R.string.extension_expanded_title), totalPercentageFree);

    publishUpdate(new ExtensionData()
            .visible(true)
            .icon(R.drawable.ic_launcher)
            .status(getString(R.string.extension_title))
            .expandedTitle(title)
            .expandedBody(bodyInternal + bodyExternal)
        //.clickIntent(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.google.com"))));
    );
  }

  private FileStorageStats getFileStorageStats(File filePath, FileStorageStatsType fileStorageStatsType) {

    final StatFs stat = new StatFs(filePath.getPath());
    long blockSize = stat.getBlockSize();

    return new FileStorageStats(fileStorageStatsType)
        .withFreeBytes(blockSize * stat.getAvailableBlocks())
        .withTotalBytes(blockSize * stat.getBlockCount());
  }

  private File findSDCardPath() {

    for (String sdPath : sdPaths) {
      File path = new File(sdPath);
      if (path.exists() && path.getTotalSpace() > 0) {
        return path;
      }
    }

    return null; // No sdcard?
  }
}