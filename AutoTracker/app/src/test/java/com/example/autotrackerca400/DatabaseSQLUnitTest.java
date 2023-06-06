package com.example.autotrackerca400;

import static org.junit.Assert.assertEquals;

import android.database.Cursor;

import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class DatabaseSQLUnitTest {

    private DatabaseSQL database;

    @Before
    public void Setup() {
        // get database instance
        database = new DatabaseSQL(InstrumentationRegistry.getInstrumentation().getTargetContext());
    }

    public void createJourneys(boolean oneJourney){
        // reset journeys
        database.clearDatabaseTable("JourneyDetails");
        // insert new journeys
        if(oneJourney == true){
            database.insertJourneyDetails("journey1",500,50,"Slow");
        } else {
            database.insertJourneyDetails("journey1", 500, 50, "Slow");
            database.insertJourneyDetails("journey2", 700, 70, "Normal");
        }
    }

    public void createSensorValues(){
        database.insertSensorValues("1,1,1,1,1,1");
        database.insertSensorValues("1,1,1,1,1,1");
        database.insertSensorValues("1,1,1,1,1,1");
    }

    /**
     * <h1>Tests Following ;</h1>
     * <p>
     * - Can insert driver type counts into database.
     * </p>
     */
    @Test
    public void insertDriverTypeCountsTest(){
        assertEquals(database.insertDriverTypeCounts(1,5,6,12),true);
    }

    /**
     * <h1>Tests Following ;</h1>
     * <p>
     * - Can insert journey details into database.
     * </p>
     */
    @Test
    public void insertJourneyDetailsTest(){
        assertEquals(database.insertJourneyDetails("Journey1",20,10,"Slow"),true);
    }

    /**
     * <h1>Tests Following ;</h1>
     * <p>
     * - Can insert sensor values into database.
     * </p>
     */
    @Test
    public void insertSensorValuesTest(){
        assertEquals(database.insertSensorValues("1,1,1,1,1,1"),true);
    }

    /**
     * <h1>Tests Following ;</h1>
     * <p>
     * - Can clear database table
     * </p><p>
     * ensures there are no values in databse after calling method
     * </p>
     */
    @Test
    public void clearDatabaseTest(){
        // insert original sensor values
        createSensorValues();

        Cursor sensorValuesCursor = database.getSensorValues();

        assertEquals(sensorValuesCursor.getCount(),3);

        // clear sensor values from database
        database.clearDatabaseTable("SensorDetails");

        // ensure that there are no sensor values remaining
        Cursor sensorValuesCursor2 = database.getSensorValues();

        assertEquals(sensorValuesCursor2.getCount(),0);

    }

    /**
     * <h1>Tests Following ;</h1>
     * <p>
     * - Inserts sensor values into database.
     * </p><p>
     * - Ensures that getSenorValues returns same amount of values.
     * </p>
     */
    @Test
    public void sensorValuesTest(){
        // insert original sensor values
        createSensorValues();

        // get sensor values
        Cursor sensorValuesCursor = database.getSensorValues();

        // ensure sensor values count is 3
        assertEquals(sensorValuesCursor.getCount(),3);
    }

    @Test
    public void getCountJourneyTest(){
        // ensure original journey count
        assertEquals(database.getCountJourney(),0);

        // insert new journeys
        createJourneys(false);

        // journey count aligns with new journeys inserted.
        assertEquals(database.getCountJourney(),2);

    }

    @Test
    public void deleteJourneyTest(){
        // insert new journeys
        createJourneys(false);

        assertEquals(database.getCountJourney(),2);

        // delete first journey
        database.deleteJourney(1);

        // journey count aligns with new journeys inserted.
        assertEquals(database.getCountJourney(),1);
    }

    @Test
    public void renameJourneyTest(){
        // insert new journeys
        createJourneys(true);
        // rename the journey
        database.renameJourney("journeyNow", 1);

        // get the name of the journey
        Cursor journeyNameCursor = database.getJourneyDetail("journeyName",null);
        journeyNameCursor.moveToFirst();

        // ensure journey has been renamed
        assertEquals(journeyNameCursor.getString(0),"journeyNow");


    }


    /**
     * <h1>Tests Following ;</h1>
     * <p>
     * - Updates totalcounts into database.
     * </p><p>
     * - Ensures that geting a total stats value of details returns correct value.
     * </p>
     */
    @Test
    public void totalCountsTest(){
        database.updateTotalCounts(10,5,2,17,50,35);
        assertEquals(database.getTotalStatsValue("count_slow"),10);
        assertEquals(database.getTotalStatsValue("count_normal"),5);
        assertEquals(database.getTotalStatsValue("count_aggr"),2);
        assertEquals(database.getTotalStatsValue("total_count"),17);
        assertEquals(database.getTotalStatsValue("total_dist"),50);
        assertEquals(database.getTotalStatsValue("Avg_speed"),35);
    }

    @Test
    public void setTotalSpeedValueTest(){
        database.setTotalSpeedValue(10,"Aspd0");
        database.setTotalSpeedValue(14,"Aspd1");
        database.setTotalSpeedValue(4,"Aspd2");
        database.setTotalSpeedValue(18,"Aspd3");

        assertEquals(database.getTotalStatsValue("Aspd0"),10);
        assertEquals(database.getTotalStatsValue("Aspd1"),14);
        assertEquals(database.getTotalStatsValue("Aspd2"),4);
        assertEquals(database.getTotalStatsValue("Aspd3"),18);

    }

    /**
     * <h1>Tests Following ;</h1>
     * <p>
     * - Inserts journeydetails into database.
     * </p><p>
     * - Ensures that returned journey details are correct.
     * </p>
     */
    @Test
    public void journeyDetailsTest(){
        // insert journey
        createJourneys(true);
        // get journey detail cursor objects
        Cursor journeyNameCursor = database.getJourneyDetail("journeyName",null);
        journeyNameCursor.moveToFirst();

        Cursor journeyDistanceCursor = database.getJourneyDetail("distTravelled",null);
        journeyDistanceCursor.moveToFirst();

        Cursor journeyAverageSpeedCursor = database.getJourneyDetail("averageSpeed",null);
        journeyAverageSpeedCursor.moveToFirst();

        Cursor journeyDriverTypeCursor = database.getJourneyDetail("driverType",null);
        journeyDriverTypeCursor.moveToFirst();

        // ensure cursor objects are equal to journey details in database
        assertEquals(journeyNameCursor.getString(0), "journey1");
        assertEquals(journeyDistanceCursor.getInt(0), 500);
        assertEquals(journeyAverageSpeedCursor.getInt(0), 50);
        assertEquals(journeyDriverTypeCursor.getString(0), "Slow");
    }

    /**
     * <h1>Tests Following ;</h1>
     * <p>
     * - Ensures that resetting journey ID doesn't mess up order of journeys
     * </p><p>
     * - Journey Ids after resetting/deleting ID should move up to the next ID.
     * </p>
     */
    @Test
    public void resetJourneyIdTest(){
        // insert journeys
        createJourneys(false);
        database.insertJourneyDetails("journey3",200,20,"Slow");
        database.insertJourneyDetails("journey4",400,10,"Slow");

        // ensure original values correct
        Cursor journeyNameCursor;
        journeyNameCursor = database.getJourneyDetail("journeyName",null);
        journeyNameCursor.moveToFirst();
        assertEquals(journeyNameCursor.getString(0),"journey1");
        journeyNameCursor.moveToNext();
        assertEquals(journeyNameCursor.getString(0),"journey2");
        journeyNameCursor.moveToNext();
        assertEquals(journeyNameCursor.getString(0),"journey3");
        journeyNameCursor.moveToNext();
        assertEquals(journeyNameCursor.getString(0),"journey4");

        // reset ID (used in conjunction with deleting a journey)
        database.deleteJourney(2);
        database.resetJourneyIds(2);

        // journey2 now removed, ids still match journeyNames in order.
        journeyNameCursor = database.getJourneyDetail("journeyName",1);
        journeyNameCursor.moveToFirst();
        assertEquals(journeyNameCursor.getString(0),"journey1");
        journeyNameCursor = database.getJourneyDetail("journeyName",2);
        journeyNameCursor.moveToFirst();
        assertEquals(journeyNameCursor.getString(0),"journey3");
        journeyNameCursor = database.getJourneyDetail("journeyName",3);
        journeyNameCursor.moveToFirst();
        assertEquals(journeyNameCursor.getString(0),"journey4");
    }

}