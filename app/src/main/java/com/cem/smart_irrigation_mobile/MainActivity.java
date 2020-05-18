package com.cem.smart_irrigation_mobile;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.LineData;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    public JSONObject response;
    public JSONArray plant_list_response;
    Spinner spinner;

    String URL = "http://192.168.1.100:7373";

    int plantID = 1;
    String plantName;

    float currentSoilMoisture;
    float currentAirHumidty;
    float currentAirTemperature;

    float maxSoilMoisture;
    float maxAirHumidty;
    float maxAirTemperature;

    float minSoilMoisture;
    float minAirHumidty;
    float minAirTemperature;

    float avgSoilMoisture;
    float avgAirHumidty;
    float avgAirTemperature;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final SmartIrrigationService smartIrrigationService = new SmartIrrigationService();
        setContentView(R.layout.activity_main);
        spinner = findViewById(R.id.spinner);
        final RequestQueue queue = Volley.newRequestQueue(this);


        final Gson gson = new Gson();


        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL + "/plant/list/",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        System.out.println("Bitkiler geldi");
                        try {
                            JSONArray jsonObject = new JSONArray(response);
                            setSpinnerArray(jsonObject);
                        }
                        catch (Exception e){
                            System.out.print(e.getMessage());

                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println(error.getMessage());
            }
        });

        queue.add(stringRequest);

        spinner.setOnItemSelectedListener(this);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Irrigation is in progress..", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                StringRequest stringRequest = new StringRequest(Request.Method.GET, URL + "/plant/" + plantID +"/irrigate/",
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        System.out.println(error.getMessage());
                    }
                });
                System.out.println("cem");
                queue.add(stringRequest);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void setResponse(JSONObject response){

        this.response = response;

    }

    public void setSpinnerArray(JSONArray response){

        this.plant_list_response= response;
        Plant[] plants = new Plant[response.length()];

        for(int i = 0 ; i < response.length(); i++){
            try {
                JSONObject rec = response.getJSONObject(i);
                int id = rec.getInt("id");
                String name = rec.getString("name");
                plants[i] = new Plant(name, id);
            }
            catch (Exception e){

                System.err.print(e);
            }

        }

        ArrayAdapter<Plant> spinnerArrayAdapter = new ArrayAdapter<Plant>(this, android.R.layout.simple_spinner_item, plants);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerArrayAdapter);
    }

    public void onItemSelected(AdapterView<?> parent, View view,
                               int pos, long id) {
        final RequestQueue plants_queue = Volley.newRequestQueue(this);

        Plant plant = (Plant) parent.getItemAtPosition(pos);
        StringRequest plantDetailRequest = new StringRequest(Request.Method.GET, URL + "/plant/" + plant.id + "/detail/",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        System.out.println("Plant detail geldi");
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            plantID = jsonObject.getInt("id");
                            TextView plantName = findViewById(R.id.plantName);
                            TextView temperatureText = findViewById(R.id.temperature_text);
                            plantName.setText(String.format("%s  Details", jsonObject.getString("name").toUpperCase()));
                            temperatureText.setText(jsonObject.getString("current_temperature") + "°C");
                            TextView humidityText = findViewById(R.id.humidity_text);
                            humidityText.setText(String.format("%s %%", jsonObject.getString("current_air_humidity")));
                            TextView soilText = findViewById(R.id.soil_moisture_text);
                            soilText.setText(String.format("%s", jsonObject.getString("current_soil_moisture")));

                        }
                        catch (Exception e){
                            System.out.println(e.getMessage());


                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println(error.getMessage());
            }
        });

        StringRequest plantDataRequest = new StringRequest(Request.Method.GET, URL + "/plant/" + plant.id + "/data-detail/",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        System.out.println("Plant data detail geldi");
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            LineChart lineChart = findViewById(R.id.lineChart);
                            List<LineData> lineData = new ArrayList<>();

                            System.out.println(jsonArray);

                        }
                        catch (Exception e){
                            System.out.println(e.getMessage());


                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println(error.getMessage());
            }
        });

        plants_queue.add(plantDetailRequest);
        plants_queue.add(plantDataRequest);
    }

    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }

    public void setCurrentSoilMoisture(float value){
        this.currentSoilMoisture = value;
    }

    public void setCurrentAirHumidty(float value){
        this.currentAirHumidty = value;

    }

    public void setCurrentAirTemperature(float value){
        this.currentAirTemperature = value;

    }

    public void setMaxSoilMoisture(float value){
        this.maxSoilMoisture = value;

    }

    public void setMaxAirHumidty(float value){
        this.maxAirHumidty = value;

    }

    public void setMaxAirTemperature(float value){
        this.maxAirTemperature = value;

    }

    public void setMinSoilMoisture(float value){
        this.minSoilMoisture = value;

    }

    public void setMinAirHumidty(float value){
        this.minAirHumidty = value;

    }

    public void setMinAirTemperature(float value){
        this.minAirTemperature = value;

    }

    public void setAvgSoilMoisture(float value){
        this.avgSoilMoisture = value;

    }

    public void setAvgAirHumidty(float value){
        this.avgAirHumidty = value;

    }

    public void setAvgAirTemperature(float value){
        this.avgAirTemperature = value;

    }
}

