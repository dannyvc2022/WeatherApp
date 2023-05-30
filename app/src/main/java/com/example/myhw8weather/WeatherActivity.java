package com.example.myhw8weather;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;

public class WeatherActivity extends AppCompatActivity {

    String city, country, lat, lon;
    String temp, feelTemp, minTemp, maxTemp;
    String humid, press, windSpeed, direction, descript;
    String dt;

    TextView cityView,  latLonView;;
    TextView tempView, minMaxTempView;
    TextView humPressView, windDirectionView, descriptView;

    GlobalClass globalClass;

    TextToSpeech t1;
    String strToRead;

    Button b1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

        globalClass = (GlobalClass)getApplicationContext();

        //Get the intent that started this activity and extract the string
        Intent intent = getIntent();
        String msg = globalClass.getWeatherResponse();

        cityView = findViewById(R.id.cityView);
        latLonView = findViewById(R.id.latlonView);
        tempView = findViewById(R.id.tempView);
        minMaxTempView = findViewById(R.id.minMaxTempView);
        humPressView = findViewById(R.id.humPressView);
        windDirectionView = findViewById(R.id.windDirectionView);
        descriptView = findViewById(R.id.descriptView);

        b1=(Button)findViewById(R.id.speech);

        displayResults(msg);

        t1=new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    t1.setLanguage(Locale.UK);
                }
            }
        });

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String toSpeak = strToRead;
                Toast.makeText(getApplicationContext(), toSpeak,Toast.LENGTH_SHORT).show();
                t1.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null);
            }
        });

        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.weatherbar);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.homebar:
                        Toast.makeText(WeatherActivity.this, "Home", Toast.LENGTH_SHORT).show();
                        showHome();
                        break;
                    case R.id.weatherbar:
                        Toast.makeText(WeatherActivity.this, "Weather", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.mapbar:
                        Toast.makeText(WeatherActivity.this, "Map", Toast.LENGTH_SHORT).show();
                        showMapbar();
                        break;
                    case R.id.historybar:
                        Toast.makeText(WeatherActivity.this, "History", Toast.LENGTH_SHORT).show();
                        showHistory();
                        break;}
                return true;
            }
        });
    }

    public void onPause(){
        if(t1 !=null){
            t1.stop();
            t1.shutdown();
        }
        super.onPause();
    }

    private void displayResults(String weatherResult) {
        System.out.println(weatherResult);
        try {
            JSONObject jsonObject = new JSONObject(weatherResult);
            city = jsonObject.getString("name");  globalClass.setCity(city); //set city for other activities
            JSONObject sys = jsonObject.getJSONObject("sys");
            country = sys.getString("country");    globalClass.setCountry(country); //set country for other activities
            dt = jsonObject.getString("dt");  globalClass.setDt(dt);   //set current dt for history call

            JSONObject coord = jsonObject.getJSONObject("coord");
            lat = coord.getString("lat");   globalClass.setLatitude(lat);  //set lat for other activities
            lon = coord.getString("lon");   globalClass.setLongitude(lon);  //set lon for other activities

            JSONObject main = jsonObject.getJSONObject("main");
            temp = main.getString("temp");
            feelTemp = main.getString("feels_like");
            minTemp = main.getString("temp_min");
            maxTemp = main.getString("temp_max");
            humid = main.getString("humidity");
            press = main.getString("pressure");

            JSONObject wind = jsonObject.getJSONObject("wind");
            windSpeed = wind.getString("speed");
            direction = wind.getString("deg");

            JSONArray weather = jsonObject.getJSONArray("weather");
            JSONObject w = weather.getJSONObject(0);
            descript = w.getString("description");

        } catch (JSONException err) {
            Log.d("Error", err.toString());
        }
        cityView.setText("Weather for " + city + ", " + country);
        latLonView.setText("Lon: " + lon + "\u00B0"  + " Lat: " + lat + "\u00B0");
        tempView.setText("Temperature: " + temp + "F" + " -- Feels like: " + feelTemp +"F");
        minMaxTempView.setText("Min Temp: " + minTemp + "F -- Max Temp: " + maxTemp + "F");
        humPressView.setText("Humidity: " + humid + "% -- Pressure: " + press + "mB");
        windDirectionView.setText("Wind Speed: " + windSpeed + "MPH -- Wind Directions: " + direction + "\u00B0");
        descriptView.setText("Weather Description: " + descript);

        strToRead = "" + cityView.getText().toString() + ", Temperature: " + temp + " Fahrenheit," + " Feels like: " + feelTemp +"Fahrenheit, " +
                "Minimum Temperature: " + minTemp + " Fahrenheit, Maximum Temperature: " + maxTemp + " Fahrenheit, " + "Humidity: " + humid + "%"
                + " , Wind Speed: " + windSpeed + " Miles per hour, "+ descriptView.getText().toString() ;
    }

    public void showHome () {
        //do something in response to button
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void showMapbar () {
        //do something in response to button
        Intent intent = new Intent(this, GoogleMapActivity.class);
        startActivity(intent);
    }

    public void showHistory () {
        //do something in response to button
        Intent intent = new Intent(this, HistoryActivity.class);
        startActivity(intent);
    }

}
