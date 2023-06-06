package com.example.autotrackerca400;

import static org.junit.Assert.assertEquals;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.Arrays;

@RunWith(RobolectricTestRunner.class)
public class DatabaseManagerUnitTest {

    private DatabaseManager databaseManager;

    private DatabaseSQL database;

    @Rule
    public ActivityTestRule<MainActivity> mainActivityActivityTestRule =
            new ActivityTestRule<>(MainActivity.class);

    @Before
    public void Setup(){
        MainActivity mainActivity = mainActivityActivityTestRule.getActivity();
        database = new DatabaseSQL(InstrumentationRegistry.getInstrumentation().getTargetContext());
        databaseManager = new DatabaseManager(database,mainActivity );
    }

    /**
     * <h1>Tests Following ;</h1>
     * <p>
     * - Can insert sensor values into database.
     * </p>
     */
    @Test
    public void insertSensorValuesTest(){
        assertEquals(databaseManager.insertSensorValues("1,1,1,1,1,1"),true);
    }

    /**
     * <h1>Tests Following ;</h1>
     * <p>
     * - Can reset driver id list when deleting journey so ids are aligned.
     * </p>
     */
    @Test
    public void resetDriverListTest(){
        // initialize driver list
        String[] driverValues = new String[]{"1,1,1","1,1,2","1,2,1"};

        // add to driver list
        databaseManager.driverList.add(driverValues);
        databaseManager.driverList.add(driverValues);
        // ensure values added
        assertEquals(databaseManager.driverList.size(),2);

        // reset driver list
        databaseManager.resetDriverList();

        // ensure no values
        assertEquals(databaseManager.driverList.size(),0);
    }

    /**
     * <h1>Tests Following ;</h1>
     * <p>
     * - Ensures you can convert sensor values from string format into array.
     * </p>
     */
    @Test
    public void convertSensorValuesTest(){
        database.insertSensorValues("0.01,0.1,0.2,0.3,0.4,0.5");

        assertEquals(databaseManager.driverList.size(),0);

        databaseManager.convertSensorValues();
        String[] driverValues = new String[]{"0.01","0.1","0.2","0.3","0.4","0.5"};

        assertEquals(databaseManager.driverList.get(0),driverValues);

        assertEquals(databaseManager.driverList.size(),1);

    }

    /**
     * <h1>Tests Following ;</h1>
     * <p>
     * - Ensures database service can get previous driver type counts
     * </p><p>
     * - From values inserted into the database
     * </p><p>
     * - Ensures they are equal
     * </p>
     */
    @Test
    public void getPreviousDriverTypeCountsTest(){
        database.updateTotalCounts(10,20,30,40,50,60);

        int[] indexToKeep = new int[]{10,20,30,40};
        Arrays.stream(indexToKeep).toArray();

        int totalcount = 0;

        // loop through each value to ensure they are equal.
        int i = 0;
        while(i < 4){
            if(indexToKeep[i] == databaseManager.getPreviousDriverTypeCounts()[i]){
                totalcount += 1;
            }
            i += 1;
        }

        assertEquals(totalcount,4);

    }

}