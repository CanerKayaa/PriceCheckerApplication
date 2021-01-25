package com.example.termprojectpricecheckerapp;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.widget.SearchView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity {

    FusedLocationProviderClient client;
    SupportMapFragment mapFragment;
    SearchView searchView;
    Intent intent;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        searchView = findViewById(R.id.sv_location);
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        intent = getIntent();

        client = LocationServices.getFusedLocationProviderClient(this);

        getCurrentLocation();


    }

    private void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION}, 123);
        }

        Task<Location> task = client.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(final Location location) {

                if (location != null) {
                    mapFragment.getMapAsync(new OnMapReadyCallback() {
                        @Override
                        public void onMapReady(final GoogleMap googleMap) {
                            LatLng latlng = new LatLng(location.getLatitude(), location.getLongitude());
                            //Create marker options
                            MarkerOptions options = new MarkerOptions().position(latlng).title("You are here!");

                            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latlng, 10));
                            googleMap.addMarker(options);

                            searchView.setQuery(intent.getStringExtra("shopAddress"), true);
                            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                                @Override
                                public boolean onQueryTextSubmit(String s) {
                                    String location = searchView.getQuery().toString();
                                    System.out.println("location : " + location);
                                    List<Address> addressList = null;
                                    if (location != null || !location.equals("")) {
                                        Geocoder geocoder = new Geocoder(MapsActivity.this);
                                        try {
                                            addressList = geocoder.getFromLocationName(location, 1);
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                        if(!addressList.isEmpty()){
                                            Address address = addressList.get(0);
                                            LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
                                            System.out.println("lat : " + latLng.latitude);
                                            System.out.println("long : " + latLng.longitude);
                                            googleMap.addMarker(new MarkerOptions().position(latLng).title(location));
                                            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));
                                        }


                                    }
                                    return false;
                                }

                                @Override
                                public boolean onQueryTextChange(String s) {
                                    return false;
                                }
                            });
                        }
                    });
                } else {
                    locationRequest = LocationRequest.create();
                    locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                    locationRequest.setInterval(20 * 1000);
                    locationCallback = new LocationCallback() {
                        @Override
                        public void onLocationResult(LocationResult locationResult) {
                            if (locationResult == null) {
                                return;
                            }
                            for (Location location : locationResult.getLocations()) {
                                if (location != null) {
                                    double wayLatitude = location.getLatitude();
                                    double wayLongitude = location.getLongitude();
                                }
                            }
                        }
                    };
                    if (ActivityCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    client.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
                }
            }
        });
    }

}