package com.example.autotrackerca400;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.junit.Assert.assertEquals;

import android.view.View;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class IndividualJourneysActivityUnitTest {

    @Rule
    public ActivityTestRule<IndividualJourneysActivity> individualJourneysActivityTestRule =
            new ActivityTestRule<>(IndividualJourneysActivity.class);

    @Rule
    public ActivityTestRule<MainActivity> mainMenuActivityTestRule =
            new ActivityTestRule<>(MainActivity.class);

    @Rule
    public ActivityScenarioRule<IndividualJourneysActivity> individualJourneysActivityScenario = new ActivityScenarioRule<>(
            IndividualJourneysActivity.class);


    private IndividualJourneysActivity individualJourneysActivity = null;

    private SearchView journeySearchView;

    private DatabaseSQL database;

    private JourneyCardManager.ViewHolder vH;

    private RecyclerView recyclerView;


    @Before
    public void Setup(){
        // get individual journeys instance
        individualJourneysActivity = individualJourneysActivityTestRule.getActivity();

        journeySearchView = individualJourneysActivity.findViewById(R.id.search_view);

        vH = individualJourneysActivity.journeyCardAdapter.getViewHolder();

        recyclerView = individualJourneysActivity.findViewById(R.id.journeys_recyclerView);


        // get database instance
        database = new DatabaseSQL(InstrumentationRegistry.getInstrumentation().getTargetContext());

    }

    public void createJourneys(){
        // reset journeys
        database.clearDatabaseTable("JourneyDetails");
        // insert new journeys
        database.insertJourneyDetails("journey1",500,50,"Slow");
        database.insertJourneyDetails("journey2",700,70,"Normal");

        // create journey cards based on new journeys
        individualJourneysActivity.createJourneys("");

    }

    public void interactRecyclerView(){
        // ensure recycler view can be interacted with
        onView(withId(R.id.journeys_recyclerView)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
    }

    public void clickButton(String recylerViewItemId){
        int buttonId = individualJourneysActivity.getResources().getIdentifier(recylerViewItemId,"id", individualJourneysActivity.getPackageName());
        recyclerView.findViewHolderForAdapterPosition(0).itemView.findViewById(buttonId).performClick();
    }

    /**
     * <h1>Tests Following ;</h1>
     * <p>
     * - Ensures no journeys shown as no journeys in database
     * </p>
     */
    @Test
    public void originalJourneyCardVisibility(){
        assertEquals(individualJourneysActivity.journeyCardAdapter.getItemCount(),0);
    }
    /**
     * <h1>Tests Following ;</h1>
     * <p>
     * - Inserts two journeys into database
     * </p><p>
     * - Ensures that there are two journey cards matching database journey title
     * </p>
     */
    @Test
    public void insertJourneyCardVisibility() throws InterruptedException {
        createJourneys();

        // two journey cards created
        assertEquals(individualJourneysActivity.journeyCardAdapter.getItemCount(),2);

        // ensure journey card titles are equal to journey names in database
        assertEquals(individualJourneysActivity.journeyCardAdapter.getJourneyTitle(0), "journey1");
        assertEquals(individualJourneysActivity.journeyCardAdapter.getJourneyTitle(1), "journey2");
    }
    /**
     * <h1>Tests Following ;</h1>
     * <p>
     * - Inserts journeys, searches for wrong journey
     * </p><p>
     * - Ensures there are no journey cards displayed.
     * </p>
     */
    @Test
    public void searchJourneyCardsWrongSearchTerm(){
        createJourneys();

        // create search query + search
        journeySearchView.setQuery("randomSearchTerm",true);

        // no journey cards matching search query.
        assertEquals(individualJourneysActivity.journeyCardAdapter.getItemCount(),0);

    }
    /**
     * <h1>Tests Following ;</h1>
     * <p>
     * - Inserts journeys, searches for right journey
     * </p><p>
     * - Ensures the correct amount of journeys is returned based on search term.
     * </p>
     */
    @Test
    public void searchJourneyCardsRightSearchTerm(){
        // insert new journeys
        database.insertJourneyDetails("journey1",500,50,"Slow");
        database.insertJourneyDetails("journey2",700,70,"Normal");
        database.insertJourneyDetails("vehicleCar",200,20,"Normal");
        database.insertJourneyDetails("vehicleVan",200,20,"Normal");

        // create search query(journey) + search
        journeySearchView.setQuery("journey",true);
        // no journey cards matching search query.
        assertEquals(individualJourneysActivity.journeyCardAdapter.getItemCount(),2);


        // create search query(vehicle) + search
        journeySearchView.setQuery("vehicle",true);

        // two journey cards matching search query.
        assertEquals(individualJourneysActivity.journeyCardAdapter.getItemCount(),2);


        // create search query(1) + search
        journeySearchView.setQuery("1",true);

        // two journey cards matching search query.
        assertEquals(individualJourneysActivity.journeyCardAdapter.getItemCount(),1);
    }

    /**
     * <h1>Tests Following ;</h1>
     * <p>
     * - Inserts journeys, clicks rename journey card(write button). - originally not visible.
     * </p><p>
     * - Ensures it is now visible.
     * </p>
     */
    @Test
    public void renameJourneyCardVisibility() throws Throwable {
        createJourneys();

        CardView renameCard = individualJourneysActivity.findViewById(R.id.rename_cardView);

        interactRecyclerView();

        // ensure original visibility
        assertEquals(renameCard.getVisibility() , View.INVISIBLE);

        // click rename
        recyclerView.findViewHolderForAdapterPosition(0).itemView.findViewById(R.id.write_rename).performClick();

        // ensure new visibility
        assertEquals(renameCard.getVisibility() , View.VISIBLE);

    }

    /**
     * <h1>Tests Following ;</h1>
     * <p>
     * - Ensures that when clicked delete, delete card shows
     * </p>
     */
    @Test
    public void deleteJourneyCardVisibility() throws Throwable {
        createJourneys();

        // ensure recycler view can be interacted with
        interactRecyclerView();

        CardView deleteCard = individualJourneysActivity.findViewById(R.id.delete_cardView);

        // ensure original visibility
        assertEquals(deleteCard.getVisibility() , View.INVISIBLE);

        // click delete button
        recyclerView.findViewHolderForAdapterPosition(0).itemView.findViewById(R.id.image_delete).performClick();

        // ensure new visibility
        assertEquals(deleteCard.getVisibility() , View.VISIBLE);
    }

    /**
     * <h1>Tests Following ;</h1>
     * <p>
     * - Ensures that when clicked on a journey, the journey car displays
     * </p>
     */
    @Test
    public void individualJourneyCardVisibility() {

        ConstraintLayout journeyDetailsLayout = individualJourneysActivity.findViewById(R.id.journey_details_layout);

        database.clearDatabaseTable("JourneyDetails");
        database.clearDatabaseTable("SensorCounts");
        // insert new journeys
        database.insertDriverTypeCounts(20,10,10,40);
        database.insertJourneyDetails("journey1",500,50,"Slow");
        // create journey cards based on new journeys
        individualJourneysActivity.createJourneys("");

        // ensure recycler view can be interacted with
        interactRecyclerView();


        // ensure original visibility
        assertEquals(journeyDetailsLayout.getVisibility() , View.INVISIBLE);

        // click individual journeys screen
        recyclerView.findViewHolderForAdapterPosition(0).itemView.findViewById(R.id.image_next).performClick();

        // ensure new visibility
        assertEquals(journeyDetailsLayout.getVisibility() , View.VISIBLE);

    }

    /**
     * <h1>Tests Following ;</h1>
     * <p>
     * - Ensures that when clicked on rename, rename card displays
     * </p>
     */
    @Test
    public void renameJourneyCardFunctionality() throws Throwable {
        createJourneys();

        interactRecyclerView();

        // click rename
        recyclerView.findViewHolderForAdapterPosition(0).itemView.findViewById(R.id.write_rename).performClick();

        // rename journey
        onView(withId(R.id.rename_editText)).perform(typeText("JourneyNow"));

        // confirm rename journey
        onView(withId(R.id.confirm_rename)).perform(click());

        // ensure journey has been renamed
        RecyclerView recyclerView = individualJourneysActivity.findViewById(R.id.journeys_recyclerView);
        assertEquals(((TextView) recyclerView.findViewHolderForAdapterPosition(0).itemView.findViewById(R.id.title_journey)).getText().toString(),"JourneyNow");

    }



}