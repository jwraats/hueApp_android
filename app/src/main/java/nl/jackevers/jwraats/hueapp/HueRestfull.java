package nl.jackevers.jwraats.hueapp;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;


/**
 * Created by jwraats on 02/11/15.
 * http://developer.android.com/intl/es/training/volley/index.html
 * http://developer.android.com/intl/es/training/volley/simple.html
 * http://stackoverflow.com/questions/20059576/import-android-volley-to-android-studio
 */
public class HueRestfull {
    private String discoveryURL = "https://www.meethue.com/api/nupnp";
    private String bridgeIp, bridgeToken, BridgeError;
    private HueLight huelight;

    public HueRestfull(){

    }

    public boolean discoverBridge(){
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);
        String url ="http://www.google.com";

// Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        System.out.println(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("ERROR:! "+ error.toString());
            }
        });
// Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    public String getApiUrl(){
        return "http://"+this.bridgeIp+"/api/";
    }

    public String getApiUrlWithToken(){
        return this.getApiUrl()+bridgeToken+"/";
    }


}
