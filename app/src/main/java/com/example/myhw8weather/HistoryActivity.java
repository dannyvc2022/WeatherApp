package com.example.myhw8weather;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class HistoryActivity extends AppCompatActivity {
    int dt;
    String city, country;
    String lat, lon;
    String temp;
    String humid, descript, sunrise, sunset;
    private String TAG = MainActivity.class.getSimpleName();
    private ListView lv;

    ArrayList<HashMap<String, String>> historyList;

    GlobalClass globalClass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        globalClass = (GlobalClass)getApplicationContext();

        dt =  Integer.parseInt(globalClass.getDt());
        dt = dt - 3600; //subtract an hour
        city = globalClass.getCity();
        country = globalClass.getCountry();
        lat = globalClass.getLatitude();
        lon = globalClass.getLongitude();


        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.historybar);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.homebar:
                        Toast.makeText(HistoryActivity.this, "Home", Toast.LENGTH_SHORT).show();
                        showHome();
                        break;
                    case R.id.weatherbar:
                        Toast.makeText(HistoryActivity.this, "Weather", Toast.LENGTH_SHORT).show();
                        showWeather();
                        break;
                    case R.id.mapbar:
                        Toast.makeText(HistoryActivity.this, "Map", Toast.LENGTH_SHORT).show();
                        showMapbar();
                        break;
                    case R.id.historybar:
                        Toast.makeText(HistoryActivity.this, "History", Toast.LENGTH_SHORT).show();

                        break;}
                return true;
            }
        });

        historyList = new ArrayList<>();
        lv = (ListView) findViewById(R.id.list);

        new GetHistory().execute();
    }//end of onCreate

    public void showHome () {
        //do something in response to button
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void showWeather () {
        //do something in response to button
        Intent intent = new Intent(this, WeatherActivity.class);
        startActivity(intent);
    }

    public void showMapbar () {
        //do something in response to button
        Intent intent = new Intent(this, GoogleMapActivity.class);
        startActivity(intent);
    }

    private class GetHistory extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Toast.makeText(HistoryActivity.this,"Json Data is downloading",Toast.LENGTH_LONG).show();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();
            // Making a request to url and getting response
            String WEATHER_API_KEY = "";
            String url;

            if (0 == 0) {
                try {
                    // looping through 5 days
                    for (int i = 0; i < 5; i++) {
                        url = "https://api.openweathermap.org/data/2.5/onecall/timemachine?lat=" + lat + "&lon="  + lon + "&dt=" + dt + "&appid=" + WEATHER_API_KEY + "&units=imperial";

                        String jsonStr = sh.makeServiceCall(url);

                        JSONObject jsonObject = new JSONObject(jsonStr);

                        long unixSeconds = dt;
                        Date date = new java.util.Date(unixSeconds*1000L); // convert seconds to milliseconds
                        SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd"); // the format of your date
                        sdf.setTimeZone(java.util.TimeZone.getTimeZone("GMT-4")); // give a timezone reference for formatting (see comment at the bottom)
                        String formattedDate = sdf.format(date);

                        JSONObject current = jsonObject.getJSONObject("current");
                        temp = current.getString("temp");
                        humid = current.getString("humidity");
                        sunrise = current.getString("sunrise");
                        int sr = Integer.parseInt(sunrise);
                        Date srTime = new java.util.Date(sr*1000L);
                        SimpleDateFormat time = new java.text.SimpleDateFormat("h:mm a");
                        String formattedSrTime = time.format(srTime);
                        sunset = current.getString("sunset");
                        int ss = Integer.parseInt(sunset);
                        Date ssTime = new java.util.Date(ss*1000L);
                        String formattedSsTime = time.format(ssTime);

                        JSONArray weather = current.getJSONArray("weather");
                        JSONObject w = weather.getJSONObject(0);
                        descript = w.getString("description");

                        // tmp hash map for single contact
                        HashMap<String, String> history = new HashMap<>();

                        // adding each child node to HashMap key => value
                        history.put("date", "Date: " + formattedDate);
                        history.put("temp", "Temp: " + temp + "F" + "           " + "Humidity: " + humid + "%");
                        history.put("sunrise", "Sunrise: " + formattedSrTime  + "       " + "Sunset: " + formattedSsTime);
                        history.put("descript", "Description: " + descript);

                        // adding contact to history list
                        historyList.add(history);

                        dt = dt - 86400; //subtract a day for the next API call
                    }
                } catch (final JSONException e) {
                    Log.e(TAG, "Json parsing error: " + e.getMessage());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),
                                    "Json parsing error: " + e.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    });
                }

            } else {
                Log.e(TAG, "Couldn't get json from server.");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),
                                "Couldn't get json from server. Check LogCat for possible errors!",
                                Toast.LENGTH_LONG).show();
                    }
                });
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            ListAdapter adapter = new SimpleAdapter(HistoryActivity.this, historyList,
                    R.layout.list_item, new String[]{ "date","temp", "sunrise", "descript"},
                    new int[]{R.id.date, R.id.temp, R.id.sunrise, R.id.descript});
            lv.setAdapter(adapter);
        }
    }




}