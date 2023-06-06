package com.example.autotrackerca400;

import static org.junit.Assert.assertEquals;

import android.widget.TextView;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.ArrayList;
import java.util.List;

@RunWith(RobolectricTestRunner.class)
public class JourneyDetailsActivityUnitTest {

    @Rule
    public ActivityTestRule<JourneyDetailsActivity> journeysActivityActivityTestRule =
            new ActivityTestRule<>(JourneyDetailsActivity.class);

    private DatabaseSQL database;
    JourneyDetailsActivity journeyDetailsActivity;

    private TextView distanceTravelled;
    private TextView driverType;
    private TextView averageSpeed;
    private TextView numberJourneys;

    private TextView slowDriverType;
    private TextView normalDriverType;
    private TextView aggressiveDriverType;

    private TextView AverageSpeed050;
    private TextView AverageSpeed50;
    private TextView AverageSpeed100;
    private TextView AverageSpeed200;

    @Before
    public void setup(){
        database = new DatabaseSQL(InstrumentationRegistry.getInstrumentation().getTargetContext());
        journeyDetailsActivity = journeysActivityActivityTestRule.getActivity();
        distanceTravelled = journeyDetailsActivity.findViewById(R.id.distance_travelled);
        driverType = journeyDetailsActivity.findViewById(R.id.driver_type);
        averageSpeed = journeyDetailsActivity.findViewById(R.id.average_speed);
        numberJourneys = journeyDetailsActivity.findViewById(R.id.number_of_journeys);

        slowDriverType = journeyDetailsActivity.findViewById(R.id.driver_slow);
        normalDriverType = journeyDetailsActivity.findViewById(R.id.driver_normal);
        aggressiveDriverType = journeyDetailsActivity.findViewById(R.id.driver_aggressive);

        AverageSpeed050 = journeyDetailsActivity.findViewById(R.id.zerokm_text);
        AverageSpeed50 = journeyDetailsActivity.findViewById(R.id.fiftykm_text);
        AverageSpeed100 = journeyDetailsActivity.findViewById(R.id.hundredkm_text);
        AverageSpeed200 = journeyDetailsActivity.findViewById(R.id.twohundredkm_text);

    }

    void updateTotalCounts(){
        database.updateTotalCounts(28,30,40,98,1000,50);
    }

    void insertJourney() {
        // insert journey
        database.insertJourneyDetails("Journey1", 100,100,"Slow");
    }

    void setAverageSpeedValues(){
        // insert journey
        insertJourney();

        // initialize database values
        database.setTotalSpeedValue(10,"Aspd0");
        database.setTotalSpeedValue(40,"Aspd1");
        database.setTotalSpeedValue(30,"Aspd2");
        database.setTotalSpeedValue(20,"Aspd3");

        journeyDetailsActivity.findViews();

        // call function to initialize values for the piechart
        journeyDetailsActivity.setPieChartAverageSpeedValues();
    }

    /**
     * <h1>Tests Following ;</h1>
     * <p>
     * - Aggressive sensor counts , so driver string therefore should be aggressive
     * </p>
     */
    @Test
    public void testDriverTypeStringDetails(){
        // initialize values
        updateTotalCounts();

        // call function
        journeyDetailsActivity.driverTypeDetails();

        // count is mainly aggressive so driverstring will be aggressive
        journeyDetailsActivity.driverTmp = "Aggressive";

    }

    /**
     * <h1>Tests Following ;</h1>
     * <p>
     * - Ensures pie chart values show correct percentages for driver types
     * </p>
     */
    @Test
    public void testPieChartDriverTypeValues(){
        // initialize values
        updateTotalCounts();

        // call function to initialize values for the piechart
        journeyDetailsActivity.driverTypeDetails();
        journeyDetailsActivity.setPieChartDriverTypeValues();

        //ensure driver percentages correlate to values
        assertEquals(journeyDetailsActivity.slowPercentage,28.571428571428573,0);
        assertEquals(journeyDetailsActivity.normalPercentage,30.612244897959183,0);
        assertEquals(journeyDetailsActivity.aggressivePercentage,40.816326530612244,0);

        assertEquals(slowDriverType.getText(),"Slow : 28.57%");
        assertEquals(normalDriverType.getText(),"Normal : 30.61%");
        assertEquals(aggressiveDriverType.getText(),"Aggressive : 40.82%");
    }

    /**
     * <h1>Tests Following ;</h1>
     * <p>
     * - Ensures pie chart values show correct percentages for average speed
     * </p>
     */
    @Test
    public void testPieChartAverageSpeedValues(){

        setAverageSpeedValues();

        //ensure average speeds correlate to values
        assertEquals(journeyDetailsActivity.averageSpeed0Percentage,10,0);
        assertEquals(journeyDetailsActivity.averageSpeed1Percentage,40,0);
        assertEquals(journeyDetailsActivity.averageSpeed2Percentage,30,0);
        assertEquals(journeyDetailsActivity.averageSpeed3Percentage,20,0);

        assertEquals(AverageSpeed050.getText(),"0 - 50km/h : 10%");
        assertEquals(AverageSpeed50.getText(),"50 - 100km/h : 40%");
        assertEquals(AverageSpeed100.getText(),"100 - 200km/h : 30%");
        assertEquals(AverageSpeed200.getText(),"200km/h+ : 20%");

    }

    /**
     * <h1>Tests Following ;</h1>
     * <p>
     * - Ensures testing overall stats displays correct values.
     * </p>
     */
    @Test
    public void testOverallStatValues(){
        // initialize values
        updateTotalCounts();
        insertJourney();

        //assertEquals();
        journeyDetailsActivity.driverTypeDetails();
        journeyDetailsActivity.findViews();
        journeyDetailsActivity.setOverallStats();

        //journeyDetailsActivity.
        assertEquals(distanceTravelled.getText(), "Distance Travelled : 1.00KM");
        assertEquals(driverType.getText(), "Driver Type : Aggressive");
        assertEquals(numberJourneys.getText(),"Number of Journeys : 1");
        assertEquals(averageSpeed.getText(),"Average Speed : 50 KM/H");

    }

    /**
     * <h1>Tests Following ;</h1>
     * <p>
     * - Ensures pie chart actual values display correctly for driver types
     * </p>
     */
    @Test
    public void testDriverTypePieChart(){
        // initialize values
        updateTotalCounts();

        journeyDetailsActivity.pieChartDriverType.clearChart();

        // call function to initialize values for the piechart
        journeyDetailsActivity.driverTypeDetails();
        journeyDetailsActivity.setPieChartDriverTypeValues();

        List<org.eazegraph.lib.models.PieModel> piechart = new ArrayList<>();
        piechart = journeyDetailsActivity.pieChartDriverType.getData();

        assertEquals(piechart.get(0).getValue(),28.571428298950195,0);
        assertEquals(piechart.get(1).getValue(),30.612245559692383,0);
        assertEquals(piechart.get(2).getValue(),40.81632614135742,0);



    }

    /**
     * <h1>Tests Following ;</h1>
     * <p>
     * - Ensures pie chart actual values display correctly for average speed.
     * </p>
     */
    @Test
    public void testAverageSpeedPieChart(){

        setAverageSpeedValues();

        List<org.eazegraph.lib.models.PieModel> piechart = new ArrayList<>();
        piechart = journeyDetailsActivity.pieChartAverageSpeed.getData();

        assertEquals(piechart.get(0).getValue(),10,0);
        assertEquals(piechart.get(1).getValue(),40,0);
        assertEquals(piechart.get(2).getValue(),30,0);
        assertEquals(piechart.get(3).getValue(),20,0);

    }



}