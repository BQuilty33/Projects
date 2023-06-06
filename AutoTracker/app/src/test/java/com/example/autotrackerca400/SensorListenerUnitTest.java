package com.example.autotrackerca400;

import static android.content.Context.SENSOR_SERVICE;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;

import androidx.test.rule.ActivityTestRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;

import java.lang.reflect.Field;

@RunWith(RobolectricTestRunner.class)
public class SensorListenerUnitTest {

    @Rule
    public ActivityTestRule<MainActivity> mainActivityActivityTestRule =
            new ActivityTestRule<>(MainActivity.class);

    SensorListener sensorListener;

    @Before
    public void Setup(){
        MainActivity mainA = mainActivityActivityTestRule.getActivity();
        DatabaseSQL dataB = new DatabaseSQL(mainA.getApplicationContext());
        DatabaseManager dataS = new DatabaseManager(dataB,mainA);
        sensorListener = new SensorListener((SensorManager)mainA.getSystemService(SENSOR_SERVICE),mainA,dataS);
    }

    public SensorEvent createSensorEvent(String sensorType, float[] sensorValues) throws NoSuchFieldException, IllegalAccessException {
        SensorEvent eventAccelerometer = Mockito.mock(SensorEvent.class);
        // Get the 'sensor' field in order to set it.
        Field sensorField = SensorEvent.class.getField("sensor");
        sensorField.setAccessible(true);
        // Create the value we want for the 'sensor' field.
        Sensor sensor = Mockito.mock(Sensor.class);
        if(sensorType == "Accelerometer") {
            when(sensor.getType()).thenReturn(Sensor.TYPE_ACCELEROMETER);
        } else if(sensorType == "Gyroscope") {
            when(sensor.getType()).thenReturn(Sensor.TYPE_GYROSCOPE);
        }
        // Set the 'sensor' field.
        sensorField.set(eventAccelerometer, sensor);
        // Get the 'values' field in order to set it.
        Field valuesField = SensorEvent.class.getField("values");
        valuesField.setAccessible(true);
        // Create the values we want to return for the 'values' field.
        valuesField.set(eventAccelerometer, sensorValues);
        return eventAccelerometer;
    }

    public float[] getAccelerometerValues(){
        float accelerometerValues[] = new float[]{(float)0.75819385,(float) -0.2177906, (float) 0.45726347};
        return accelerometerValues;
    }

    public float[] getGyroscopeValues(){
        float gyroscopeValues[] = new float[]{(float)0.7281939,(float) -0.2277906, (float) 0.41726348};
        return gyroscopeValues;
    }

    /**
     * <h1>Tests Following ;</h1>
     * <p>
     * - Tests that you can enter accelerometer values into database.
     * </p>
     */
    @Test
    public void testAccelerometerInsertDatabase() throws NoSuchFieldException, IllegalAccessException {
        SensorEvent accelerometerInsert = createSensorEvent("Accelerometer",getAccelerometerValues());
        assertEquals(SensorListener.setAccelerometer(accelerometerInsert),true);
    }

    /**
     * <h1>Tests Following ;</h1>
     * <p>
     * - Tests that you can enter gyroscope values into database.
     * </p>
     */
    @Test
    public void testGyroscopeInsertDatabase() throws NoSuchFieldException, IllegalAccessException {
        SensorEvent gyroscopeInsert = createSensorEvent("Gyroscope",getGyroscopeValues());
        assertEquals(SensorListener.setGyroscope(gyroscopeInsert),true);
    }
    /**
     * <h1>Tests Following ;</h1>
     * <p>
     * - Tests that you driver values array in string form gets the correct sensor values and is in correct form.
     * </p>
     */
    @Test
    public void getDriverValuesTest() throws NoSuchFieldException, IllegalAccessException {
        SensorEvent gyroscopeInsert = createSensorEvent("Gyroscope",getGyroscopeValues());
        SensorEvent accelerometerInsert = createSensorEvent("Accelerometer",getAccelerometerValues());
        SensorListener.setAccelerometer(accelerometerInsert);
        SensorListener.setGyroscope(gyroscopeInsert);
        assertEquals(SensorListener.getDriverValues(),"0.75819385,-0.2177906,0.45726347,0.7281939,-0.2277906,0.41726348");
    }



}