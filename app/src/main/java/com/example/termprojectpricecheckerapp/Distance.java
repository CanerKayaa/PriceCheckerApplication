package com.example.termprojectpricecheckerapp;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Looper;
import android.util.Log;
import androidx.core.app.ActivityCompat;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;

public class Distance implements Runnable{
    Context context;
    FusedLocationProviderClient fusedLocationProviderClient;

    ArrayList<String> arrayList;
    ArrayList<Integer> kmArrList = new ArrayList<>();
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private  double currentLat;
    private  double currentLong;
    Consumer<ArrayList<Integer>> consumer;

    public Distance(Context context, ArrayList<String> arrayList, Consumer<ArrayList<Integer>> consumer) {
        this.context = context;
        this.arrayList = arrayList;
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context);
        this.consumer = consumer;
    }


    @Override
    public void run() {
        getCurrentLocationLatLng();
    }

    private void getOutletLocationLatLng(String outletAddress) {
        String location = outletAddress;
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION}, 123);
        }
        if(location != null || !location.equals("")){
            Geocoder geocoder = new Geocoder(context,Locale.getDefault());
            try{
                List<Address>   addressList = geocoder.getFromLocationName(location,1);
                Address address = addressList.get(0);
                LatLng latLng = new LatLng(address.getLatitude(),address.getLongitude());
                kmArrList.add(CalculationByDistance(currentLat ,currentLong, latLng.latitude,latLng.longitude));

            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }


    private void getCurrentLocationLatLng() {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION}, 123);
        }

        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(final Location location) {

                if (location != null) {
                    try{
                        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
                        List<Address> addresses = geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),1);
                        currentLat = addresses.get(0).getLatitude();
                        currentLong = addresses.get(0).getLongitude();

                        for(int i = 0; i < arrayList.size(); i++){
                            getOutletLocationLatLng(arrayList.get(i));
                        }
                        System.out.println("final km arr list : " + kmArrList);
                        consumer.accept(kmArrList);
                    }catch (IOException e){
                        e.printStackTrace();
                    }
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
                    if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
                }
            }
        });
        }


    public int CalculationByDistance(double currentLat, double currentLong, double outletLat, double outletLong) {
        int Radius = 6371;// radius of earth in Km
        double lat1 = currentLat;
        double lat2 = outletLat;
        double lon1 = currentLong;
        double lon2 = outletLong;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1))
                * Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2)
                * Math.sin(dLon / 2);
        double c = 2 * Math.asin(Math.sqrt(a));
        double valueResult = Radius * c;
        double km = valueResult / 1;
        DecimalFormat newFormat = new DecimalFormat("####");
        int kmInDec = Integer.valueOf(newFormat.format(km));
        double meter = valueResult % 1000;
        int meterInDec = Integer.valueOf(newFormat.format(meter));
        Log.i("Radius Value", "" + valueResult + "   KM  " + kmInDec
                + " Meter   " + meterInDec);

        return kmInDec;
    }

    public ArrayList<Integer> getKmArrList(){
        return kmArrList;
    }


}
