package com.example.autotrackerca400;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.models.PieModel;

import java.util.Locale;

/**
 * <h1>Displays Following ;</h1>
 * <p>
 * - Each journey + stats of journey(Name,averagespeed,totaldistance,drivertype}.
 * </p><p>
 * - Percentage of each driver type in pie chart form.
 *</p>
 */

public class IndividualJourneysActivity extends AppCompatActivity {

    private ConstraintLayout individualJourney;
    private CardView renameCardJourney;
    private Button renameConfirm;
    private Button renameIgnore;
    EditText renameText;
    DatabaseSQL database;
    public int renameInt = 1;
    private boolean journeyOn = false;

    public CardView deleteView;
    private Button confirmDelete;
    private Button ignoreDelete;
    private SearchView journeysSearchView;

    private int deleteIndex = 2;

    private ImageView backButton;

    private TextView individualJourneyTitle;
    private TextView distanceTravelledText;
    private TextView averageSpeedText;
    private TextView driverTypeText;

    private TextView driverSlowText;
    private TextView driverNormalText;
    private TextView driverAggressiveText;
    private TextView exitJourney;

    RecyclerView recyclerView;

    JourneyCardManager journeyCardAdapter;

    PieChart pieChartDriverTypes;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_individual_journeys);

        // initialize database class
        database = new DatabaseSQL(this);

        // list all individual journeys
        createJourneys("");

        // initialize buttons by ids
        findButtonViews();

        // initialize remaining views
        findViews();

        // add functionality when buttons are pressed
        buttonFunctionality();

    }

    public void findViews(){
        // find all remaining views
        renameCardJourney = findViewById(R.id.rename_cardView);
        individualJourney = findViewById(R.id.journey_details_layout);
        renameText = findViewById(R.id.rename_editText);
        deleteView = findViewById(R.id.delete_cardView);
        individualJourneyTitle = findViewById(R.id.title_journey);
        distanceTravelledText = findViewById(R.id.distance_travelled_text);
        averageSpeedText = findViewById(R.id.average_speed_text);
        driverTypeText = findViewById(R.id.driver_type_individual);
        pieChartDriverTypes = findViewById(R.id.piechart_driverType);
        driverSlowText = findViewById(R.id.driver_slow);
        driverNormalText = findViewById(R.id.driver_normal);
        driverAggressiveText = findViewById(R.id.driver_aggressive);
        exitJourney = findViewById(R.id.exit_journey);
    }

    public void setViews(String JourneyNameTitle,String DistanceTravelled, String AverageSpeed, String DriverType){
        // underline journey title
        SpannableString content = new SpannableString(JourneyNameTitle);
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        individualJourneyTitle.setText(content);
        distanceTravelledText.setText(DistanceTravelled);
        averageSpeedText.setText(AverageSpeed);
        driverTypeText.setText(DriverType);
    }

    public void setPieDriverTypes(float SlowPercentage, float NormalPercentage, float AggressivePercentage){
        // set text values
        driverSlowText.setText("Slow : " + SlowPercentage + "%");
        driverNormalText.setText("Normal : " + NormalPercentage + "%");
        driverAggressiveText.setText("Aggressive : " + AggressivePercentage + "%");
        // set pie chart values
        pieChartDriverTypes.addPieSlice(
                new PieModel(
                        "Slow",
                        (float) SlowPercentage,
                        Color.parseColor("#FFA351FF")));
        pieChartDriverTypes.addPieSlice(
                new PieModel(
                        "Normal",
                        NormalPercentage,
                        Color.parseColor("#FFBE7BFF")));
        pieChartDriverTypes.addPieSlice(
                new PieModel(
                        "Aggressive",
                        AggressivePercentage,
                        Color.parseColor("#EED971FF")));
        pieChartDriverTypes.startAnimation();
    }

    public void findButtonViews(){
        confirmDelete = findViewById(R.id.confirm_delete);
        ignoreDelete = findViewById(R.id.ignore_delete);
        journeysSearchView = findViewById(R.id.search_view);
        backButton = findViewById(R.id.image_back);
        renameConfirm = findViewById(R.id.confirm_rename);
        renameIgnore = findViewById(R.id.ignore_rename);
    }

    public void buttonFunctionality(){
        // exit from journey screen
        exitJourney.setOnClickListener(view -> individualJourney.setVisibility(View.INVISIBLE)
        );
        // go to previous activity
        backButton.setOnClickListener(view -> {
            Intent JourneyActivityIntent = new Intent(IndividualJourneysActivity.this, JourneyDetailsActivity.class);
            IndividualJourneysActivity.this.startActivity(JourneyActivityIntent);
        });

        // search for individual journeys based on input string
        journeysSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                createJourneys(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        // ignore delete button
        ignoreDelete.setOnClickListener(view -> deleteView.setVisibility(View.INVISIBLE));

        // functionality for confirming deletion
        confirmDelete.setOnClickListener(view -> {
            database.deleteJourney(deleteIndex);
            database.resetJourneyIds(deleteIndex + 1);
            deleteView.setVisibility(View.INVISIBLE);
            createJourneys("");
        });

        // ignore renaming of journey
        renameIgnore.setOnClickListener(view -> renameCardJourney.setVisibility(View.INVISIBLE));

        // confirm renaming of journey.
        renameConfirm.setOnClickListener(view -> {
            database.renameJourney(renameText.getText().toString(),renameInt);
            renameCardJourney.setVisibility(View.INVISIBLE);
            createJourneys("");
        });
    }

    public void setDeleteVar(int DeleteIndex){
        deleteIndex = DeleteIndex;
    }


    public void deleteViewOn(){
        deleteView.setVisibility(View.VISIBLE);
    }

    public void renameViewOn(){
        renameCardJourney.setVisibility(View.VISIBLE);
    }

    public void individualJourneyOn(){
        if(journeyOn == false) {
            individualJourney.setVisibility(View.VISIBLE);
            journeyOn = true;
        } else{
            individualJourney.setVisibility(View.INVISIBLE);
            journeyOn = false;
        }
    }

    public void pieChartValues(int Position){
        // get driver type counts
        int slowCount = database.getJourneySensorCount("count_slow",Position);
        int normalCount = database.getJourneySensorCount("count_normal",Position);
        int aggressiveCount = database.getJourneySensorCount("count_aggr",Position);
        int totalCount = database.getJourneySensorCount("total_count",Position);
        // get percentages
        double slowPercentage = (double) (slowCount * 100) / totalCount;
        double normalPercentage = (double) (normalCount * 100) / totalCount;
        double aggressivePercentage = (double) (aggressiveCount * 100) / totalCount;
        //setPieDriverTypes
        setPieDriverTypes((float) slowPercentage, (float) normalPercentage, (float) aggressivePercentage);
    }

    public void journeyStats(int Position){
        // get journeyname detail from database
        // get journey name
        Cursor cursorJourneyName = database.getJourneyDetail("journeyName",Position);
        cursorJourneyName.moveToNext();
        String journeyNameIndividual = "" + cursorJourneyName.getString(0);
        // get distance travelled
        Cursor cursorDistanceTravelled = database.getJourneyDetail("distTravelled",Position);
        cursorDistanceTravelled.moveToNext();
        String distanceTravelledText = "Distance travelled : " + String.format("%.2f", ( cursorDistanceTravelled.getFloat(0) / MainActivity.speedDivision) / 1000) + MainActivity.speedMetric;
        // get average speed
        Cursor cursorAverageSpeed = database.getJourneyDetail("averageSpeed",Position);
        cursorAverageSpeed.moveToNext();
        String averageSpeedText = "Average speed : " + ( Math.round( cursorAverageSpeed.getInt(0) / MainActivity.speedDivision )) + MainActivity.speedMetric + "/H";
        // get driver type
        Cursor cursorDriverType = database.getJourneyDetail("driverType",Position);
        cursorDriverType.moveToNext();
        String driverTypeText = "Driver type : " + cursorDriverType.getString(0);
        // set values to views.
        setViews(journeyNameIndividual,distanceTravelledText,averageSpeedText,driverTypeText);
        // set pie chart values for driver type
        pieChartValues(Position);
    }

    public RecyclerView createJourneys(String SearchTerm){

        // journeycount to defined size of journeynames array
        int journeyCount = database.getCountJourney();
        String[] journeyNames = new String[journeyCount];

        // get journeyname detail from database
        Cursor cursorDetailName = database.getJourneyDetail("journeyName",null);
        int ii = 0;

        // count overall journeys that match string
        int searchJourneyStringMatch = 0;

        // get each journey name from every journey
        while (cursorDetailName.moveToNext()) {
            String journeyName = cursorDetailName.getString(0).toLowerCase(Locale.ROOT);
            if(journeyName.contains(SearchTerm.toLowerCase(Locale.ROOT))) {
                journeyNames[ii] = cursorDetailName.getString(0);
                searchJourneyStringMatch += 1;
            }
            ii += 1;
        }

        // list of all journeys
        JourneyCard[] journeyCardData = new JourneyCard[searchJourneyStringMatch];
        ii = 0;
        while (ii < searchJourneyStringMatch) {
            journeyCardData[ii] = new JourneyCard(journeyNames[ii]);
            ii += 1;
        }
        return setJourneys(journeyCardData);
    }

    public RecyclerView setJourneys(JourneyCard[] JourneyCardData){
        recyclerView = findViewById(R.id.journeys_recyclerView);
        journeyCardAdapter = new JourneyCardManager(JourneyCardData, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(journeyCardAdapter);
        return recyclerView;
    }


}