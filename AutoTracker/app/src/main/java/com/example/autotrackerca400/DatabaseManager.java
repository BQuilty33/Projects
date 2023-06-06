package com.example.autotrackerca400;

import android.database.Cursor;
import android.os.Handler;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
/**
 * <h1>Following Functionality ;</h1>
 * <p>
 * - Converts sensor values passed from current journey inside database.
 * </p><p>
 * - Used to predict driver type based on converted values.
 *</p><p>
 * - Analyses values from journey + inserts journey into database.
 *</p><p>
 * - inserts driver type counts, average speed counts, totaldistance.
 *</p>
 */
public class DatabaseManager {

    //initialize variables

    DatabaseSQL database;

    ArrayList<String[]> driverList = new ArrayList<>();
    int[] driversPercentage = new int[8];


    MainActivity mainActivity;

    Handler handlerSpeedLimit;

    int journeyCount;

    // constructor method
    public DatabaseManager(DatabaseSQL DatabaseSQL, MainActivity MainActivity){
        // set variables to parameters passed through
        database = DatabaseSQL;
        this.mainActivity = MainActivity;
        journeyCount = database.getCountJourney();
    }

    boolean insertSensorValues(String SensorValue){
        // add value to database from parameter
        return database.insertSensorValues(SensorValue);
    }

    public void resetDriverList(){
        // reset driver list so it can be re-used
        driverList.clear();
    }

    void convertSensorValues(){
        // get sensor values from the database
        Cursor sensorCursor = database.getSensorValues();
        while(sensorCursor.moveToNext()){
            // sensor value as string
            String sensorValue = sensorCursor.getString(0);
            // 6 string values converted to array by separator ","
            if(sensorValue != null) {
                String[] driverValues = convertStringToArray(sensorValue);
                // add to array of arrays of sensor values
                driverList.add(driverValues);
            }
        }
    }

    int[] getPreviousDriverTypeCounts(){
        // get the count of each percentage and total count
        Cursor resCount = database.getDriverTypeCounts();
        // initialize previous values array
        int[] previousValues = new int[4];
        // loop through cursor object
        while (resCount.moveToNext()) {
            // loop through columns in cursor
            int ii = 0;
            while (ii < resCount.getColumnCount()){
                // set previous count values to initialized array
                previousValues[ii] = resCount.getInt(ii);
                ii += 1;
            }
        }
        return previousValues;
    }

    int[] drivingStylePercentage(int[] PreviousValues) throws Exception {
        // initalize python object
        InputStream targetStream = null;
        try {
            targetStream = mainActivity.getAssets().open("driving_behaviour_dataset.csv");
        } catch (IOException e) {
            e.printStackTrace();
        }
        DrivingTypeModel model = new DrivingTypeModel();
        driversPercentage = model.PredictDriverType(targetStream,driverList,PreviousValues[0], PreviousValues[1], PreviousValues[2], PreviousValues[3]);
        return driversPercentage;
    }

    String getDriverTypeString(int[] DriversPercentage){
        String driverType = "";
        if(DriversPercentage[0] > DriversPercentage[1] && DriversPercentage[0] > DriversPercentage[2] ){
            driverType = "Slow";
        }
        else if(DriversPercentage[1] > DriversPercentage[0] && DriversPercentage[1] > DriversPercentage[2] ){
            driverType = "Normal";
        } else if (DriversPercentage[2] > DriversPercentage[0] && DriversPercentage[2] > DriversPercentage[1] ) {
            driverType = "Aggressive";
        }
        return driverType;
    }
    void setSpeedValuesDatabase(int AverageSpeed) {
        // set speed total for pie chart
        if(AverageSpeed >= 0 && AverageSpeed < 50){
            database.setTotalSpeedValue(database.getTotalStatsValue("Aspd0") + 1,"Aspd0");
        } if (AverageSpeed >= 50 && AverageSpeed < 100){
            database.setTotalSpeedValue(database.getTotalStatsValue("Aspd1") + 1,"Aspd1");
        } if (AverageSpeed >= 100 && AverageSpeed < 200){
            database.setTotalSpeedValue(database.getTotalStatsValue("Aspd2") + 1,"Aspd2");
        } if (AverageSpeed >= 200){
            database.setTotalSpeedValue(database.getTotalStatsValue("Aspd3") + 1,"Aspd3");
        }
    }

    void insertDatabase(ArrayList<Double> TravelValues, String DriverType, double AverageSpeed){
        // insert the total count + total percentage count to database
        database.insertDriverTypeCounts(driversPercentage[0], driversPercentage[1],driversPercentage[2],driversPercentage[3]);
        double currentTotalDist = database.getTotalStatsValue("total_dist");
        // add to total count of values overall
        database.updateTotalCounts(driversPercentage[4], driversPercentage[5],driversPercentage[6],driversPercentage[7],currentTotalDist + TravelValues.get(0),  AverageSpeed);
        //get total count of journeys
        int journeyCount = database.getCountJourney();
        String journeyName = "Journey" + (journeyCount + 1);
        // insert journeydetails
        database.insertJourneyDetails(journeyName,TravelValues.get(0),TravelValues.get(1),DriverType);
    }

    void analyzeVehicle(ArrayList<Double> TravelValues) {
        // convert all sensorvalues from string to array
        convertSensorValues();
        // initialize previous values array
        int[] previousValues;
        // get the count of each percentage and total count
        previousValues = getPreviousDriverTypeCounts();
        handlerSpeedLimit = new Handler();
        Runnable runnableSpeedLimit = new Runnable() {
            public void run() {
                try {
                    // get percentage of each driving style
                    drivingStylePercentage(previousValues);
                    // get drivertype in string format
                    String driverType = getDriverTypeString(driversPercentage);
                    journeyCount = database.getCountJourney();
                    // get average speed
                    double averageSpeedTmp = database.getTotalStatsValue("Avg_speed") * journeyCount;
                    double averageSpeed;
                    if(journeyCount > 0) {
                        averageSpeed = (averageSpeedTmp + TravelValues.get(1)) / (journeyCount + 1);
                    } else {
                        averageSpeed = TravelValues.get(1);
                    }
                    // get average speed and set speed values into database
                    setSpeedValuesDatabase(TravelValues.get(1).intValue());
                    // insert counts into database
                    // insert details of journey into database
                    insertDatabase(TravelValues,driverType,averageSpeed);
                    MainActivity.setViews();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        handlerSpeedLimit.post(runnableSpeedLimit);
    }

    public static String[] convertStringToArray(String String){
        String[] array = String.split(",");
        return array;
    }

}
