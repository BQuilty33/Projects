package com.example.autotrackerca400;

import static org.junit.Assert.assertEquals;

import android.database.Cursor;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class MainActivityUnitTest {


    @Rule
    public ActivityTestRule<MainActivity> mainActivityActivityTestRule =
            new ActivityTestRule<>(MainActivity.class);
    @Rule
    public ActivityScenarioRule<MainActivity> mActivityRule = new ActivityScenarioRule<>(
            MainActivity.class);


    private MainActivity mainActivity = null;
    private Button strtbutn;
    private Button objdet;
    private TextView vehicleSpeed;
    private TextView speedInput;
    private TextView SpeedLimit;
    private TextView limitInput;

    private LinearLayout mLinearLayout;
    private LinearLayout mLinearLayout2;
    private LinearLayout mLinearLayout3;

    private DatabaseSQL database;

    private ImageView writeSymbol;
    private CardView RenameView;

    private EditText renameEdit;
    private Button ConfirmRename;
    private Button IgnoreRename;

    private TextView journeyName;
    private TextView driverType;
    private TextView averageSpeed;



    @Before
    public void Setup(){
        // get main activity instance
        mainActivity = mainActivityActivityTestRule.getActivity();
        // get buttons
        strtbutn = mainActivity.findViewById(R.id.start_journey);
        objdet = mainActivity.findViewById(R.id.object_detection);
        // get texts
        vehicleSpeed = mainActivity.findViewById(R.id.speed_text);
        speedInput = mainActivity.findViewById(R.id.speed_input);
        SpeedLimit = mainActivity.findViewById(R.id.speed_limit);
        limitInput = mainActivity.findViewById(R.id.limit_input);
        journeyName = mainActivity.findViewById(R.id.journey1);
        driverType = mainActivity.findViewById(R.id.driver_type1);
        averageSpeed = mainActivity.findViewById(R.id.average_speed1);
        // get layouts
        mLinearLayout = mainActivity.findViewById(R.id.journey_linearLayout);
        mLinearLayout2 = mainActivity.findViewById(R.id.journey_linearLayout2);
        mLinearLayout3 = mainActivity.findViewById(R.id.journey_linearLayout3);
        // rename views
        writeSymbol = mainActivity.findViewById(R.id.write_logo1);
        RenameView = mainActivity.findViewById(R.id.rename_cardView);
        renameEdit = mainActivity.findViewById(R.id.rename_editText);
        ConfirmRename = mainActivity.findViewById(R.id.confirm_rename);
        IgnoreRename = mainActivity.findViewById(R.id.ignore_rename);

        // get database instance
        database = new DatabaseSQL(InstrumentationRegistry.getInstrumentation().getTargetContext());
    }
    /**
     * <h1>Tests Following ;</h1>
     * <p>
     * - Ensures correct visibility for views change when starting journey
     * </p>
     */
    @Test
    public void startJourneyVisibility() {


        objdet = mainActivity.findViewById(R.id.object_detection);

        // ensure original visibility
        assertEquals(objdet.getVisibility(),View.VISIBLE);
        assertEquals(vehicleSpeed.getVisibility(),View.INVISIBLE);
        assertEquals(speedInput.getVisibility(),View.INVISIBLE);
        assertEquals(SpeedLimit.getVisibility(),View.INVISIBLE);
        assertEquals(limitInput.getVisibility(),View.INVISIBLE);

        strtbutn.performClick();

        // ensure new visibility
        assertEquals(objdet.getVisibility(),View.GONE);
        assertEquals(vehicleSpeed.getVisibility(),View.VISIBLE);
        assertEquals(speedInput.getVisibility(),View.VISIBLE);
        assertEquals(SpeedLimit.getVisibility(),View.VISIBLE);
        assertEquals(limitInput.getVisibility(),View.VISIBLE);

    }

    /**
     * <h1>Tests Following ;</h1>
     * <p>
     * - Ensures correct views visibility + texts change when ending journey
     * </p>
     */
    @Test
    public void endJourneyVisibility() throws InterruptedException {
        // ensure original visibility + view values
        assertEquals(mLinearLayout.getVisibility(),View.INVISIBLE);
        assertEquals(journeyName.getText(), "");
        assertEquals(driverType.getText(), "Driver Type:");
        assertEquals(averageSpeed.getText(), "Average speed:");
        // original button is start
        assertEquals(strtbutn.getText().toString(), "▶︎ Start the journey");
        // click start button
        strtbutn.performClick();
        // button now end journey
        assertEquals(strtbutn.getText().toString(), "\uD83D\uDED1 End the journey");
        // register journey
        Thread.sleep(10000);
        // click end button
        strtbutn.performClick();
        // button now start button again
        assertEquals(strtbutn.getText().toString(), "▶︎ Start the journey");
        // check visibility + values changed
        //assertEquals(mLinearLayout.getVisibility(),View.INVISIBLE);
        //assertEquals(journeyName.getText(), "Journey1");
        //assertEquals(averageSpeed.getText(), "Average speed: 0 KM/H");

    }

    /**
     * <h1>Tests Following ;</h1>
     * <p>
     * - Ensures view will show speed change when set.
     * </p>
     */
    @Test
    public void startJourneyFunctionality(){
        // ensure original values
        assertEquals(speedInput.getText(),"0KM/H");
        assertEquals(limitInput.getText(),"0KM/H");

        // journey started and speed has changed
        strtbutn.performClick();
        mainActivity.setSpeedText(80, String.valueOf(90));

        // ensure change has occurred
        assertEquals(speedInput.getText(),"80KM/H");
        assertEquals(limitInput.getText(),"90KM/H");

    }
    /**
     * <h1>Tests Following ;</h1>
     * <p>
     * - Ensures views at bottom of screen show when journeys created
     * </p>
     */
    @Test
    public void individualJourneysVisibility() throws InterruptedException {
        // check original visibility + values.
        assertEquals(mLinearLayout.getVisibility(),View.INVISIBLE);
        assertEquals(mLinearLayout2.getVisibility(),View.INVISIBLE);
        assertEquals(mLinearLayout3.getVisibility(),View.INVISIBLE);

        assertEquals(database.getCountJourney(),0);

        // insert new journeys
        database.insertJourneyDetails("journey1",500,50,"Slow");
        database.insertJourneyDetails("journey2",500,70,"Normal");

        assertEquals(database.getCountJourney(),2);

        // check individual journeys showing
        mainActivity.setViews();
        assertEquals(mLinearLayout.getVisibility(),View.VISIBLE);
        assertEquals(mLinearLayout2.getVisibility(),View.VISIBLE);
        assertEquals(mLinearLayout3.getVisibility(),View.INVISIBLE);


    }
    /**
     * <h1>Tests Following ;</h1>
     * <p>
     * - Ensures rename card displayed when clicking rename button
     * </p>
     */
    @Test
    public void checkRenameVisibility(){
        // ensure original visibility
        assertEquals(RenameView.getVisibility(), View.INVISIBLE);

        // click write symbol
        writeSymbol.performClick();

        // ensure rename view visibility
        assertEquals(RenameView.getVisibility(), View.VISIBLE);


    }
    /**
     * <h1>Tests Following ;</h1>
     * <p>
     * - Ensures journey details views change when adding journey
     * </p>
     */
    @Test
    public void checkJourneyDetailsViews(){
        // ensure original values
        assertEquals(journeyName.getText(), "");
        assertEquals(driverType.getText(), "Driver Type:");
        assertEquals(averageSpeed.getText(), "Average speed:");
        // initialize journey
        database.insertJourneyDetails("JourneyOne",800,5000,"Slow");
        // check update textviews
        mainActivity.setViews();
        assertEquals(journeyName.getText(), "JourneyOne");
        assertEquals(driverType.getText(), "Driver type: Slow");
        assertEquals(averageSpeed.getText(), "Average speed: 5000 KM/H");
    }

    /**
     * <h1>Tests Following ;</h1>
     * <p>
     * - Ensures you can rename a journey and it will be displayed
     * </p>
     */
    @Test
    public void checkRenameConfirmFunctionality(){
        // ensure original rename text is empty
        assertEquals(renameEdit.getText().toString(),"");
        // initialize journey
        database.insertJourneyDetails("journey1",500,50,"Slow");
        // click write symbol
        writeSymbol.performClick();
        // set journey name + confirm
        renameEdit.setText("Journey2");
        ConfirmRename.performClick();
        // ensure journey has been renamed
        Cursor cursor = database.getJourneyDetail("journeyName",null);
        cursor.moveToNext();
        assertEquals(cursor.getString(0),"Journey2");
    }

    /**
     * <h1>Tests Following ;</h1>
     * <p>
     * - Ensures you can ignore the rename card
     * </p>
     */
    @Test
    public void checkRenameIgnoreFunctionality(){
        // click write symbol
        writeSymbol.performClick();
        // ensure rename view visibility
        assertEquals(RenameView.getVisibility(), View.VISIBLE);
        // ignore rename
        IgnoreRename.performClick();
        // ensure visibility now invisible.
        assertEquals(RenameView.getVisibility(), View.INVISIBLE);
    }



}