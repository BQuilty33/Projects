package com.example.autotrackerca400;


import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URL;
import java.util.concurrent.ExecutionException;


/**
 * <h1>Following Functionality;</h1>
 * <p>
 * - Determines Speed limit of current road you are on.
 *</p><p>
 * - Uses current latitude and longitude + uses bing api.
 *</p>
 */

class InternetCheck extends AsyncTask<Void,Void,Boolean> {

    public  InternetCheck() {
    }

    @Override protected Boolean doInBackground(Void... voids) { try {
        Socket sock = new Socket();
        sock.connect(new InetSocketAddress("8.8.8.8", 53), 1500);
        sock.close();
        return true;
    } catch (IOException e) {
        return false;
    } }

    @Override protected void onPostExecute(Boolean internet) {  }
}

public class SpeedLimitManager {

    public static boolean isInternetConnection() throws ExecutionException, InterruptedException {
        InternetCheck internetCheck = new InternetCheck();
        return internetCheck.execute().get();
    }

    public Integer speedLimitThread(String Latitude, String Longitude){
        String urlBingApi = "http://dev.virtualearth.net/REST/V1/Routes/SnapToRoad?points=" + Latitude +"," + Longitude + "&interpolate=false&includeSpeedLimit=true&includeTruckSpeedLimit=false&speedUnit=KPH&travelMode=driving&key=As8vM1g3KvKYfy7gVutkLX7Uv7IU9G34VLKwO7xdXwgNbcHD0jlNea3U_KLCr_7l";
        HttpURLConnection httpConnection = null;
        Integer speedLimitTmp = 0;
        try {
            java.net.URL url = new URL(urlBingApi);
            httpConnection = (HttpURLConnection) url.openConnection();
        } catch (Exception e) {
        }
        try {
            httpConnection.setRequestMethod("GET");
        } catch (Exception e) {
        }
        try {
            BufferedReader httpBingInput = new BufferedReader(
                    new InputStreamReader(httpConnection.getInputStream()));
            String inputLine;
            StringBuffer bingResponse = new StringBuffer();
            while ((inputLine = httpBingInput.readLine()) != null) {
                bingResponse.append(inputLine);
            }

            httpBingInput.close();

            speedLimitTmp = parseJSon(bingResponse.toString());

        } catch (Exception e) {
            //return 0;
        }
        return speedLimitTmp;
    }

    public Integer getSpeedLimit(String Latitude, String Longitude) throws InterruptedException, ExecutionException {
        //code to do the HTTP request
        final Integer[] speedLimit = {0};
        Thread thread = new Thread(() -> speedLimit[0] = speedLimitThread(Latitude,Longitude));
        boolean connected = isInternetConnection();
        if(connected == true) {
            thread.start();
            thread.join();
        } else{
            // unrealistic number to show that no internet
            speedLimit[0] = null;
        }
        return speedLimit[0];

    }
    // Parses the JSON data received from the API
    private int parseJSon(String data) throws JSONException {


        if (data == null) {
            return 0;
        }

        JSONObject jsonData = new JSONObject(data);


        String congestionString = (jsonData.getJSONArray("resourceSets").getJSONObject(0).getJSONArray("resources").getJSONObject(0).getJSONArray("snappedPoints").getJSONObject(0).get("speedLimit")).toString();



        return Integer.parseInt(congestionString);
    }
}
