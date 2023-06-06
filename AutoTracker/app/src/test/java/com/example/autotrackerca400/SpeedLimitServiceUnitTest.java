package com.example.autotrackerca400;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.Optional;
import java.util.concurrent.ExecutionException;

@RunWith(RobolectricTestRunner.class)
public class SpeedLimitServiceUnitTest {

    SpeedLimitManager speedLimitManager;


    @Before
    public void Setup(){
        speedLimitManager = new SpeedLimitManager();
    }

    /**
     * <h1>For CI/CD</h1>
     * <p>
     * - Ensure functionality without internet connection.
     *</p><p>
     * - CI/CD cannot connect to internet so tests fail.
     *</p>
     */
    @Test
    public void getSpeedLimitTestMotorway() {
        // get speed limit of m3 road, which is 120km/h
        assertEquals(speedLimitManager.speedLimitThread("53.560546", "-6.558191"), (Integer) 120);
    }

    @Test
    public void getSpeedLimitTestNationalRoad() {
        // get speed limit of n3 road, national roads are 100km
        assertEquals(speedLimitManager.speedLimitThread("53.868294", "-7.117541"), (Integer) 100);
    }

    @Test
    public void getSpeedLimitTestRegionalRoad() {
        // get speed limit of r195 road, regional roads are 80km
        assertEquals(speedLimitManager.speedLimitThread("53.756201", "-7.166777"), (Integer) 80);
    }

    @Test
    public void getSpeedLimitTestUrbanRoad() {
        // get speed limit of mullingar town - urban area is 50km.
        assertEquals(speedLimitManager.speedLimitThread("53.525686", "-7.341365"), (Integer) 50);
    }

    /**
     * <h1>For Local tests</h1>
     * <p>
     * - Ensure normal usage of class works
     *</p><p>
     * - With internet connection. To be tested locally.
     *</p>
     */

    @Test
    public void getSpeedLimitTestMotorwayLocal() throws InterruptedException, ExecutionException {
        if(speedLimitManager.isInternetConnection()) {
            // get speed limit of m3 road, which is 120km/h
            assertEquals(speedLimitManager.getSpeedLimit("53.560546", "-6.558191"), (Integer) 120);
        }
    }

    @Test
    public void getSpeedLimitTestNationalRoadLocal() throws InterruptedException, ExecutionException {
        if(speedLimitManager.isInternetConnection()) {
            // get speed limit of n3 road, national roads are 100km
            assertEquals(speedLimitManager.getSpeedLimit("53.868294", "-7.117541"), (Integer) 100);
        }
    }

    @Test
    public void getSpeedLimitTestRegionalRoadLocal() throws InterruptedException, ExecutionException {
        if(speedLimitManager.isInternetConnection()) {
            // get speed limit of r195 road, regional roads are 80km
            assertEquals(speedLimitManager.getSpeedLimit("53.756201", "-7.166777"), (Integer) 80);
        }
    }

    @Test
    public void getSpeedLimitTestUrbanRoadLocal() throws InterruptedException, ExecutionException {
        if(speedLimitManager.isInternetConnection()) {
            // get speed limit of mullingar town - urban area is 50km.
            assertEquals(speedLimitManager.getSpeedLimit("53.525686", "-7.341365"), (Integer) 50);
        }
    }




}
