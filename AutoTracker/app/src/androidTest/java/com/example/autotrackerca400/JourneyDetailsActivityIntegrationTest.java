package com.example.autotrackerca400;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.internal.runner.junit4.statement.UiThreadStatement.runOnUiThread;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class JourneyDetailsActivityIntegrationTest {

    @Rule
    public ActivityTestRule<JourneyDetailsActivity> journeyDetailsActivityTestRule =
            new ActivityTestRule<>(JourneyDetailsActivity.class);

    JourneyDetailsActivity journeyDetailsActivity;
    DatabaseSQL database;

    @Before
    public void Setup(){
        // initialize database values
        database = new DatabaseSQL(InstrumentationRegistry.getInstrumentation().getTargetContext());
        journeyDetailsActivity = journeyDetailsActivityTestRule.getActivity();
    }

    @Test
    public void overallStatsTest() throws Throwable {
        database.clearDatabaseTable("JourneyDetails");
        database.insertJourneyDetails("Journey1",50,80,"Aggressive");
        database.updateTotalCounts(10,20,30,40,50,60);

        runOnUiThread(() -> journeyDetailsActivity.findViews());
        runOnUiThread(() -> journeyDetailsActivity.setOverallStats());

        onView(withId(R.id.number_of_journeys)).check(matches(withText("Number of Journeys : 1")));
        onView(withId(R.id.distance_travelled)).check(matches(withText("Distance Travelled : 0.05KM")));
        onView(withId(R.id.average_speed)).check(matches(withText("Average Speed : 60 KM/H")));
        onView(withId(R.id.driver_type)).check(matches(withText("Driver Type : Aggressive")));


    }

    @Test
    public void testSetPieDriverTypes() throws Throwable {
        // initialize database values
        database.updateTotalCounts(30,30,40,100,10,50);

        database.insertJourneyDetails("Journey1",50,50,"Slow");

        // call function to initialize values for the piechart
        runOnUiThread(() -> journeyDetailsActivity.driverTypeDetails());
        runOnUiThread(() -> journeyDetailsActivity.setPieChartDriverTypeValues());

        // check that texts are equal to the values initialized.
        onView(withId(R.id.driver_slow)).check(matches(withText("Slow : 30%")));
        onView(withId(R.id.driver_normal)).check(matches(withText("Normal : 30%")));
        onView(withId(R.id.driver_aggressive)).check(matches(withText("Aggressive : 40%")));
    }

    @Test
    public void testSetPieChartAverageSpeed() throws Throwable {
        // initialize database values
        database.insertJourneyDetails("Journey1",50,50,"Slow");

        // initialize database values
        database.setTotalSpeedValue(10,"Aspd0");
        database.setTotalSpeedValue(40,"Aspd1");
        database.setTotalSpeedValue(30,"Aspd2");
        database.setTotalSpeedValue(20,"Aspd3");

        runOnUiThread(() -> journeyDetailsActivity.findViews());

        // call function to initialize values for the piechart
        runOnUiThread(() ->journeyDetailsActivity.setPieChartAverageSpeedValues());

        // call function to initialize values for the piechart
        runOnUiThread(() -> journeyDetailsActivity.setPieChartAverageSpeedValues());

        // check that texts are equal to the values initialized.
        onView(withId(R.id.zerokm_text)).check(matches(withText("0 - 50km/h : 10%")));
        onView(withId(R.id.fiftykm_text)).check(matches(withText("50 - 100km/h : 40%")));
        onView(withId(R.id.hundredkm_text)).check(matches(withText("100 - 200km/h : 30%")));
        onView(withId(R.id.twohundredkm_text)).check(matches(withText("200km/h+ : 20%")));

    }

}