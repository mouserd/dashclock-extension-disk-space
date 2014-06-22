package com.pixelus.dashclock.ext.mystorage;

import android.os.Environment;
import android.os.StatFs;
import android.util.Log;
import com.google.android.apps.dashclock.api.DashClockExtension;
import com.google.android.apps.dashclock.api.ExtensionData;

import java.io.File;

import static java.lang.String.format;

public class MyStorageExtension extends DashClockExtension {

  public static final String TAG = MyStorageExtension.class.getName();

  private String[] sdPaths = {
      "/storage/extSdCard/",
      "/storage/sdcard1/",
      "/storage/usbcard1/",
      "/storage/sdcard0/"
  };

  @Override
  protected void onUpdateData(int i) {

    MyStorageStats intMyStorageStats = getMyStorageStats(Environment.getExternalStorageDirectory(), MyStorageStatsType.INTERNAL);
    long totalBytes = intMyStorageStats.getTotalBytes();
    long freeBytes = intMyStorageStats.getFreeBytes();

    Log.d(TAG, format("Internal Storage [total: %d, free: %d, free %%: %d]",
        intMyStorageStats.getTotalBytes(), intMyStorageStats.getFreeBytes(),
        intMyStorageStats.calculatePercentageFree()));

    String bodyExternal = "";
    File sdCardPath = findSDCardPath();
    Log.d(TAG, "SD Card Path: " + sdCardPath.getPath());
    if (sdCardPath != null) {

      MyStorageStats extMyStorageStats = getMyStorageStats(sdCardPath, MyStorageStatsType.EXTERNAL);
      // Make our best attempt to ensure that the path used for the internal storage isn't the same that
      // we've identified for our external storage.
      if (extMyStorageStats.getTotalBytes() != intMyStorageStats.getTotalBytes()) {
        totalBytes += extMyStorageStats.getTotalBytes();
        freeBytes += extMyStorageStats.getFreeBytes();

        Log.d(TAG, format("External Storage [total: %d, free: %d, free %%: %d]",
            extMyStorageStats.getTotalBytes(), extMyStorageStats.getFreeBytes(),
            extMyStorageStats.calculatePercentageFree()));

        bodyExternal = "\n" + extMyStorageStats.toString(this);
      }
    }

    int totalPercentageFree = (int) Math.ceil(((float) freeBytes / totalBytes) * 100);
    Log.d(TAG, format("Total Free: %d%%", totalPercentageFree));

    String bodyInternal = intMyStorageStats.toString(this);
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

  private MyStorageStats getMyStorageStats(File filePath, MyStorageStatsType myStorageStatsType) {

    final StatFs stat = new StatFs(filePath.getPath());
    long blockSize = stat.getBlockSize();

    return new MyStorageStats(myStorageStatsType)
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