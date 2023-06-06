package com.example.autotrackerca400;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        Handler handlerJourneyLimitNotification = new Handler();
        Runnable runnableJourneyLimitNotification = () -> {
            try {
                goToView();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        };
        handlerJourneyLimitNotification.postDelayed(runnableJourneyLimitNotification,1000);
    }

    public void goToView() throws InterruptedException {
        SharedPreferences settingsGet = getApplicationContext().getSharedPreferences("PREFS_NAME", 0);
        boolean saveDetails = settingsGet.getBoolean("saveDetails", false);
        if (saveDetails){
            Intent mainActivityInt = new Intent(SplashActivity.this, MainActivity.class);
            SplashActivity.this.startActivity(mainActivityInt);
        } else{
            Intent loginActivityInt = new Intent(SplashActivity.this, LoginActivity.class);
            SplashActivity.this.startActivity(loginActivityInt);
        }
    }
}