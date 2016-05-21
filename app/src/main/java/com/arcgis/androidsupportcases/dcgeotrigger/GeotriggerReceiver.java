package com.arcgis.androidsupportcases.dcgeotrigger;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import com.esri.android.geotrigger.GeotriggerBroadcastReceiver;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by alex7370 on 5/11/2016.
 */
public class GeotriggerReceiver extends GeotriggerBroadcastReceiver {
  @Override protected void onPushMessage(Context context, Bundle data) {
    super.onPushMessage(context, data);
    String defaultDesc = "New location";

    // The notification Bundle has these keys: 'text', 'url', 'sound', 'icon', 'data'  (DATA)

    try {
      JSONObject obj = new JSONObject(data.get("data").toString());
      defaultDesc = obj.getString("name");
    } catch (JSONException e) {
      e.printStackTrace();
    }
    NotificationCompat.Builder mBuilder =
        new NotificationCompat.Builder(context)
            .setSmallIcon(R.drawable.ic_my_push)
            .setContentTitle("DC Self Tour")
            .setAutoCancel(true)
            .setContentText(defaultDesc);
    Intent resultIntent = new Intent(context, NotificationActivity.class);
    Log.e("NOHE", String.valueOf(data.get("data")));
    resultIntent.putExtra("locationData", data);
    TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
    stackBuilder.addParentStack(NotificationActivity.class);
    stackBuilder.addNextIntent(resultIntent);
    PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0,
        PendingIntent.FLAG_UPDATE_CURRENT);
    mBuilder.setContentIntent(resultPendingIntent);
    NotificationManager notificationManager =
        (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    notificationManager.notify(1, mBuilder.build());
  }
}
