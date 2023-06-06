package com.example.autotrackerca400;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.models.PieModel;

import java.text.DecimalFormat;

/**
 * <h1>Displays Following ;</h1>
 * <p>
 * - Total stats of journey {drivertype,averagespeed,totaldistance,averagespeed}.
 * </p><p>
 * - Percentage of each driver type in pie chart form of total journeys.
 *</p><p>
 * - Percentage of average speed in pie chart form of total journeys.
 *</p>
 */
public class JourneyDetailsActivity extends AppCompatActivity {

    PieChart pieChartDriverType;
    DatabaseSQL database;
    PieChart pieChartAverageSpeed;

    private TextView distanceTravelledText;
    private TextView averageSpeedText;
    private TextView driverTypeText;
    private TextView numberJourneysText;
    private TextView driverSlowText;
    private TextView driverNormalText;
    private TextView driverAggressiveText;

    private Button goJourneyStatsButton;
    private Button confirmReset;
    private Button ignoreReset;

    private TextView averageSpeed0Text;
    private TextView averageSpeed50Text;
    private TextView averageSpeed100Text;
    private TextView averageSpeed200Text;

    private ImageView backButton;
    ImageView resetStats;

    String driverTmp;
    double slowPercentage;
    double normalPercentage;
    double aggressivePercentage;

    private int journeyCount = 0;

    double averageSpeed0Percentage = 0;
    double averageSpeed1Percentage = 0;
    double averageSpeed2Percentage = 0;
    double averageSpeed3Percentage = 0;

    CardView confirmResetCard;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_journey_dets);

        database = new DatabaseSQL(this);

        // initalize view variables based on id
        findViews();

        // add functionality when buttons are pressed
        buttonFunctionality();

        // set values in views
        setViews();

    }

    public void setViews(){
        // set driver variables
        driverTypeDetails();

        // set overall stats text
        setOverallStats();

        // set average speed text + piechart
        setPieChartAverageSpeedValues();

        // set driver types text + piechart
        setPieChartDriverTypeValues();
    }

    public void driverTypeDetails(){
        // get counts from database
        Cursor getDriverTypeCounts = database.getDriverTypeCounts();

        // set counts, slow normal aggressive
        int i = 0;
        int slowCount = 0;
        int normalCount = 0;
        int aggressiveCount = 0;
        int totalCount = 0;
        while (i < 1) {
            getDriverTypeCounts.moveToNext();
            slowCount = getDriverTypeCounts.getInt(0);
            normalCount = getDriverTypeCounts.getInt(1);
            aggressiveCount = getDriverTypeCounts.getInt(2);
            totalCount =  getDriverTypeCounts.getInt(3);
            i += 1;
        }

        // set driver type string based on counts
        if(slowCount >= normalCount && slowCount >= aggressiveCount){
            driverTmp = "Slow";
        } else if(normalCount >= slowCount && normalCount >= aggressiveCount){
            driverTmp = "Normal";
        } else {
            driverTmp = "Aggressive";
        }

        // get percentages based on counts.
        if(totalCount > 0) {
            slowPercentage = (double) (slowCount * 100) / totalCount ;
            normalPercentage = (double) (normalCount * 100) / totalCount;
            aggressivePercentage = (double) (aggressiveCount * 100) / totalCount;
        }
    }

    public void setOverallStats(){
        numberJourneysText.setText("Number of Journeys : " + journeyCount);
        distanceTravelledText.setText("Distance Travelled : " + String.format("%.2f" , (database.getTotalStatsValueFloat("total_dist") / MainActivity.speedDivision) / 1000)  + MainActivity.speedMetric);
        averageSpeedText.setText("Average Speed : " + Math.round(database.getTotalStatsValue("Avg_speed") / MainActivity.speedDivision) + " " + MainActivity.speedMetric + "/H");
        driverTypeText.setText("Driver Type : " + driverTmp);
    }

    public void setPieChartDriverTypeValues(){

        DecimalFormat df = new DecimalFormat();
        df.setMaximumFractionDigits(2);

        driverSlowText.setText("Slow : " + df.format(slowPercentage) + "%");
        driverNormalText.setText("Normal : " + df.format(normalPercentage) + "%");
        driverAggressiveText.setText("Aggressive : " + df.format(aggressivePercentage) + "%");

        // set pie chart driver types
        setPieDriverTypes((float) slowPercentage, (float) normalPercentage, (float) aggressivePercentage);
    }

    public void setPieChartAverageSpeedValues(){
        if(journeyCount > 0) {
            // get total average speed from database
            int averageSpeed0KM = database.getTotalStatsValue("Aspd0");
            int averageSpeed50KM = database.getTotalStatsValue("Aspd1");
            int averageSpeed100KM = database.getTotalStatsValue("Aspd2");
            int averageSpeed200KM = database.getTotalStatsValue("Aspd3");


            int speedCount = averageSpeed0KM + averageSpeed50KM + averageSpeed100KM + averageSpeed200KM;
            if (speedCount == 0) {
                speedCount = 1;
            }


            averageSpeed0Percentage = (double) (averageSpeed0KM * 100) / speedCount;
            averageSpeed1Percentage = (double) (averageSpeed50KM * 100) / speedCount;
            averageSpeed2Percentage = (double) (averageSpeed100KM * 100) / speedCount;
            averageSpeed3Percentage = (double) (averageSpeed200KM * 100) / speedCount;

            // set textview percentages
            averageSpeed0Text.setText("0 - 50km/h : " + Math.round(averageSpeed0Percentage) + "%" );
            averageSpeed50Text.setText("50 - 100km/h : " + Math.round(averageSpeed1Percentage) + "%" );
            averageSpeed100Text.setText("100 - 200km/h : " + Math.round(averageSpeed2Percentage) + "%" );
            averageSpeed200Text.setText("200km/h+ : " + Math.round(averageSpeed3Percentage) + "%" );

            // set pie chart values
            setPieAverageSpeed((int) Math.round(averageSpeed0Percentage), (int) Math.round(averageSpeed1Percentage), (int) Math.round(averageSpeed2Percentage), (int) Math.round(averageSpeed3Percentage));
        }
    }

    public void buttonFunctionality(){

        resetStats.setOnClickListener(view -> {
            confirmResetCard.setVisibility(View.VISIBLE);
        });

        // go to previous activity
        backButton.setOnClickListener(viewNew -> {
            Intent MainActivityIntent = new Intent(JourneyDetailsActivity.this, MainActivity.class);
            JourneyDetailsActivity.this.startActivity(MainActivityIntent);
        });

        // go to individual journeys activity
        goJourneyStatsButton.setOnClickListener(view1 -> {
            Intent IndividualJourneysIntent = new Intent(JourneyDetailsActivity.this, IndividualJourneysActivity.class);
            JourneyDetailsActivity.this.startActivity(IndividualJourneysIntent);
        });

        confirmReset.setOnClickListener( view -> {
                    // clear all total stats
                    database.clearDatabaseTable("TotalCounts");
                    // re-initialise database values
                    database.initializeTotalCounts();
                    // reset percentages
                    slowPercentage = 0;
                    normalPercentage = 0;
                    aggressivePercentage = 0;
                    // set views
                    setViews();
                    confirmResetCard.setVisibility(View.INVISIBLE);
                }
        );

        ignoreReset.setOnClickListener( view -> confirmResetCard.setVisibility(View.INVISIBLE)
        );

    }

    public void findViews(){
        journeyCount = database.getCountJourney();
        // piechart views
        pieChartDriverType = findViewById(R.id.piechart_driverType);
        pieChartAverageSpeed = findViewById(R.id.piechart_averageSpeed);
        // text views
        distanceTravelledText = findViewById(R.id.distance_travelled);
        averageSpeedText = findViewById(R.id.average_speed);
        driverTypeText = findViewById(R.id.driver_type);
        numberJourneysText = findViewById(R.id.number_of_journeys);
        driverSlowText = findViewById(R.id.driver_slow);
        driverNormalText = findViewById(R.id.driver_normal);
        driverAggressiveText = findViewById(R.id.driver_aggressive);
        averageSpeed0Text = findViewById(R.id.zerokm_text);
        averageSpeed50Text = findViewById(R.id.fiftykm_text);
        averageSpeed100Text = findViewById(R.id.hundredkm_text);
        averageSpeed200Text = findViewById(R.id.twohundredkm_text);
        // button views
        goJourneyStatsButton = findViewById(R.id.individual_journeyStats);
        confirmReset = findViewById(R.id.confirm_reset);
        ignoreReset = findViewById(R.id.ignore_reset);
        backButton = findViewById(R.id.image_back);
        resetStats = findViewById(R.id.image_reset);
        // card view
        confirmResetCard = findViewById(R.id.reset_cardView);
    }

    public void setPieDriverTypes(float SlowPercentage, float NormalPercentage, float AggressivePercentage){
        pieChartDriverType.addPieSlice(
                new PieModel(
                        "Slow",
                        (float) SlowPercentage,
                        Color.parseColor("#FFA351FF")));
        pieChartDriverType.addPieSlice(
                new PieModel(
                        "Normal",
                        NormalPercentage,
                        Color.parseColor("#FFBE7BFF")));
        pieChartDriverType.addPieSlice(
                new PieModel(
                        "Aggressive",
                        AggressivePercentage,
                        Color.parseColor("#EED971FF")));
        pieChartDriverType.startAnimation();
    }

    public void setPieAverageSpeed(int ZeroPercentage, int FiftyPercentage, int OneHundredPercentage, int TwoHundredPercentage){
        pieChartAverageSpeed.addPieSlice(
                new PieModel(
                        "050",
                        ZeroPercentage,
                        Color.parseColor("#2460A7FF")));
        pieChartAverageSpeed.addPieSlice(
                new PieModel(
                        "50",
                        FiftyPercentage,
                        Color.parseColor("#85B3D1FF")));
        pieChartAverageSpeed.addPieSlice(
                new PieModel(
                        "100",
                        OneHundredPercentage,
                        Color.parseColor("#B3C7D6FF")));
        pieChartAverageSpeed.addPieSlice(
                new PieModel(
                        "200",
                        TwoHundredPercentage,
                        Color.parseColor("#D9B48FFF")));
        pieChartAverageSpeed.startAnimation();

    }

}