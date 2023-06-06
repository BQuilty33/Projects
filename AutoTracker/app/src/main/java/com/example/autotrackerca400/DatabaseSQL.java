package com.example.autotrackerca400;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * <h1>Following Functionality ;</h1>
 * <p>
 * - Holds database values for each journey.
 * </p><p>
 * - Holds database values for each sensor value for current journey.
 *</p><p>
 * - Holds database values for each driver type for each journey.
 *</p><p>
 * - Holds database values for total count of each driver type, average speed and distance.
 *</p><p>
 * - Various insert,read and deletion methods for tables.
 * </p>
 */

public class DatabaseSQL extends SQLiteOpenHelper {
    // initialize variable

    private SQLiteDatabase db;

    public DatabaseSQL(Context context) {
        super(context, "Userdata.db", null, 3);
    }


    @Override
    public void onCreate(SQLiteDatabase DB) {
        // create database with array of sensor values and count of percentages + total count
        DB.execSQL("create Table SensorDetails(arraySensor TEXT)");
        // create database table for journeyDetails
        DB.execSQL("create Table JourneyDetails(journeyName Text, driverType Text, averageSpeed REAL, distTravelled REAL, id INTEGER)");
        // create database table for individual journey driver counts
        DB.execSQL("create Table SensorCounts(count_slow INTEGER,count_normal INTEGER,count_aggr INTEGER,total_count INTEGER, id INTEGER)");
        // create database table with total journey counts.
        DB.execSQL("create Table TotalCounts(count_slow INTEGER,count_normal INTEGER,count_aggr INTEGER,total_count INTEGER, total_dist REAL, Avg_speed REAL, Aspd0 INTEGER, Aspd1 INTEGER, Aspd2 INTEGER, Aspd3 INTEGER, id INTEGER)");
        // create database table for login details
        DB.execSQL("create Table UserAccount(UserName Text,Password Text)");
        db = DB;
        initializeTotalCounts();
    }

    @Override
    public void onUpgrade(SQLiteDatabase DB, int i, int ii) {
    }

    public void initializeTotalCounts(){
        if(db == null){
            db = this.getWritableDatabase();
        }
        // initialise table
        ContentValues totalSensorCountValue = new ContentValues();
        // originally string will be converted to array later
        totalSensorCountValue.put("total_dist", 0);
        totalSensorCountValue.put("Avg_speed", 0);
        totalSensorCountValue.put("count_slow", 0);
        totalSensorCountValue.put("count_normal", 0);
        totalSensorCountValue.put("count_aggr", 0);
        totalSensorCountValue.put("total_count", 0);
        totalSensorCountValue.put("Aspd0", 0);
        totalSensorCountValue.put("Aspd1", 0);
        totalSensorCountValue.put("Aspd2", 0);
        totalSensorCountValue.put("Aspd3", 0);
        totalSensorCountValue.put("id", 1);
        db.insert("TotalCounts", null, totalSensorCountValue);
    }

    public void createAccount(String UserName, String Password){
        SQLiteDatabase DB = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("UserName", UserName);
        contentValues.put("Password", Password);
        DB.insert("UserAccount", null, contentValues);
    }

    public boolean loginAccount(String UserName, String Password){
        SQLiteDatabase DB = this.getWritableDatabase();
        Cursor accountDetails = DB.rawQuery("Select UserName,Password from UserAccount", null);
        if(accountDetails.getCount() > 0) {
            accountDetails.moveToFirst();
            if (accountDetails.getString(0).equals(UserName) && accountDetails.getString(1).equals(Password)) {
                return true;
            } else {
                return false;
            }
        }
        return false;
    }

    public void clearDatabaseTable(String TableToDelete) {
        SQLiteDatabase DBS = this.getWritableDatabase();
        DBS.execSQL("delete from " + TableToDelete);
    }


    public Boolean insertDriverTypeCounts(Integer SlowCount, Integer NormalCount,Integer aggressiveCount, Integer countTotal){
        // insert the counts for each individual percentages + total count of values into database
        SQLiteDatabase DB = this.getWritableDatabase();
        int journeyID = getCountJourney();
        ContentValues contentValues = new ContentValues();
        contentValues.put("count_slow", SlowCount);
        contentValues.put("count_normal", NormalCount);
        contentValues.put("count_aggr", aggressiveCount);
        contentValues.put("total_count", countTotal);
        contentValues.put("id", journeyID + 1);
        long result = DB.insert("SensorCounts", null, contentValues);
        if(result==-1){
            return false;
        }else{
            return true;
        }
    }

    public Boolean insertJourneyDetails(String JourneyName, double DistanceTravelled, double AverageSpeed, String DriverType){
        SQLiteDatabase DB = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        int journeyID = getCountJourney();
        contentValues.put("journeyName", JourneyName);
        contentValues.put("driverType", DriverType);
        contentValues.put("distTravelled", DistanceTravelled);
        contentValues.put("averageSpeed", AverageSpeed);
        contentValues.put("id", journeyID + 1);
        long result = DB.insert("JourneyDetails", null, contentValues);
        if(result==-1){
            return false;
        }else{
            return true;
        }

    }

    public Boolean insertSensorValues(String SensorValuesArray)
    {
        SQLiteDatabase DB = this.getWritableDatabase();
        // insert sensor values into database
        ContentValues contentValues = new ContentValues();
        // originally string will be converted to array later
        contentValues.put("arraySensor", SensorValuesArray);
        long result = DB.insert("SensorDetails", null, contentValues);
        if(result==-1){
            return false;
        }else{
            return true;
        }
    }

    public Cursor getSensorValues()
    {
        // get sensor values from the database
        SQLiteDatabase DB = this.getWritableDatabase();
        Cursor sensorCursor = DB.rawQuery("Select arraySensor from SensorDetails", null);
        return sensorCursor;
    }

    public int getCountJourney(){
        SQLiteDatabase DB = this.getWritableDatabase();
        Cursor journeyCountCursor = DB.rawQuery("Select journeyName from JourneyDetails", null);
        return journeyCountCursor.getCount();
    }

    public void deleteJourney(int DeleteID){
        // delete journey details
        SQLiteDatabase DB = this.getWritableDatabase();
        String whereClause = "id=?";
        String[] deleteIdArg = new String[] { String.valueOf(DeleteID) };
        DB.delete("JourneyDetails", whereClause, deleteIdArg);
        // delete sensor counts for journey
        DB.delete("SensorCounts", whereClause, deleteIdArg);
    }


    public void renameJourney(String JourneyRename, int JourneyID){
        SQLiteDatabase DB = this.getWritableDatabase();
        String[] renameArgument = new String[]{JourneyRename};
        String renameSQLString = "UPDATE JourneyDetails SET journeyName= ? WHERE id=" + JourneyID;
        DB.execSQL(renameSQLString,renameArgument);
    }

    public void updateTotalCounts(Integer TotalSlow, Integer TotalNormal, Integer TotalAggressive, Integer TotalCount, double TotalDistance, double AverageSpeed){
        SQLiteDatabase DB = this.getWritableDatabase();
        // set arguments from function parameters
        Integer[] countArguments = new Integer[]{TotalSlow, TotalNormal, TotalAggressive, TotalCount};
        Double[] travelValueArguments = new Double[]{TotalDistance, AverageSpeed};

        // create sql strings to be executed within the database
        String countSQLString = "UPDATE TotalCounts SET count_slow= ?,count_normal = ?,count_aggr = ?,total_count = ? WHERE id=" + 1;
        String travelValuesSQLString = "UPDATE TotalCounts SET total_dist = ?, Avg_speed = ? WHERE id=" + 1;

        // update total counts for drivertype sensor values
        DB.execSQL(countSQLString,countArguments);

        // update total distance + average speed of all trips.
        DB.execSQL(travelValuesSQLString,travelValueArguments);
    }

    public void setTotalSpeedValue(Integer SpeedTotal, String SpeedColumn){
        SQLiteDatabase DB = this.getWritableDatabase();

        // set arguments from function parameters
        Integer[] speedArguments = new Integer[]{SpeedTotal};

        // create sql strings to be executed within the database
        String sqlSpeedString = "UPDATE TotalCounts SET " + SpeedColumn + " = ?";

        // update total speed values for pie chart
        DB.execSQL(sqlSpeedString,speedArguments);
    }

    public void resetJourneyIds(int JourneyIdReplaced){
        SQLiteDatabase DB = this.getWritableDatabase();
        int journeyCount = getCountJourney();
        while (JourneyIdReplaced <= journeyCount + 1){
            Integer[] idArguments = new Integer[]{JourneyIdReplaced - 1, JourneyIdReplaced};
            String sqlJourneyDetails = "UPDATE JourneyDetails SET id  = ? Where id = ?";
            DB.execSQL(sqlJourneyDetails,idArguments);
            String sqlSensorDetails = "UPDATE SensorCounts SET id  = ? Where id = ?";
            DB.execSQL(sqlSensorDetails,idArguments);
            JourneyIdReplaced += 1;
        }
    }

    public int getTotalStatsValue(String SensorColumn){
        SQLiteDatabase DB = this.getWritableDatabase();
        Cursor cursor = DB.rawQuery("Select " +  SensorColumn + " from TotalCounts", null);
        cursor.moveToFirst();
        if(cursor.getCount() == 0){
            return 0;
        } else {
            return cursor.getInt(0);
        }
    }

    public float getTotalStatsValueFloat(String SensorColumn){
        SQLiteDatabase DB = this.getWritableDatabase();
        Cursor cursor = DB.rawQuery("Select " +  SensorColumn + " from TotalCounts", null);
        cursor.moveToFirst();
        if(cursor.getCount() == 0){
            return 0;
        } else {
            return cursor.getFloat(0);
        }

    }



    public Cursor getJourneyDetail(String Detail, Integer journeyId){
        SQLiteDatabase DB = this.getWritableDatabase();
        Cursor updateDetailCursor;
        if(journeyId == null) {
            updateDetailCursor = DB.rawQuery("Select " + Detail + " from JourneyDetails", null);
        } else {
            updateDetailCursor = DB.rawQuery("Select " + Detail + " from JourneyDetails WHERE id=" + journeyId, null);
        }
        return updateDetailCursor;
    }

    public int getJourneySensorCount(String SensorDetail, Integer SensorJourneyId){
        SQLiteDatabase DB = this.getWritableDatabase();
        Cursor JourneySensorCursor;
        JourneySensorCursor = DB.rawQuery("Select " + SensorDetail + " from SensorCounts  WHERE id= 0" + SensorJourneyId, null);
        if (JourneySensorCursor.getCount() > 0) {
            JourneySensorCursor.moveToNext();
            return JourneySensorCursor.getInt(0);
        }
        return 0;
    }

    public Cursor getDriverTypeCounts(){
        // get total count of values from database
        SQLiteDatabase DB = this.getWritableDatabase();
        return DB.rawQuery("Select count_slow,count_normal,count_aggr,total_count from TotalCounts", null);
    }

}
