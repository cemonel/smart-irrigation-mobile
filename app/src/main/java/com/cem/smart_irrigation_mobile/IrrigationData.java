package com.cem.smart_irrigation_mobile;

import com.google.gson.annotations.SerializedName;

public class IrrigationData {

    @SerializedName("air_temperature")
    float airTemperature;

    @SerializedName("air_humidity")
    float airHumidity;

    @SerializedName("soil_moisture")
    float soilMoisture;

    @SerializedName("date")
    int date;
}
