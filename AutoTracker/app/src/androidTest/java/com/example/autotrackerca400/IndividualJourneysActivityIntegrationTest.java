package com.example.autotrackerca400;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.internal.runner.junit4.statement.UiThreadStatement.runOnUiThread;
import static org.junit.Assert.assertEquals;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;
import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


class MyViewAction {

    public static ViewAction clickChildViewWithId(final int id) {
        return new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return null;
            }

            @Override
            public String getDescription() {
                return "Click on a child view with specified id.";
            }

            @Override
            public void perform(UiController uiController, View view) {
                View v = view.findViewById(id);
                v.performClick();
            }

        };
    }

}

@RunWith(JUnit4.class)
public class IndividualJourneysActivityIntegrationTest {

    private DatabaseSQL database;

    @Rule
    public ActivityTestRule<IndividualJourneysActivity> individualJourneysActivityTestRule =
            new ActivityTestRule<>(IndividualJourneysActivity.class);
    @Rule
    public ActivityScenarioRule<IndividualJourneysActivity> mActivityRule = new ActivityScenarioRule<>(
            IndividualJourneysActivity.class);
    private IndividualJourneysActivity individualJourneysActivity = null;



    @Before
    public void Setup() throws Throwable {
        // get database instance
        database = new DatabaseSQL(InstrumentationRegistry.getInstrumentation().getTargetContext());
        individualJourneysActivity = individualJourneysActivityTestRule.getActivity();
    }

    @Test
    public void renameJourneyCardVisibility() throws Throwable {
        database.clearDatabaseTable("JourneyDetails");
        // insert new journeys
        database.insertJourneyDetails("journey1",500,50,"Slow");
        database.insertJourneyDetails("journey2",700,70,"Normal");

        // create journey cards based on new journeys
        runOnUiThread(() -> individualJourneysActivity.createJourneys(""));

        // ensure original visibility
        onView(withId(R.id.rename_cardView)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.INVISIBLE)));

        // click rename
        onView(withId(R.id.journeys_recyclerView))
                .perform(RecyclerViewActions.actionOnItemAtPosition(0, MyViewAction.clickChildViewWithId(R.id.write_rename)));

        // ensure new visibility
        onView(withId(R.id.rename_cardView)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));

    }

    @Test
    public void deleteJourneyCardVisibility() throws Throwable {
        database.clearDatabaseTable("JourneyDetails");
        // insert new journeys
        database.insertJourneyDetails("journey1",500,50,"Slow");
        database.insertJourneyDetails("journey2",700,70,"Normal");

        // create journey cards based on new journeys
        runOnUiThread(() -> individualJourneysActivity.createJourneys(""));

        // ensure original visibility
        onView(withId(R.id.delete_cardView)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.INVISIBLE)));

        // click delete
        onView(withId(R.id.journeys_recyclerView))
                .perform(RecyclerViewActions.actionOnItemAtPosition(0, MyViewAction.clickChildViewWithId(R.id.image_delete)));

        // ensure new visibility
        onView(withId(R.id.delete_cardView)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));

    }

    @Test
    public void individualJourneyCardVisibility() throws Throwable {
        database.clearDatabaseTable("JourneyDetails");
        // insert new journeys
        database.insertJourneyDetails("journey1",500,50,"Slow");
        database.insertJourneyDetails("journey2",700,70,"Normal");
        database.initializeTotalCounts();
        // create journey cards based on new journeys
        runOnUiThread(() -> individualJourneysActivity.createJourneys(""));

        // ensure original visibility
        onView(withId(R.id.journey_details_layout)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.INVISIBLE)));

        // click view journey
        onView(withId(R.id.journeys_recyclerView))
                .perform(RecyclerViewActions.actionOnItemAtPosition(0, MyViewAction.clickChildViewWithId(R.id.image_next)));

        // ensure new visibility
        onView(withId(R.id.journey_details_layout)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));

    }


    @Test
    public void renameJourneyCardFunctionality() throws Throwable {
        database.clearDatabaseTable("JourneyDetails");
        // insert new journeys
        database.insertJourneyDetails("journey1",500,50,"Slow");
        database.insertJourneyDetails("journey2",700,70,"Normal");

        // create journey cards based on new journeys
        runOnUiThread(() -> individualJourneysActivity.createJourneys(""));

        // click rename
        onView(withId(R.id.journeys_recyclerView))
                .perform(RecyclerViewActions.actionOnItemAtPosition(0, MyViewAction.clickChildViewWithId(R.id.write_rename)));

        // rename journey
        onView(withId(R.id.rename_editText)).perform(typeText("JourneyNow"));

        // confirm rename journey
        onView(withId(R.id.confirm_rename)).perform(click());

        // ensure journey has been renamed
        RecyclerView recyclerView = individualJourneysActivity.findViewById(R.id.journeys_recyclerView);
        assertEquals(((TextView) recyclerView.findViewHolderForAdapterPosition(0).itemView.findViewById(R.id.title_journey)).getText().toString(),"JourneyNow");

    }

    @Test
    public void deleteJourneyCardFunctionality() throws Throwable {
        database.clearDatabaseTable("JourneyDetails");

        // insert new journeys
        database.insertJourneyDetails("journey1",500,50,"Slow");
        database.insertJourneyDetails("journey2",700,70,"Normal");

        // create journey cards based on new journeys
        runOnUiThread(() -> individualJourneysActivity.createJourneys(""));

        // ensure two journeys
        assertEquals(individualJourneysActivity.journeyCardAdapter.getItemCount(),2);

        // click delete
        onView(withId(R.id.journeys_recyclerView))
                .perform(RecyclerViewActions.actionOnItemAtPosition(0, MyViewAction.clickChildViewWithId(R.id.image_delete)));

        // confirm delete
        onView(withId(R.id.confirm_delete)).perform(click());

        // ensure now only one journey
        assertEquals(individualJourneysActivity.journeyCardAdapter.getItemCount(),1);
    }


}
