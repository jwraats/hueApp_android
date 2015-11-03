package nl.jackevers.jwraats.hueapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import java.util.ArrayList;
import java.util.Iterator;


/**
 * Created by jwraats on 02/11/15.
 * http://developer.android.com/intl/es/training/volley/index.html
 * http://developer.android.com/intl/es/training/volley/request.html#request-json
 * http://stackoverflow.com/questions/20059576/import-android-volley-to-android-studio
 */
public class HueRestfull {
    private static HueRestfull mInstance;
    private final String DISCOVER_URL = "https://www.meethue.com/api/nupnp";
    private String bridgeIp, bridgeToken, BridgeError;
    private ArrayList<HueLight> hueLights = new ArrayList<HueLight>();
    private RequestQueue mRequestQueue;
    private static Context context;
    private MainActivity activity;

    public HueRestfull(Context context, MainActivity activity){
        this.context = context;
        this.activity = activity;

        //Todo make settings page that can be reset.
        //JW Thuis
        /*this.bridgeIp = "192.168.178.42";
        this.bridgeToken = "3364ad21c15d37f147578a042f7f5f"; */
    }

    public static synchronized HueRestfull getInstance(Context context, MainActivity activity) {
        if (mInstance == null) {
            mInstance = new HueRestfull(context, activity);
        }
        mInstance.activity = activity;
        return mInstance;
    }
    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            // getApplicationContext() is key, it keeps you from leaking the
            // Activity or BroadcastReceiver if someone passes one in.
            mRequestQueue = Volley.newRequestQueue(context.getApplicationContext());
        }
        return mRequestQueue;
    }

    public void discoverBridge(){
        // Instantiate the RequestQueue.
        String url = this.DISCOVER_URL;
        //Following the android tutorial getting the error always fun: http://stackoverflow.com/questions/29247525/why-is-my-jsonobjectrequest-not-working
        JsonArrayRequest jsObjRequest = new JsonArrayRequest(Request.Method.GET, url, (String)null, new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        JSONObject json_data = null;
                        try {
                            json_data = response.getJSONObject(0);
                            bridgeIp = json_data.getString("internalipaddress");
                            requestToken();
                        } catch (JSONException e) {
                            System.out.println(e.toString());
                        }

                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        System.out.println("ERRROOR! "+error.toString());

                    }
                });
        // Add the request to the RequestQueue.
        this.getRequestQueue().add(jsObjRequest);
    }

    public void requestToken(){
        if(this.bridgeToken != null){
            getLights();
        }
        else if(this.bridgeIp != null) {
            JSONObject jsonObjectParameters = new JSONObject();
            try {
                jsonObjectParameters.put("devicetype", "HueHome#AndroidApp");
            } catch (JSONException e) {
                System.out.println("If this happends... WTF?");
            }
            JsonArrayRequest jsObjRequest = new JsonArrayRequest(Request.Method.POST, this.getApiUrl(), jsonObjectParameters.toString(), new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray response) {
                    JSONObject json_data = null;
                    try {
                        json_data = response.getJSONObject(0);
                        if(json_data.getJSONObject("error").getString("description").equals("link button not pressed")){
                            //Alert popup comes here
                            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                            builder.setTitle("Knop indrukken")
                                    .setMessage("Wilt u even de knop indrukken!")
                                    .setCancelable(false)
                                    .setNegativeButton("GEDAAN",new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            requestToken();
                                        }
                                    });
                            AlertDialog alert = builder.create();
                            alert.show();
                        }
                    } catch (JSONException tokenIsPressed) {
                        //Token Already exist?!
                        try {
                            json_data = response.getJSONObject(0).getJSONObject("success");
                            bridgeToken = json_data.getString("username");
                            System.out.println(bridgeToken);
                            getLights();

                        } catch (JSONException error) {
                            System.out.println("NU is het wel echt mis! "+ error.toString());
                        }
                    }

                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    System.out.println("FOUT! " + error.toString());

                }
            });
            // Add the request to the RequestQueue.
            this.getRequestQueue().add(jsObjRequest);
        }
    }

    public void updateLight(HueLight light){
        if(light != null && this.bridgeToken != null) {
            //Update the light
            JSONObject jsonObjectParameters = new JSONObject();
            try {
                jsonObjectParameters.put("on", light.switchLightOn);
                jsonObjectParameters.put("bri", (int)light.brightness.doubleValue());
                jsonObjectParameters.put("hue", (int)light.hue.doubleValue());
                if(light.sat != null) {
                    jsonObjectParameters.put("sat", (int) light.sat.doubleValue());
                }
            } catch (JSONException e) {
                System.out.println("If this happends... WTF?");
            }

            //Request
            JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.PUT, this.getApiUrlWithToken() + "lights/"+light.id+"/state", jsonObjectParameters.toString(), new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    //Als goed is doet hij het gewoon :)
                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    System.out.println("ERRROOR! " + error.toString());

                }
            });
            // Add the request to the RequestQueue.
            this.getRequestQueue().add(jsObjRequest);


        }
    }

    private void getLights(){
        if(this.bridgeToken != null) {
            JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, this.getApiUrlWithToken() + "lights", (String) null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        Iterator<String> iterator = response.keys();
                        while (iterator.hasNext()) {
                            String lightId = iterator.next();
                            HueLight hueLight = new HueLight();
                            hueLight.id = Integer.parseInt(lightId);
                            hueLight.lightName = response.getJSONObject(lightId).getString("name");
                            hueLight.switchLightOn = response.getJSONObject(lightId).getJSONObject("state").getBoolean("on");
                            if (response.getJSONObject(lightId).getString("modelid").equals("LCT001")) {
                                hueLight.sat = response.getJSONObject(lightId).getJSONObject("state").getDouble("sat");
                            }
                            hueLight.hue = response.getJSONObject(lightId).getJSONObject("state").getDouble("hue");
                            hueLight.brightness = response.getJSONObject(lightId).getJSONObject("state").getDouble("bri");
                            hueLights.add(hueLight);
                        }
                        //Done.. RENDER THIS SHIT!
                        activity.changeData(hueLights);

                    } catch (JSONException e) {
                        System.out.println(e.toString());
                    }

                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    System.out.println("ERRROOR! " + error.toString());

                }
            });
            // Add the request to the RequestQueue.
            this.getRequestQueue().add(jsObjRequest);
        }
    }



    public String getApiUrl(){
        return "http://"+this.bridgeIp+"/api/";
    }

    public String getApiUrlWithToken(){
        return this.getApiUrl()+bridgeToken+"/";
    }


}
