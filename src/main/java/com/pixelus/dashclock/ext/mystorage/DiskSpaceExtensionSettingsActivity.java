package com.pixelus.dashclock.ext.mystorage;

import android.os.Bundle;
import com.crashlytics.android.Crashlytics;
import com.pixelus.android.activity.SharedPreferenceActivity;
import io.fabric.sdk.android.Fabric;

public class DiskSpaceExtensionSettingsActivity
    extends SharedPreferenceActivity {

  @Override
  public int getIconResourceId() {
    return R.drawable.ic_launcher;
  }

  @Override
  public int getPreferencesXmlResourceId() {
    return R.xml.preferences;
  }

  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    Fabric.with(this, new Crashlytics());
  }
}