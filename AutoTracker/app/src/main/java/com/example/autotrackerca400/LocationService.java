package com.example.autotrackerca400;


import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

public class LocationService extends Service implements LocationListener {

    LocationManager locationManager;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    @Override
    public void onCreate() {
        getLocation();
    }
    @Override
    public void onStart(Intent intent, int startid) {
        super.onStart(intent, startid);
        getLocation();
    }
    @Override
    public void onDestroy() {
        locationManager.removeUpdates(this);
    }

    // get location of the place you are currently in
    void getLocation() {
        try {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                locationManager =
                        (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 1, this);
            }
        }
        catch(SecurityException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onLocationChanged(Location Location) {

        // pass location to monitorvehicle
        MonitorVehicleManager.LocationChanged(Location);

        // look for more location updates
        getLocation();

    }
}
