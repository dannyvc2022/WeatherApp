package com.example.myhw8weather;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.android.gms.maps.model.TileProvider;
import com.google.android.gms.maps.model.UrlTileProvider;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.net.MalformedURLException;
import java.net.URL;

public class GoogleMapActivity extends AppCompatActivity implements OnMapReadyCallback {

    String city, country;
    Double latMap, lonMap;
    TextView cityView;
    String dt;

    GlobalClass globalClass;

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_map);

        globalClass = (GlobalClass)getApplicationContext();

        city = globalClass.getCity();
        country = globalClass.getCountry();
        latMap = Double.parseDouble(globalClass.getLatitude());
        lonMap = Double.parseDouble(globalClass.getLongitude());

        cityView = findViewById(R.id.cityView);
        cityView.setText("Map of " + city + ", " + country);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.mapbar);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.homebar:
                        Toast.makeText(GoogleMapActivity.this, "Home", Toast.LENGTH_SHORT).show();
                        showHome();
                        break;
                    case R.id.weatherbar:
                        Toast.makeText(GoogleMapActivity.this, "Weather", Toast.LENGTH_SHORT).show();
                        showWeather();
                        break;
                    case R.id.mapbar:
                        Toast.makeText(GoogleMapActivity.this, "Map", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.historybar:
                        Toast.makeText(GoogleMapActivity.this, "History", Toast.LENGTH_SHORT).show();
                        showHistory();
                        break;}
                return true;
            }
        });
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // https://tile.openweathermap.org/map/precipitation/30/10/10.png?appid=
        // Add a marker in Sydney and move the camera
        LatLng TutorialsPoint = new LatLng(latMap, lonMap);
        mMap.addMarker(new MarkerOptions().position(TutorialsPoint));
        // Move the camera instantly to Location with a zoom of 12.
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(TutorialsPoint, 8));

        ///Weather Overlay
        TileProvider tileProvider = new UrlTileProvider(256, 256) {

            @Override
            public URL getTileUrl(int x, int y, int zoom) {

                /* Define the URL pattern for the tile images */
                String s = String.format("https://tile.openweathermap.org/map/clouds_new/%d/%d/%d.png?appid=", zoom, x, y);

                if (!checkTileExists(x, y, zoom)) {
                    return null;
                }
                try {
                    return new URL(s);
                } catch (MalformedURLException e) {
                    throw new AssertionError(e);
                }
            }
            /* Check that the tile server supports the requested x, y and zoom.
             * Complete this stub according to the tile range you support.
             * If you support a limited range of tiles at different zoom levels, then you need to define the supported x, y range at each zoom level. */
            private boolean checkTileExists(int x, int y, int zoom) {
                int minZoom = 1;
                int maxZoom = 30;

                return (zoom >= minZoom && zoom <= maxZoom);
            }
        };
        TileOverlay tileOverlay = mMap.addTileOverlay(new TileOverlayOptions()
                .tileProvider(tileProvider));
    }

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

    public void showHistory () {
        //do something in response to button
        Intent intent = new Intent(this, HistoryActivity.class);
        startActivity(intent);
    }

}