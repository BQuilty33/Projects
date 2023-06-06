package com.example.autotrackerca400;

import static org.junit.Assert.assertEquals;

import androidx.test.rule.ActivityTestRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

@RunWith(RobolectricTestRunner.class)
public class DrivingTypeModelUnitTest {
    DrivingTypeModel drivingTypeModel;

    @Rule
    public ActivityTestRule<MainActivity> mainActivityActivityTestRule =
            new ActivityTestRule<>(MainActivity.class);

    MainActivity mainActivity;

    int[] driversPercentage = new int[8];

    @Before
    public void Setup(){
        drivingTypeModel = new DrivingTypeModel();
        // get main activity instance
        mainActivity = mainActivityActivityTestRule.getActivity();
    }

    /**
     * <h1>Tests Following ;</h1>
     * <p>
     * - Inserts two driver type percentages based on aggressive and normal styles
     * </p><p>
     * - Ensures that PredictDriverType function predicts correct types.
     * </p>
     */
    @Test
    public void predictDriverTypeTest() throws Exception {
        InputStream targetStream = null;
        try {
            targetStream = mainActivity.getAssets().open("driving_behaviour_dataset.csv");
        } catch (IOException e) {
            e.printStackTrace();
        }
        ArrayList<String[]> tmpArray = new ArrayList<>();
        String[] tmp = new String[]{"0.75819385" , "-0.2177906","0.45726347","0.0","0.0","0.0"}; // Aggressive
        String[] tmp2 = new String[]{"-0.6068866" , "0.60555565","-0.13221455","0.03359759","0.031994067","0.0047342055"}; // Normal
        tmpArray.add(tmp);
        tmpArray.add(tmp2);
        driversPercentage = drivingTypeModel.PredictDriverType(targetStream,tmpArray,2,4,6,12);
        assertEquals(driversPercentage[0],0); // 0 slow values
        assertEquals(driversPercentage[1],1); // 1 normal values
        assertEquals(driversPercentage[2],1); // 1 aggressive values
        assertEquals(driversPercentage[3],2); // 2 total values predicted.
        assertEquals(driversPercentage[4],2); // 2 total slow values(counting previous slow)
        assertEquals(driversPercentage[5],5); // 5 total normal values
        assertEquals(driversPercentage[6],7); // 7 total aggressive value
        assertEquals(driversPercentage[7],14); // 14 total values
    }



}