

package com.example.autotrackerca400;

import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Handler;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;


/**
 * <h1>Following Functionality ;</h1>
 * <p>
 * - Determines speed limit of current road.
 * </p><p>
 * - Calculates current speed of vehicle.
 *</p><p>
 * - Determines if speed limit was exceeded.
 *</p><p>
 * - Calculates values at the end of the journey.
 *</p><p>
 * - Such as averagespeed and totaldistance.
 * </p>
 */
public class MonitorVehicleManager extends Service {

    // initialize variables

    static boolean buttonSpeak = false;
    static boolean threadVoice = false;

    // indication for location boolean when detecting objects
    public static boolean LocationDetect = false;


    static double previousLatitude;
    static double previousLongitude;
    static double currentLatitude;
    static double currentLongitude;
    // Integer so can return null value.
    static Integer speedLimit;
    static int currentSpeed;

    // counts for average speed.
    static int averageSpeedTotal;
    static int averageSpeedCount;

    //counts for totaldistance
    static double distanceTotal;



    static LocationManager locationManager;

    boolean journeyState = false;

    static boolean locationChanged = false;

    static Runnable runnableLocationPermissions;

    static Handler handlerLocationUpdates = new Handler();
    Handler handlerSpeedLimit;
    Runnable runnableSpeedLimit;

    Handler handlerTimer;

    static MainActivity mainActivity;

    static VoiceListener voiceListener;

    static int TAG_CODE_PERMISSION_LOCATION;

    int tripTimeSeconds;

    static boolean locationPermission = false;


    public MonitorVehicleManager(MainActivity MainActivity, LocationManager LocationManager){
        this.mainActivity = MainActivity;
        this.locationManager = LocationManager;
        voiceListener = new VoiceListener(this.mainActivity);
        // request location
        locationPermission();

        buttonSpeak = true;




    }

    public void speedLimitThread(){
        // thread used to get speed limit of the road that you are on
        handlerSpeedLimit = new Handler();
        runnableSpeedLimit = new Runnable() {
            public void run() {
                try {
                    getSpeedLimit(currentLatitude, currentLongitude);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
                // every 10 seconds look for speed limit again
                handlerSpeedLimit.postDelayed(this, 10000);
            }
        };
        handlerSpeedLimit.post(runnableSpeedLimit);
    }

    public Integer getSpeedLimit(double Latitude, double Longitude) throws InterruptedException, ExecutionException {
        SpeedLimitManager speedLimitInstance = new SpeedLimitManager();
        speedLimit = speedLimitInstance.getSpeedLimit(Double.toString(Latitude), Double.toString(Longitude));
        // null means no internet connection
        if(speedLimit == null){
            mainActivity.setSpeedText(currentSpeed,"No internet");
        } else{
            mainActivity.setSpeedText(currentSpeed, String.valueOf(speedLimit));
        }
        return speedLimit;
    }

    static void locationPermission() {
        // thread used to listen for location updates
        runnableLocationPermissions = () -> {
            // if locations permissions on phone not enabled sent notification
            if(ContextCompat.checkSelfPermission(mainActivity, Manifest.permission.ACCESS_FINE_LOCATION) ==
                    PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(mainActivity, Manifest.permission.ACCESS_COARSE_LOCATION) ==
                            PackageManager.PERMISSION_GRANTED) {
                locationPermission = true;
            } else {
                ActivityCompat.requestPermissions(mainActivity, new String[] {
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION },
                        TAG_CODE_PERMISSION_LOCATION);
            }
        };
        handlerLocationUpdates.post(runnableLocationPermissions);
    }



    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    // initialize location service
    void startLocationService(){
        // Creates a new Handler
        handlerTimer = new Handler();

        // determine total seconds.
        handlerTimer.post(new Runnable() {
            @Override
            public void run()
            {
                // increment the
                // seconds variable.
                tripTimeSeconds += 1;
                handlerTimer.postDelayed(this, 1000);
            }
        });

        //reset average speed counters + total distance
        averageSpeedTotal = 0;
        averageSpeedCount = 0;
        distanceTotal = 0;



        //get speed limit
        speedLimitThread();

    }


    public double getTripTime(int tripSeconds){
        // get total trip time in hours
        return (double) tripSeconds / 3600;
    }

    // stop listening for location updates
    ArrayList<Double> stopLocationService(){

        // stop timer
        handlerTimer.removeCallbacksAndMessages(null);


        // average speed of journey
        double averageSpeed = averageSpeedTotal / averageSpeedCount;

        // return totaldistance + average speed
        ArrayList<Double> vehicleTripValues = new ArrayList<>();
        vehicleTripValues.add(distanceTotal);
        vehicleTripValues.add(averageSpeed);

        // stop listening for location updates
        handlerLocationUpdates.removeCallbacksAndMessages(null);
        return vehicleTripValues;
    }

    // set the journey state to false
    void setJourneyState(boolean JourneyState){
        journeyState = JourneyState;
    }



    public static void LocationChanged(Location Location) {
        // get speed of vehicle
        getVehicleSpeed(Location);
        // set speed text in mainactivity
        if(speedLimit == null){
            mainActivity.setSpeedText(currentSpeed,"No internet");
        } else{
            mainActivity.setSpeedText(currentSpeed, String.valueOf(speedLimit));
        }

        // analyse gps co-ordinates
        setCoOrdinates(Location);

        DetectionActivity.speed = currentSpeed;

        // start listening for road speed updates
        if(locationChanged == false){
            locationChanged = true;
        }

        // has speed limit been exceeded?
        try {
            if(speedLimit != null) {
                isSpeedExceeded(currentSpeed, speedLimit);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static boolean isSpeedExceeded(int CurrentSpeed, int SpeedLimit) throws InterruptedException {
        boolean speedExceeded = false;
        // if speed is over speed limit of road
        // speedlimit > 0 shows connection established
        if(CurrentSpeed > SpeedLimit && SpeedLimit > 0){
            speedExceeded = true;
            // if voice is initialized and not in the middle of thread
            if(buttonSpeak == true && threadVoice == false) {
                // bool to indicate in middle of thread
                threadVoice = true;
                Thread voiceThread = new Thread() {
                    @Override
                    public void run() {
                        // emit voice
                        voiceListener.voiceSpeak("Slow down! Speed limit exceeded");
                        try {
                            Thread.sleep(5000);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                        threadVoice = false;
                    }
                };
                // start the thread
                voiceThread.start();
            }
        }
        return speedExceeded;
    }

    public static int getVehicleSpeed(Location Location){
        // get speed of location object
        // in m/s, convert to km/h by multiplying by 3.6 and round
        float vehicleSpeed = Location.getSpeed() * 3.6f;
        currentSpeed = Math.round(vehicleSpeed);
        averageSpeedTotal += currentSpeed;
        averageSpeedCount += 1;
        return currentSpeed;
    }

    public static void setCoOrdinates(Location Location){
        // get gps-co-ordinates used to determine speed of road
        currentLatitude = Location.getLatitude();
        currentLongitude = Location.getLongitude();

        // initialize latitude variables
        if(previousLatitude == 0 && previousLongitude == 0){
            previousLatitude = Location.getLatitude();
            previousLongitude = Location.getLongitude();
        }
        double distance = calculateDistance(currentLatitude, currentLongitude, previousLatitude, previousLongitude);
        // count every 10 meters
        if(distance >= 10){
            distanceTotal += distance;
            previousLatitude = Location.getLatitude();
            previousLongitude = Location.getLongitude();
        }
    }


    @Override
    public void onStart(Intent intent, int startid) {
    }

    public static float calculateDistance(double lat1, double lng1, double lat2, double lng2) {
        double earthRadius = 6371000; //meters
        double dLat = Math.toRadians(lat2-lat1);
        double dLng = Math.toRadians(lng2-lng1);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLng/2) * Math.sin(dLng/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        float dist = (float) (earthRadius * c);

        return dist;
    }

}
