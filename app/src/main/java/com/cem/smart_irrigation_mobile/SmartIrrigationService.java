package com.cem.smart_irrigation_mobile;

import android.content.Context;
import android.text.TextUtils;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

public class SmartIrrigationService {

    private final String SMART_IRRIGATION_BASE_URL = "10.0.2.2:7373";
    public  RequestQueue queue;

    public  void init(Context context) {
        queue = Volley.newRequestQueue(context);
    }

    public void getPlants() {
        System.out.println("deneme");
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, SMART_IRRIGATION_BASE_URL + "/plant/2/irrigation/", null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        System.out.println("Response: " + response.toString());
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Handle error

                    }
                });

    }


}
