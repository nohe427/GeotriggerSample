/*
   For step-by-step instructions on connecting your Android application to this backend module,
   see "App Engine Java Endpoints Module" template documentation at
   https://github.com/GoogleCloudPlatform/gradle-appengine-templates/tree/master/HelloEndpoints
*/

package com.arcgis.androidsupportcases.angelhacks;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;

import com.google.appengine.repackaged.com.google.gson.Gson;
import com.google.appengine.repackaged.com.google.gson.JsonArray;
import com.google.appengine.repackaged.com.google.gson.JsonObject;
import com.google.appengine.repackaged.com.google.gson.JsonParser;
import javax.inject.Named;

/** An endpoint class we are exposing */
@Api(
  name = "myApi",
  version = "v1",
  namespace = @ApiNamespace(
    ownerDomain = "angelhacks.androidsupportcases.arcgis.com",
    ownerName = "angelhacks.androidsupportcases.arcgis.com",
    packagePath=""
  )
)
public class MyEndpoint {

    /** A simple endpoint method that takes a name and says Hi back */
    @ApiMethod(name = "sayHi")
    public MyBean sayHi(@Named("name") String name) {
        MyBean response = new MyBean();
        response.setData("Hi, " + name);

        return response;
    }

    @ApiMethod(name = "updateFeature", httpMethod = ApiMethod.HttpMethod.POST)
    public void updateFeature(Object objectSent) {
        JsonParser jsonParser = new JsonParser();
        String newObj = objectSent.toString();

        String jsonString = new Gson().toJson(objectSent);


        //JsonObject o = gson.fromJson(objectSent.toString(), JsonObject.class);
        JsonObject o = jsonParser.parse(jsonString).getAsJsonObject();

        JsonObject x = o.get("trigger").getAsJsonObject();

        int objId = x.get("action").getAsJsonObject().get("notification").getAsJsonObject().get("data").getAsJsonObject().get("featureId").getAsInt();

        String featureService = "featureServiceURL";
        String dataToPost = "where=1%3D1"
            + "&objectIds=" + objId
            + "&returnIdsOnly=false"
            + "&outFields=\"*\""
            + "&returnCountOnly=false"
            + "&f=pjson";

        String returnFromPost = MyBean.excutePost(featureService, dataToPost);
        JsonObject rPo = jsonParser.parse(returnFromPost).getAsJsonObject();
        JsonArray features = rPo.get("features").getAsJsonArray();
        int count = features.get(0).getAsJsonObject().get("attributes").getAsJsonObject().get("Count").getAsInt();
        count++;
        String featureServiceAdd = "featureServiceURL/applyEdits";
        String dataToUpdate = "f=json"
            + "&updates=%5B%7B%22attributes%22%3A%7B%22OBJECTID%22%3A"+ objId +"%2C%22Count%22%3A"+ count +"%7D%7D%5D";
        MyBean.excutePost(featureServiceAdd, dataToUpdate);

    }

}
