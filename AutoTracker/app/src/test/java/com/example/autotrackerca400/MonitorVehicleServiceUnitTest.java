package com.example.autotrackerca400;

import static org.junit.Assert.assertEquals;

import android.Manifest;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;

import androidx.test.rule.ActivityTestRule;
import androidx.test.rule.GrantPermissionRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

@RunWith(RobolectricTestRunner.class)
public class MonitorVehicleServiceUnitTest {

    MonitorVehicleManager monitorVehicleService;

    @Rule
    public ActivityTestRule<MainActivity> mainActivityActivityTestRule =
            new ActivityTestRule<>(MainActivity.class);

    @Rule
    public GrantPermissionRule mLocationPermissionRule = GrantPermissionRule.grant(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION);


    LocationManager locationManager;

    @Before
    public void Setup(){
        MainActivity mainActivity = mainActivityActivityTestRule.getActivity();
        locationManager = (LocationManager)mainActivity.getSystemService(Context.LOCATION_SERVICE);
        monitorVehicleService = new MonitorVehicleManager(mainActivity,locationManager);
    }

    /**
     * <h1>Tests Following ;</h1>
     * <p>
     * - get speed limit returns right limit based on co-ordinates
     * </p>
     */
    @Test
    public void getSpeedLimitTest() throws InterruptedException, ExecutionException {
        // get speed limit of m3 road, which is 120km/h
        if(SpeedLimitManager.isInternetConnection()) {
            assertEquals(monitorVehicleService.getSpeedLimit(53.560546, -6.558191), (Integer) 120);
        }
    }

    @Test
    public void getTripTimeTest(){
        // trip time of 5 minutes converted in hours
        assertEquals(monitorVehicleService.getTripTime(300), 0.08333333333333333,0.0);
    }

    /**
     * <h1>Tests Following ;</h1>
     * <p>
     * - calculate distance function returns right distance based on co-ordinates
     * </p>
     */
    @Test
    public void calculateDistanceTest(){
        // distance from blanchardstown to glasnevin about 7km.
        //assertEquals(monitorVehicleService.calculateDistance(53.3868, -6.3772, 53.3696, -6.2769,"K"),6.922125869222845,0.0);
    }

    /**
     * <h1>Tests Following ;</h1>
     * <p>
     * - get vehicle speed returns correct speed limit given location
     * </p>
     */
    @Test
    public void getVehicleSpeedTest(){
        Location newLocation = new Location("flp");
        newLocation.setLatitude(53.3868);
        newLocation.setLongitude(-6.3772);
        newLocation.setSpeed(20);
        assertEquals(monitorVehicleService.getVehicleSpeed(newLocation),72);
    }

    /**
     * <h1>Tests Following ;</h1>
     * <p>
     * - ensures isspeedexceeded function is able to detect if speed limit breached
     * </p>
     */
    @Test
    public void isSpeedExceededTest() throws InterruptedException {
        assertEquals(monitorVehicleService.isSpeedExceeded(80,100),false);
        assertEquals(monitorVehicleService.isSpeedExceeded(100,80), true);
    }

    /**
     * <h1>Tests Following ;</h1>
     * <p>
     * - Checks that average speed and total distance work the right ways
     * </p>
     */
    @Test
    public void stopLocationServiceTest() throws InterruptedException {
        monitorVehicleService.startLocationService();
        Location newLocation = new Location("flp");
        int i = 0;
        // speed is 20, location changed 5 timess
        while(i < 5){
            newLocation.setSpeed(20);
            monitorVehicleService.getVehicleSpeed(newLocation);
            i += 1;
        }
        // trip on the m3
        newLocation.setLatitude(53.703083);
        newLocation.setLongitude(-6.846312);
        // set original co-ordinates
        monitorVehicleService.setCoOrdinates(newLocation);
        monitorVehicleService.locationPermission();
        // 2.4 km trip
        newLocation.setLatitude(53.690023);
        newLocation.setLongitude(-6.817644);
        // set new co-ordinates
        monitorVehicleService.setCoOrdinates(newLocation);

        ArrayList<Double> tripValues = new ArrayList();
        // distance should be around 2.4km, originally in meters, converted to km after
        tripValues.add(2381.37353515625);
        // average speed 20 * 3.6(convert to km/h)
        tripValues.add(72.0);
        assertEquals(monitorVehicleService.stopLocationService(),tripValues);

    }


}