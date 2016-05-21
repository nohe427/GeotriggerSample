package com.arcgis.androidsupportcases.dcgeotrigger;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.widget.TextView;

import com.esri.android.map.MapOptions;
import com.esri.android.map.MapView;
import com.esri.android.map.event.OnStatusChangedListener;
import com.esri.core.geometry.GeometryEngine;
import com.esri.core.geometry.Point;
import com.esri.core.geometry.SpatialReference;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by alex7370 on 5/11/2016.
 */
public class NotificationActivity extends AppCompatActivity {

  MapView mapView;
  double x;
  double y;
  TextView titleText;
  TextView moreInfoText;
  TextView descText;
  JSONObject obj;

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.notificationview);
    try {
      obj = new JSONObject(((Bundle)getIntent().getExtras().get("locationData")).get("data").toString());
      Log.e("NOHE", obj.toString());
      Log.e("NOHE", obj.getString("x"));
      x = Float.parseFloat(obj.getString("x"));
      y = Float.parseFloat(obj.getString("y"));
    } catch (JSONException e) {
      e.printStackTrace();
    }

    mapView = (MapView)findViewById(R.id.mapView);
    titleText = (TextView)findViewById(R.id.title);
    moreInfoText = (TextView)findViewById(R.id.moreInfo);
    descText = (TextView)findViewById(R.id.description);

    try {
      titleText.setText(obj.getString("name"));
      descText.setText(obj.getString("desc"));
      moreInfoText.setMovementMethod(LinkMovementMethod.getInstance());
      moreInfoText.setText(Html.fromHtml("<a href="+obj.getString("link")+">More Information</a>"));
    } catch (JSONException e) {
      e.printStackTrace();
    }


    mapView.setOnStatusChangedListener(new OnStatusChangedListener() {
      @Override public void onStatusChanged(Object o, STATUS status) {
        if (status == STATUS.INITIALIZED) {
          Point newPoint = (Point) GeometryEngine.project(new Point(y,x), SpatialReference.create(4326), mapView.getSpatialReference());

          mapView.zoomToResolution(newPoint, .25);
        }
      }
    });




  }
}
