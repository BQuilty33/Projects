package com.example.autotrackerca400;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;



/**
 * <h1>Following Functionality ;</h1>
 * <p>
 * - Determines accelerometer value of phone every 1 second and inserts each value into database.
 *</p><p>
 * - Determines gyroscope value of phone every 1 second and inserts each value into database.
 *</p><p>
 * - Database will consist of each specific sensor value throughout the journey.
 * </p>
 */
public class SensorListener implements SensorEventListener {
    // initialize variables
    public static SensorManager mSensorManager;
    private final Sensor mAccelerometer;
    private final Sensor mGyroscope;

    MainActivity mainActivity;
    static DatabaseManager databaseManager;

    static double sensorSecondsCurrent;
    static double sensorSecondsStarted;

    static float accelerometerXCord;
    static float accelerometerYCord;
    static float accelerometerZCord;
    static float gyroscopeXCord;
    static float gyroscopeYCord;
    static float gyroscopeZCord;

    static double previousSecondsGyroscope;

    static String drivingValues;



    public SensorListener(SensorManager SensorManager, MainActivity MainActivity, DatabaseManager DatabaseManager){

        // initialize sensors
        mSensorManager = SensorManager;
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mGyroscope = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);

        // initialize classes
        this.mainActivity = MainActivity;
        databaseManager = DatabaseManager;

        // initial time to determine when sensor value changes
        long initTime = System.nanoTime();
        sensorSecondsStarted = (double) initTime / 1_000_000_000;
        previousSecondsGyroscope = (double) initTime / 1_000_000_000;
    }

    protected void onResume() {
        // register accelerometer and gyroscope listener sensors.
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this, mGyroscope, SensorManager.SENSOR_DELAY_NORMAL);
    }

    protected void onPause() {
        // stop sensor listeners
        mSensorManager.unregisterListener(this);
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    public void onSensorChanged(SensorEvent Event) {
        long startTime = System.nanoTime();
        sensorSecondsCurrent = (double) startTime / 1_000_000_000;

        // if accelerometer type and seconds passed > 1
        if(Event.sensor.getType() == 1 && sensorSecondsCurrent - sensorSecondsStarted >= 1) {
            setAccelerometer(Event);
        }

        // if gyroscope type and seconds passed > 1
        if(Event.sensor.getType() == 4 && sensorSecondsCurrent - previousSecondsGyroscope >= 1){
            setGyroscope(Event);
        }
    }

    public static boolean setAccelerometer(SensorEvent Event){
        // reset seconds
        sensorSecondsStarted = sensorSecondsCurrent;

        // assign sensor values
        accelerometerXCord = Event.values[0];
        accelerometerYCord = Event.values[1];
        accelerometerZCord = Event.values[2];

        // add sensorarray value to database as a string separated by delimiter ","
        return databaseManager.insertSensorValues(getDriverValues());
    }

    public static boolean setGyroscope(SensorEvent Event){
        // reset seconds
        previousSecondsGyroscope = sensorSecondsCurrent;

        // assign sensor values
        gyroscopeXCord = Event.values[0];
        gyroscopeYCord = Event.values[1];
        gyroscopeZCord = Event.values[2];

        // add sensorarray value to database as a string separated by delimiter ","
        return databaseManager.insertSensorValues(getDriverValues());
    }

    public static String getDriverValues(){
        drivingValues = accelerometerXCord + "," + accelerometerYCord + "," + accelerometerZCord + "," + gyroscopeXCord + "," + gyroscopeYCord + "," + gyroscopeZCord;
        return drivingValues;
    }

}
