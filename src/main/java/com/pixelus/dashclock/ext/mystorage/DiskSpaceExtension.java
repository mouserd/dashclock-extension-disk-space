package com.pixelus.dashclock.ext.mystorage;

import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.StatFs;
import android.preference.PreferenceManager;
import android.util.Log;
import com.crashlytics.android.Crashlytics;
import com.google.android.apps.dashclock.api.DashClockExtension;
import com.google.android.apps.dashclock.api.ExtensionData;

import java.io.File;

import static android.content.Intent.ACTION_MAIN;
import static android.content.Intent.CATEGORY_LAUNCHER;
import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static android.text.format.Formatter.formatShortFileSize;
import static com.pixelus.dashclock.ext.mystorage.DiskSpaceStatsType.EXTERNAL;
import static com.pixelus.dashclock.ext.mystorage.DiskSpaceStatsType.INTERNAL;
import static java.lang.String.format;

public class DiskSpaceExtension extends DashClockExtension {

  public static final String TAG = DiskSpaceExtension.class.getSimpleName();
  public static final String PREF_CLICK_INTENT_APPLICATION = "pref_click_intent_shortcut";

  private File detectedSDCardPath = null;

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
  public void onCreate() {
    super.onCreate();
    Crashlytics.start(this);
  }

  @Override
  protected void onUpdateData(final int i) {

    final DiskSpaceStats intDiskSpaceStats = getMyStorageStats(getFilesDir(), INTERNAL);
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

    final int totalPercentageFree = (int) Math.ceil(((float) freeBytes / totalBytes) * 100);
    Log.d(TAG, format("Total Free: %d%%", totalPercentageFree));

    final String title = getString(R.string.extension_expanded_title, totalPercentageFree);
    final String status = getString(R.string.extension_status, totalPercentageFree, formatShortFileSize(this, freeBytes));

    publishUpdate(new ExtensionData()
            .visible(true)
            .icon(R.drawable.ic_launcher)
            .status(status)
            .expandedTitle(title)
            .expandedBody(bodyInternal + bodyExternal)
            .clickIntent(createClickIntent())
    );
  }

  private DiskSpaceStats getMyStorageStats(final File filePath, final DiskSpaceStatsType diskSpaceStatsType) {

    final StatFs stat = new StatFs(filePath.getPath());
    final long blockSize = stat.getBlockSize();

    return new DiskSpaceStats(diskSpaceStatsType)
        .withFreeBytes(blockSize * stat.getAvailableBlocks())
        .withTotalBytes(blockSize * stat.getBlockCount());
  }

  private File findSDCardPath() {

    if (detectedSDCardPath != null) {
      return detectedSDCardPath;
    }

    for (String sdPath : sdPaths) {
      final File path = new File(sdPath);
      if (path.exists() && path.getTotalSpace() > 0) {
        detectedSDCardPath = path;
        return path;
      }
    }

    return null; // No sdcard?
  }

  public Intent createClickIntent() {

    final SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
    final String intentPackageName = sp.getString(PREF_CLICK_INTENT_APPLICATION, "");
    if (intentPackageName.isEmpty()) {
      return null;
    }

    final Intent intent = new Intent(ACTION_MAIN);
    intent.addCategory(CATEGORY_LAUNCHER);
    intent.setComponent(ComponentName.unflattenFromString(intentPackageName));
    intent.setFlags(FLAG_ACTIVITY_NEW_TASK);

    return intent;
  }
}