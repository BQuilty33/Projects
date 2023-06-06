package com.example.autotrackerca400;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.hardware.SensorManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.android.material.slider.RangeSlider;

import java.util.ArrayList;
/**
 * <h1>Displays Following ;</h1>
 * <p>
 * - Start and end a journey.
 * </p><p>
 * - Go to object detection screen.
 *</p><p>
 * - Displays last four journey details.
 *</p>
 */

public class MainActivity extends AppCompatActivity {

    // initialize variables

    private static TextView speedText;
    private static TextView speedLimit;
    private static TextView limitInput;
    private static TextView speedInput;


    private static LinearLayout journeyLinearLayout;
    private static LinearLayout journeyLinearLayout2;
    private static LinearLayout journeyLinearLayout3;

    static TextView[] journeyNameImage = new TextView[4];
    static TextView[] driverTypeImage = new TextView[4];
    static TextView[] averageSpeedImage  = new TextView[4];
    ImageView[] writeJourneyImage  = new ImageView[4];

    private CardView renameCardJourney;
    private CardView settingsCardView;

    private Button renameConfirm;
    private Button renameIgnore;
    private Button detectObject;
    private Button journeyDetailsButton;
    Button startJourneyButton;
    private Button journeyLimitNotification;
    private ImageView testVoiceButton;

    private Button confirmSettings;

    private Button logoutButton;

    EditText renameText;
    public int renameInt = 1;
    public static String speedMetric = "KM";
    public static double speedDivision = 1;
    boolean journeyState = false;
    private static int journeyCount;


    public static LocationManager locationManager;
    private SensorListener sensors;
    private MonitorVehicleManager monitorVehicle;
    private DatabaseManager databaseManager;
    private static DatabaseSQL database;
    public MainActivity mainActivity;

    Handler handlerJourneyLimitNotification;
    Runnable runnableJourneyLimitNotification;

    public Intent serviceIntent;

    RangeSlider pitchSlider;
    RangeSlider speechRateSlider;
    static VoiceListener voiceListener;
    String localeString;
    String metricString;
    TextView ignoreSettings;
    ImageView settingsButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        try {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        }
        catch(SecurityException e) {
            e.printStackTrace();
        }



        // initialize classes
        database = new DatabaseSQL(this);
        databaseManager = new DatabaseManager(database,this);
        sensors = new SensorListener((SensorManager)getSystemService(SENSOR_SERVICE),this, databaseManager);
        monitorVehicle = new MonitorVehicleManager(this,locationManager);
        voiceListener = new VoiceListener(this);
        mainActivity = this;

        // initialise lists for settings screen
        setSettings();

        // set values from saved shared preferences.
        assignSavedData();

        // initialize buttons by ids
        findButtonViews();

        // initialize remaining views
        findViews();

        // add functionality when buttons are pressed
        buttonFunctionality();

        // set values for views
        setViews();


    }

    void setSettings(){
        ListView listViewLanguages = findViewById(R.id.list_view);
        String languages[]
                = { "English","French","German","Japanese","Chinese","Italian","Korean" };
        ArrayAdapter<String> arr;
        arr
                = new ArrayAdapter<>(
                this,
                R.layout.support_simple_spinner_dropdown_item,
                languages);
        listViewLanguages.setAdapter(arr);
        listViewLanguages.setOnItemClickListener((adapterView, view, i, l1) -> {
            String s = listViewLanguages.getItemAtPosition(i).toString();
            localeString = s;
        });


        ListView listViewMetric = findViewById(R.id.list_metric);
        String Metrics[]
                = { "KM","MI" };
        ArrayAdapter<String> newArr;
        newArr
                = new ArrayAdapter<>(
                this,
                R.layout.support_simple_spinner_dropdown_item,
                Metrics);
        listViewMetric.setAdapter(newArr);
        listViewMetric.setOnItemClickListener((adapterView, view, i, l1) -> {
            String metric = listViewMetric.getItemAtPosition(i).toString();
            metricString = metric;
        });
    }

    public void assignSavedData(){
        SharedPreferences settingsGet = getApplicationContext().getSharedPreferences("PREFS_NAME", 0);
        float getPitch = settingsGet.getFloat("Pitch", 0);
        float getSpeechRate = settingsGet.getFloat("speechRate",0);
        String getMetric = settingsGet.getString("Metric","");
        String getLocale = settingsGet.getString("Locale","");
        if(getMetric != "") {
            speedMetric = getMetric;
            if (getMetric.equals("KM")) {
                speedDivision = 1;
            } else {
                speedDivision = 1.609;
            }
        }
        voiceListener.SetVoiceParameters(getLocale, getPitch,getSpeechRate);
    }

    public void findViews(){
        // initialize text views
        speedText = findViewById(R.id.speed_text);
        speedLimit = findViewById(R.id.speed_limit);
        renameCardJourney = findViewById(R.id.rename_cardView);
        settingsCardView = findViewById(R.id.settings_cardview);
        limitInput = findViewById(R.id.limit_input);
        speedInput = findViewById(R.id.speed_input);
        renameText = findViewById(R.id.rename_editText);
        ignoreSettings = findViewById(R.id.ignore_settings);



        // slider values
        pitchSlider = findViewById(R.id.pitch_slider);
        speechRateSlider = findViewById(R.id.rate_slider);


        // initialize layout views
        journeyLinearLayout = findViewById(R.id.journey_linearLayout);
        journeyLinearLayout2 = findViewById(R.id.journey_linearLayout2);
        journeyLinearLayout3 = findViewById(R.id.journey_linearLayout3);

        // initialize individual journey views
        int iz = 1;
        while(iz < 4){

            // get ids based on index
            String journeyIdString = "journey" + iz;
            String driverTypeIdString = "driver_type" + iz;
            String averageSpeedIdString = "average_speed" + iz;
            String writeJourneyIdString = "write_logo" + iz;
            int journeyId = getResources().getIdentifier(journeyIdString, "id", getPackageName());
            int driverTypeId = getResources().getIdentifier(driverTypeIdString, "id", getPackageName());
            int averageSpeedId = getResources().getIdentifier(averageSpeedIdString, "id", getPackageName());
            int writeJourneyId = getResources().getIdentifier(writeJourneyIdString, "id", getPackageName());

            // get view based on id obtained
            journeyNameImage[iz - 1] = (findViewById(journeyId));
            driverTypeImage[iz - 1] = ( findViewById(driverTypeId));
            averageSpeedImage[iz - 1] = ( findViewById(averageSpeedId));
            writeJourneyImage[iz - 1] = ( findViewById(writeJourneyId));

            iz += 1;
        }
    }

    @SuppressLint("SetTextI18n")
    public static void setViews(){
        // set original text
        speedText.setText("Speed of vehicle: ");
        speedLimit.setText("Speed limit: ");

        speedInput.setText("0" + speedMetric + "/H");
        limitInput.setText("0" + speedMetric + "/H");

        // get database objects for information
        journeyCount = database.getCountJourney();
        Cursor cursorDetailName = database.getJourneyDetail("journeyName",null);
        Cursor cursorDetailDriverType = database.getJourneyDetail("driverType",null);
        Cursor cursorAverageSpeed = database.getJourneyDetail("averageSpeed",null);

        // make individual journeys objet visible based on count
        int ii = 0;
        while(ii < journeyCount){
            if(ii == 0) {
                journeyLinearLayout.setVisibility(View.VISIBLE);
            } if(ii == 1) {
                journeyLinearLayout2.setVisibility(View.VISIBLE);
            } if(ii == 2) {
                journeyLinearLayout3.setVisibility(View.VISIBLE);
            }
            ii += 1;
        }

        // set textviews based on database objects
        ii = 0;
        while (cursorDetailName.moveToNext() && ii < 3) {
            // set previous count values to initialized array
            journeyNameImage[ii].setText(cursorDetailName.getString(0));
            ii += 1;
        }
        ii = 0;
        while (cursorDetailDriverType.moveToNext() && ii < 3) {
            driverTypeImage[ii].setText("Driver type: " + cursorDetailDriverType.getString(0));
            ii += 1;
        }
        ii = 0;
        while (cursorAverageSpeed.moveToNext() && ii < 3) {
            averageSpeedImage[ii].setText("Average speed: " + Math.round((Integer.valueOf(cursorAverageSpeed.getString(0))) / speedDivision) + " " + speedMetric + "/H");
            ii += 1;
        }
    }

    public void findButtonViews(){
        testVoiceButton = findViewById(R.id.test_sound);
        confirmSettings = findViewById(R.id.confirm_settings);
        journeyDetailsButton =  findViewById(R.id.more_journeyDetails);
        startJourneyButton = findViewById(R.id.start_journey);
        detectObject = findViewById(R.id.object_detection);
        renameConfirm = findViewById(R.id.confirm_rename);
        renameIgnore = findViewById(R.id.ignore_rename);
        journeyLimitNotification = findViewById(R.id.journey_limitExceeded);
        settingsButton = findViewById(R.id.settings_icon);
        logoutButton = findViewById(R.id.logout);
    }

    public void buttonFunctionality(){
        logoutButton.setOnClickListener( view -> {
                    SharedPreferences settings = getApplicationContext().getSharedPreferences("PREFS_NAME", 0);
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putBoolean("saveDetails", false);
                    editor.apply();
                    Intent loginActivityInt = new Intent(MainActivity.this, LoginActivity.class);
                    MainActivity.this.startActivity(loginActivityInt);
                }
        );
        settingsButton.setOnClickListener( view -> {
                    settingsCardView.setVisibility(View.VISIBLE);
                }
        );
        ignoreSettings.setOnClickListener( view -> {
                    settingsCardView.setVisibility(View.INVISIBLE);
                }
        );
        testVoiceButton.setOnClickListener(view -> {
                    voiceListener.TestVoiceParameters(localeString, pitchSlider.getValues().get(0),speechRateSlider.getValues().get(0));
                }
        );
        confirmSettings.setOnClickListener(view -> {
            settingsCardView.setVisibility(View.INVISIBLE);
            // save settings for future
            SharedPreferences settings = getApplicationContext().getSharedPreferences("PREFS_NAME", 0);
            SharedPreferences.Editor editor = settings.edit();
            editor.putFloat("Pitch", pitchSlider.getValues().get(0));
            editor.putFloat("speechRate", speechRateSlider.getValues().get(0));
            if(metricString != null ) {
                editor.putString("Metric", metricString);
                speedMetric = metricString;
                if(metricString == "KM") {
                    speedDivision = 1;
                } else{
                    speedDivision = 1.609;
                }
            }
            if(localeString != null ) {
                editor.putString("Locale", localeString);
            }
            editor.apply();
            voiceListener.SetVoiceParameters(localeString, pitchSlider.getValues().get(0),speechRateSlider.getValues().get(0));
        });
        // go to journeydetails view
        journeyDetailsButton.setOnClickListener(view -> {
            if(serviceIntent != null) {
                stopService(serviceIntent);
            }
            Intent journeyDetailsInt = new Intent(MainActivity.this, JourneyDetailsActivity.class);
            MainActivity.this.startActivity(journeyDetailsInt);
        });

        // ignore rename
        renameIgnore.setOnClickListener(view -> renameCardJourney.setVisibility(View.INVISIBLE));

        // rename functionality
        renameConfirm.setOnClickListener(view -> {
            database.renameJourney(renameText.getText().toString(),renameInt);
            journeyNameImage[renameInt - 1].setText(renameText.getText().toString());
            renameCardJourney.setVisibility(View.INVISIBLE);
        });

        // Rename card journey view
        int i = 0;
        while(i < 3) {
            int finalIi = i;
            writeJourneyImage[i].setOnClickListener(view -> {
                //your stuff
                renameCardJourney.setVisibility(View.VISIBLE);
                renameInt = finalIi + 1;
            });
            i += 1;
        }

        // go to detect objects activity
        detectObject.setOnClickListener(view -> {
            serviceIntent = new Intent(this, LocationService.class);
            startService(serviceIntent);
            Intent detectionIntent = new Intent(MainActivity.this, DetectionActivity.class);
            MainActivity.this.startActivity(detectionIntent);
        });

        // start and end journeys button click
        startJourneyButton.setOnClickListener(view -> {
            // button visibility
            if(!journeyState) {
                detectObject.setVisibility(View.GONE);
                if(journeyCount <= 25) {
                    startJourney();
                } else{
                    journeyLimitNotification.setVisibility(View.VISIBLE);
                    handlerJourneyLimitNotification = new Handler();
                    runnableJourneyLimitNotification = () -> journeyLimitNotification.setVisibility(View.INVISIBLE);
                    handlerJourneyLimitNotification.postDelayed(runnableJourneyLimitNotification, 3500);
                }
            } else{
                try {
                    endJourney();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                detectObject.setVisibility(View.VISIBLE);
            }
        });

    }

    public void startJourney(){
        serviceIntent = new Intent(this, LocationService.class);
        startService(serviceIntent);
        // reset sensor values, allow new values.
        database.clearDatabaseTable("SensorDetails");
        // reset the list to be entered into the database
        databaseManager.resetDriverList();
        // set journey state to true
        monitorVehicle.setJourneyState(true);
        // start the journey service

        monitorVehicle.startLocationService();
        // resume looking for sensor values
        sensors.onResume();
        // button visibility
        startJourneyButton.setVisibility(View.VISIBLE);
        startJourneyButton.setText("\uD83D\uDED1 End the journey");
        speedText.setVisibility(View.VISIBLE);
        speedLimit.setVisibility(View.VISIBLE);
        speedInput.setVisibility(View.VISIBLE);
        limitInput.setVisibility(View.VISIBLE);
        journeyState = true;
    }

    public void endJourney() throws Exception {
        startJourneyButton.setText("▶︎ Start the journey");
        speedText.setVisibility(View.INVISIBLE);
        speedLimit.setVisibility(View.INVISIBLE);
        speedInput.setVisibility(View.INVISIBLE);
        limitInput.setVisibility(View.INVISIBLE);
        journeyState = false;
        stopService(serviceIntent);
        // set journey state to false
        monitorVehicle.setJourneyState(false);
        // stop looking for sensor values
        sensors.onPause();
        // stop location service
        ArrayList<Double> distTr = monitorVehicle.stopLocationService();
        // analyze the values entered into the database
        databaseManager.analyzeVehicle(distTr);
    }


    void setSpeedText(int CurrSpeed , String SpeedLimit){
        int tmpLimit;
        if(speedMetric == "MP"){
            // convert speed limit to mp/h
            tmpLimit = Integer.valueOf(SpeedLimit);
            tmpLimit = (int) (tmpLimit / speedDivision);
            SpeedLimit = String.valueOf(tmpLimit);
        }
        speedInput.setText(Math.round( CurrSpeed / speedDivision) + speedMetric + "/H");
        if(SpeedLimit == "No internet"){
            limitInput.setText(SpeedLimit);
        } else {
            limitInput.setText(SpeedLimit + speedMetric + "/H");
        }
    }



}