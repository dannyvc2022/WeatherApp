package com.example.myhw8weather;

import android.app.Application;

public class GlobalClass extends Application {
    private String weatherResponse;
    private String latitude, longitude, city, country, dt;

    public String getWeatherResponse() {
        return weatherResponse;
    }

    public void setWeatherResponse(String weatherResponse) {
        this.weatherResponse = weatherResponse;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getDt() {
        return dt;
    }

    public void setDt(String dt) {
        this.dt = dt;
    }
}
