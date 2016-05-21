package com.arcgis.androidsupportcases.dcgeotrigger;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import com.esri.android.geotrigger.GeotriggerApiClient;
import com.esri.android.geotrigger.GeotriggerApiListener;
import com.esri.android.geotrigger.GeotriggerBroadcastReceiver;
import com.esri.android.geotrigger.GeotriggerService;
import com.esri.android.geotrigger.internal.util.GeotriggerUtils;
import com.esri.android.runtime.ArcGISRuntime;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity implements
    GeotriggerBroadcastReceiver.ReadyListener {

  GeotriggerBroadcastReceiver mGeotriggerBroadcastReceiver;

  final static int MY_FINE_LOCATION = 7335;
  private static final String[] TAGS = new String[] {"nohe"};
  final String TAG = "Nohe";

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    ArcGISRuntime.setClientId(getResources().getString(R.string.ArcGISClientID));

    setContentView(R.layout.activity_main);

    if (ContextCompat.checkSelfPermission(this,
        Manifest.permission.ACCESS_FINE_LOCATION)
        != PackageManager.PERMISSION_GRANTED)
    {
      ActivityCompat.requestPermissions(this,
          new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
          MY_FINE_LOCATION);
    }

    Log.e(TAG, getResources().getString(R.string.ArcGISClientID));




  }

  @Override
  public void onRequestPermissionsResult(int requestCode,
      String permissions[], int[] grantResults) {
    switch (requestCode) {
      case MY_FINE_LOCATION: {
        // If request is cancelled, the result arrays are empty.
        if (grantResults.length > 0
            && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

          mGeotriggerBroadcastReceiver = new GeotriggerBroadcastReceiver();
          GeotriggerService.init(
              getApplicationContext(),
              getResources().getString(R.string.ArcGISClientID),
              getResources().getString(R.string.ProjectNumber),
              TAGS,
              GeotriggerService.TRACKING_PROFILE_ADAPTIVE
          );

          registerReceiver(mGeotriggerBroadcastReceiver, GeotriggerBroadcastReceiver.getDefaultIntentFilter());

          GeotriggerService.setPushNotificationHandlingEnabled(getApplicationContext(), false);

          // permission was granted, yay! Do the
          // contacts-related task you need to do.

        } else {

          Toast.makeText(getApplicationContext(), "Location required to use this app",
              Toast.LENGTH_LONG).show();

          Intent intent = new Intent(Intent.ACTION_MAIN);
          intent.addCategory(Intent.CATEGORY_HOME);
          intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
          startActivity(intent);

          // permission denied, boo! Disable the
          // functionality that depends on this permission.
        }
        return;
      }

      // other 'case' lines to check for other
      // permissions this app might request
    }
  }

  @Override
  protected void onResume() {
    super.onResume();
    // Register the receiver. The default intent filter listens for all
    // intents that the receiver can handle. If you need to handle events
    // while the app is in the background, you must register the receiver
    // in the manifest.
    if(mGeotriggerBroadcastReceiver != null) {
      registerReceiver(mGeotriggerBroadcastReceiver, GeotriggerBroadcastReceiver.getDefaultIntentFilter());
    }
  }

  @Override
  protected void onPause() {
    super.onPause();
    if(mGeotriggerBroadcastReceiver != null) {
      unregisterReceiver(mGeotriggerBroadcastReceiver);
    }
  }

  @Override public void onReady() {
    Log.e("NOHE", "DEVICE READY");
    JSONObject params = new JSONObject();
    try {
      params.put("text", "Push notifications are working!");
      params.put("url", "http://developers.arcgis.com");
      addTag();


    } catch (JSONException e) {
      Log.e(TAG, "Error creating device/notify params", e);
    }
  }

  public void addTag() {
    JSONObject params = new JSONObject();
    try {
      params.put("addTags", "dctours");
      //runTrigger();
    } catch (JSONException e) {
      Log.e(TAG, "Error creating device update parameters.", e);
    }

    GeotriggerApiClient.runRequest(getApplicationContext(), "device/update", params, new GeotriggerApiListener() {
      @Override
      public void onSuccess(JSONObject data) {
        Log.d(TAG, "Device updated: " + data.toString());
      }

      @Override
      public void onFailure(Throwable error) {
        Log.d(TAG, "Failed to update device.", error);
      }
    });
  }

  public void runTrigger() {
    JSONObject params = new JSONObject();
    try {
      params.put("triggerIds", "13a4143c6be35f41fafe537c40547220");
    } catch (JSONException e) {
      Log.e(TAG, "Error testing trigger with trigger/run.", e);
    }

    GeotriggerApiClient.runRequest(getApplicationContext(), "trigger/run", params, new GeotriggerApiListener() {
      @Override
      public void onSuccess(JSONObject data) {
        Log.d(TAG, "Trigger run successful: " + data.toString());
      }

      @Override
      public void onFailure(Throwable error) {
        Log.d(TAG, "Failed to run trigger.", error);
      }
    });
  }

}
