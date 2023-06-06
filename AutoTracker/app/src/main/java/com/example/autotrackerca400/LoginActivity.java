package com.example.autotrackerca400;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

public class LoginActivity extends AppCompatActivity {

    EditText usernameEditText;
    EditText passwordEditText;

    Button loginButton;
    Button createAccountButton;

    Button loginNotification;

    CardView confirmCreate;

    Button confirmCreateAccount;

    Button ignoreCreateAccount;

    DatabaseSQL database;

    CheckBox stayLogged;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        database = new DatabaseSQL(this);
        findViews();
        buttonFunctionality();
    }

    public void findViews(){
        usernameEditText = findViewById(R.id.username_editText);
        passwordEditText = findViewById(R.id.password_editText);
        loginButton = findViewById(R.id.login_account);
        createAccountButton = findViewById(R.id.create_account);
        loginNotification = findViewById(R.id.login_notification);
        confirmCreate = findViewById(R.id.confirm_create_cardView);
        confirmCreateAccount = findViewById(R.id.confirm_create);
        ignoreCreateAccount = findViewById(R.id.ignore_create);
        stayLogged = findViewById(R.id.logged_checkbox);
    }

    public void buttonFunctionality(){
        loginButton.setOnClickListener( view -> {
                if(usernameEditText.getText().toString().trim().length() == 0 || passwordEditText.getText().toString().trim().length() == 0){
                    loginNotification.setVisibility(View.VISIBLE);
                }
            }
        );
        ignoreCreateAccount.setOnClickListener( view -> confirmCreate.setVisibility(View.INVISIBLE)
        );
        createAccountButton.setOnClickListener( view -> {
                    if(usernameEditText.getText().toString().trim().length() == 0 || passwordEditText.getText().toString().trim().length() == 0){
                        loginNotification.setText("Enter password or username!");
                        loginNotification.setVisibility(View.VISIBLE);
                    } else{
                        loginNotification.setVisibility(View.INVISIBLE);
                        confirmCreate.setVisibility(View.VISIBLE);
                    }
                }
        );
        confirmCreateAccount.setOnClickListener( view -> {
                    database.clearDatabaseTable("JourneyDetails");
                    database.clearDatabaseTable("SensorCounts");
                    database.clearDatabaseTable("TotalCounts");
                    database.clearDatabaseTable("UserAccount");
                    database.initializeTotalCounts();
                    database.createAccount(usernameEditText.getText().toString(),passwordEditText.getText().toString());
                    confirmCreate.setVisibility(View.INVISIBLE);
                }
        );
        loginButton.setOnClickListener( view -> {
            boolean login = database.loginAccount(usernameEditText.getText().toString(),passwordEditText.getText().toString());
            if(login) {
                SharedPreferences settings = getApplicationContext().getSharedPreferences("PREFS_NAME", 0);
                SharedPreferences.Editor editor = settings.edit();
                editor.putBoolean("saveDetails", stayLogged.isChecked());
                editor.apply();
                Intent loginDetailsInt = new Intent(LoginActivity.this, MainActivity.class);
                LoginActivity.this.startActivity(loginDetailsInt);
            } else{
                loginNotification.setText("Incorrect username or password!");
                loginNotification.setVisibility(View.VISIBLE);
            }
        });

    }
}