package com.example.myhw8weather;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private final int REQ_CODE = 100;

    String weatherResult;
    TextView errorView;
    Intent intent;
    //GPS code
    Button btnShowLocation;
    Button speakBtn;
    private static final int REQUEST_CODE_PERMISSION = 2;
    String mPermission = Manifest.permission.ACCESS_FINE_LOCATION;

    // GPSTracker class
    GPSTracker gps;

    GlobalClass globalClass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        globalClass = (GlobalClass)getApplicationContext();

        errorView = (TextView) findViewById(R.id.errorView);
        //Hide the results area
        errorView.setVisibility(View.INVISIBLE);

        //GPS code
        try {
            if (ActivityCompat.checkSelfPermission(this, mPermission)
                    != PackageManager.PERMISSION_GRANTED) {   //MockPackageManager

                ActivityCompat.requestPermissions(this, new String[]{mPermission},
                        REQUEST_CODE_PERMISSION);

                // If any permission above not allowed by user, this condition will execute every time, else your else part will work
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        btnShowLocation = (Button) findViewById(R.id.gpsButton);
        // show location button click event
        btnShowLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // create class object
                gps = new GPSTracker(MainActivity.this);

                // check if GPS enabled
                if(gps.canGetLocation()){

                    double latitude = gps.getLatitude();
                    double longitude = gps.getLongitude();

                    getResultsGPS(latitude,longitude );  //Call GPS function

                }else{
                    // can't get location
                    // GPS or Network is not enabled
                    // Ask user to enable GPS/network in settings
                    gps.showSettingsAlert();
                }
            }
        });

        speakBtn = (Button) findViewById(R.id.voice);
        speakBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                        RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
                intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Need to speak");
                try {
                    startActivityForResult(intent, REQ_CODE);
                } catch (ActivityNotFoundException a) {
                    Toast.makeText(getApplicationContext(),
                            "Sorry your device not supported",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
             @Override
             public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                 switch (item.getItemId()) {
                     case R.id.homebar:
                         Toast.makeText(MainActivity.this, "Home", Toast.LENGTH_SHORT).show();
                         break;
                     case R.id.weatherbar:
                         Toast.makeText(MainActivity.this, "Please Search for a location first", Toast.LENGTH_SHORT).show();
                         break;
                     case R.id.mapbar:
                         Toast.makeText(MainActivity.this, "Please Search for a location first", Toast.LENGTH_SHORT).show();
                         break;
                     case R.id.historybar:
                         Toast.makeText(MainActivity.this, "Please Search for a location first", Toast.LENGTH_SHORT).show();
                         break;}
                 return true;
             }
        });

    } ///end of onCreate

    // Called when the user taps the search button
    public void sendMessage(View view){
        //do something in response to button
        intent = new Intent (this, WeatherActivity.class);
        EditText editText = (EditText) findViewById(R.id.userInput);
        String message = editText.getText().toString();
        getResults(message);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        intent = new Intent (this, WeatherActivity.class);
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQ_CODE: {
                if (resultCode == RESULT_OK && null != data) {
                    ArrayList result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    EditText editText = (EditText) findViewById(R.id.userInput);
                    editText.setText((CharSequence) result.get(0));
                    String message = editText.getText().toString();
                    getResults(message);
                }
                break;
            }
        }
    }

    private void getResults (String userInput) {
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);
        String WEATHER_API_KEY = "";
        String url;

        try { //check if input was a zip code by using parse
            Integer.parseInt(userInput);
            url = "https://api.openweathermap.org/data/2.5/weather?zip=" + userInput + "&appid=" + WEATHER_API_KEY + "&units=imperial";        // Request a string response from the provided URL.

        } catch(NumberFormatException e){   // if parse fails then it was a city or state name
            url = "https://api.openweathermap.org/data/2.5/weather?q=" + userInput + "&appid=" + WEATHER_API_KEY + "&units=imperial";        // Request a string response from the provided URL.
        }

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
//                        textView.setText("Response is: " + response);
                        System.out.println(response);
                        weatherResult = response;
                        globalClass.setWeatherResponse(weatherResult);
                        startActivity(intent);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                errorView.setVisibility(View.VISIBLE);
                errorView.setText("Please try a valid city or Zip!");
            }
        });
        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    private void getResultsGPS (Double latitude, Double longitude) {
        intent = new Intent (this, WeatherActivity.class);
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);
        String WEATHER_API_KEY = "";
        String url = "https://api.openweathermap.org/data/2.5/weather?lat=" + latitude + "&lon=" + longitude + "&appid=" + WEATHER_API_KEY + "&units=imperial";       // Request a string response from the provided URL.;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
//                        textView.setText("Response is: " + response);
                        System.out.println(response);
                        weatherResult = response;
                        globalClass.setWeatherResponse(weatherResult);
                        startActivity(intent);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                errorView.setVisibility(View.VISIBLE);
                errorView.setText("GPS Error");
            }
        });
        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }
}
