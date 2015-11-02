package nl.jackevers.jwraats.hueapp;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.android.volley.toolbox.Volley;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;


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
    private HueLight huelight;
    private RequestQueue mRequestQueue;
    private static Context context;

    public HueRestfull(Context context){
        this.context = context;
    }

    public static synchronized HueRestfull getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new HueRestfull(context);
        }
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
                        } catch (JSONException e) {
                            e.printStackTrace();
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
        //ToDo
    }

    public String getApiUrl(){
        return "http://"+this.bridgeIp+"/api/";
    }

    public String getApiUrlWithToken(){
        return this.getApiUrl()+bridgeToken+"/";
    }


}
