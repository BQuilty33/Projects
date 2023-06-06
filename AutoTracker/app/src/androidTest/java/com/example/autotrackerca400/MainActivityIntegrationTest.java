package com.example.autotrackerca400;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.pressKey;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.internal.runner.junit4.statement.UiThreadStatement.runOnUiThread;
import static org.junit.Assert.assertEquals;

import android.view.KeyEvent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class MainActivityIntegrationTest {


    @Rule
    public ActivityTestRule<MainActivity> mainActivityActivityTestRule =
            new ActivityTestRule<>(MainActivity.class);
    @Rule
    public ActivityScenarioRule<MainActivity> mActivityRule = new ActivityScenarioRule<>(
            MainActivity.class);


    private MainActivity mainActivity = null;

    private DatabaseSQL database;




    @Before
    public void Setup() throws Throwable {
        // get main activity instance
        mainActivity = mainActivityActivityTestRule.getActivity();

        // get database instance
        database = new DatabaseSQL(InstrumentationRegistry.getInstrumentation().getTargetContext());
        database.clearDatabaseTable("JourneyDetails");
        runOnUiThread(() -> mainActivity.setViews());
    }

    @Test
    public void startJourneyVisibility() throws Throwable {

        // ensure original visibility
        onView(withId(R.id.object_detection)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        onView(withId(R.id.speed_text)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.INVISIBLE)));
        onView(withId(R.id.speed_input)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.INVISIBLE)));
        onView(withId(R.id.speed_limit)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.INVISIBLE)));
        onView(withId(R.id.limit_input)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.INVISIBLE)));



        onView(withId(R.id.start_journey)).perform(click());

        // ensure new visibility
        onView(withId(R.id.object_detection)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)));
        onView(withId(R.id.speed_text)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        onView(withId(R.id.speed_input)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        onView(withId(R.id.speed_limit)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        onView(withId(R.id.limit_input)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
    }

    @Test
    public void endJourneyVisibility() throws Throwable {
        database.clearDatabaseTable("JourneyDetails");
        // ensure original visibility + view values
        runOnUiThread(() -> mainActivity.setViews());
        onView(withId(R.id.journey_linearLayout)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.INVISIBLE)));

        onView(withId(R.id.journey1)).check(matches(withText("")));
        onView(withId(R.id.driver_type1)).check(matches(withText("Driver Type:")));
        onView(withId(R.id.average_speed1)).check(matches(withText("Average speed:")));
        // original button is start
        onView(withId(R.id.start_journey)).check(matches(withText("▶︎ Start the journey")));
        // click start button
        onView(withId(R.id.start_journey)).perform(click());
        // register journey
        Thread.sleep(1000);
        // button now end journey
        onView(withId(R.id.start_journey)).check(matches(withText("\uD83D\uDED1 End the journey")));
        // click end button
        onView(withId(R.id.start_journey)).perform(click());
        // button now start button again
        onView(withId(R.id.start_journey)).check(matches(withText("▶︎ Start the journey")));
        // check visibility + values changed
        onView(withId(R.id.journey_linearLayout)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        onView(withId(R.id.journey1)).check(matches(withText("Journey1")));
        onView(withId(R.id.average_speed1)).check(matches(withText("Average speed: 0 KM/H")));

        database.clearDatabaseTable("JourneyDetails");
    }

    @Test
    public void startJourneyFunctionality() throws Throwable {
        // ensures speed change is functional.

        // ensure original values
        onView(withId(R.id.speed_input)).check(matches(withText("0KM/H")));
        onView(withId(R.id.limit_input)).check(matches(withText("0KM/H")));

        // journey started and speed has changed
        //runOnUiThread(() -> strtbutn.performClick());
        onView(withId(R.id.start_journey)).perform(click());
        runOnUiThread(() ->mainActivity.setSpeedText(80,"90"));

        onView(withId(R.id.speed_input)).check(matches(withText("80KM/H")));
        onView(withId(R.id.limit_input)).check(matches(withText("90KM/H")));

    }
    @Test
    public void indvJourneysVisibility() throws Throwable {
        // check original visibility + values.
        onView(withId(R.id.journey_linearLayout)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.INVISIBLE)));
        onView(withId(R.id.journey_linearLayout2)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.INVISIBLE)));
        onView(withId(R.id.journey_linearLayout3)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.INVISIBLE)));


        assertEquals(database.getCountJourney(),0);

        // insert new journeys by starting and ending journey
        onView(withId(R.id.start_journey)).perform(click());
        // register journey
        Thread.sleep(11000);
        // end journey
        onView(withId(R.id.start_journey)).perform(click());
        // start new journey
        onView(withId(R.id.start_journey)).perform(click());
        // register journey
        Thread.sleep(11000);
        // end journey
        onView(withId(R.id.start_journey)).perform(click());
        // wait for thread to finish journey
        Thread.sleep(1000);
        assertEquals(database.getCountJourney(),2);

        // check individual journeys showing
        runOnUiThread(() -> mainActivity.setViews());

        onView(withId(R.id.journey_linearLayout)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        onView(withId(R.id.journey_linearLayout2)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));

        database.clearDatabaseTable("JourneyDetails");
    }
    @Test
    public void checkRenameVisibility() throws Throwable {
        // ensure original visibility
        onView(withId(R.id.rename_cardView)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.INVISIBLE)));

        // create new journey
        onView(withId(R.id.start_journey)).perform(click());
        // register journey
        Thread.sleep(11000);
        // now end journey
        onView(withId(R.id.start_journey)).perform(click());

        // rename button
        onView(withId(R.id.write_logo1)).perform(click());

        // ensure rename view visibility
        onView(withId(R.id.rename_cardView)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));

        database.clearDatabaseTable("JourneyDetails");
    }

    @Test
    public void checkJourneyDetailsViews() throws Throwable {
        // ensure original values
        onView(withId(R.id.journey1)).check(matches(withText("")));
        onView(withId(R.id.driver_type1)).check(matches(withText("Driver Type:")));
        onView(withId(R.id.average_speed1)).check(matches(withText("Average speed:")));

        // create new journey
        onView(withId(R.id.start_journey)).perform(click());
        // register journey
        Thread.sleep(1000);
        // now end journey
        onView(withId(R.id.start_journey)).perform(click());

        // check update textviews
        runOnUiThread(() -> mainActivity.setViews());
        onView(withId(R.id.journey1)).check(matches(withText("Journey1")));
        //onView(withId(R.id.driver_type1)).check(matches(withText("Driver type: Slow")));
        onView(withId(R.id.average_speed1)).check(matches(withText("Average speed: 0 KM/H")));

        // clear journeys
        database.clearDatabaseTable("JourneyDetails");
    }

    @Test
    public void checkRenameConfirmFunctionality() throws Throwable {
        // ensure original rename text is empty
        onView(withId(R.id.rename_editText)).check(matches(withText("")));

        // create new journey
        onView(withId(R.id.start_journey)).perform(click());
        // register journey
        Thread.sleep(1000);
        // now end journey
        onView(withId(R.id.start_journey)).perform(click());

        // click rename button
        onView(withId(R.id.write_logo1)).perform(click());

        // set journey name + confirm
        onView(withId(R.id.rename_editText)).perform(click(),replaceText("JourneyNow")).perform(pressKey(KeyEvent.KEYCODE_ENTER));
        // confirm journey name
        onView(withId(R.id.confirm_rename)).perform(click());
        // ensure journey has been renamed
        onView(withId(R.id.journey1)).check(matches(withText("JourneyNow")));
    }

    @Test
    public void checkRenameIgnoreFunctionality() throws InterruptedException {
        // create new journey
        onView(withId(R.id.start_journey)).perform(click());
        // register journey
        Thread.sleep(11000);
        // now end journey
        onView(withId(R.id.start_journey)).perform(click());
        // click rename button
        onView(withId(R.id.write_logo1)).perform(click());
        // ensure rename view visibility
        onView(withId(R.id.rename_cardView)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        // ignore rename
        onView(withId(R.id.ignore_rename)).perform(click());
        // ensure visibility now invisible.
        onView(withId(R.id.rename_cardView)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.INVISIBLE)));

        database.clearDatabaseTable("JourneyDetails");
    }



}