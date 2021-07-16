package com.kof.jetx;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;

public class SettingsActivity extends AppCompatActivity {
    public int dificult;

    public SharedPreferences sPref;
    public final String SAVED_DIF = "saved_preferences";

    RadioButton rBEasy, rBNormal, rBHard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        rBNormal = (RadioButton)findViewById(R.id.normal);
        rBNormal.setOnClickListener(radioButtonClickListener);

        rBEasy = (RadioButton)findViewById(R.id.easy);
        rBEasy.setOnClickListener(radioButtonClickListener);

        rBHard = (RadioButton)findViewById(R.id.hard);
        rBHard.setOnClickListener(radioButtonClickListener);

        loadIntPref();

        switch (dificult){
            case 1:
                rBEasy.setChecked(true);
                break;
            case 2:
                rBNormal.setChecked(true);
                break;
            case 3:
                rBHard.setChecked(true);
                break;
        }
    }

    View.OnClickListener radioButtonClickListener = v -> {
        RadioButton rb = (RadioButton)v;
        switch (rb.getId()) {
            case R.id.easy:
                dificult = 1;
                break;
            case R.id.normal:
                dificult = 2;
                break;
            case R.id.hard:
                dificult = 3;
                break;
            default:
                break;
        }
        saveIntPref();
    };

    public void saveIntPref(){
        sPref = getSharedPreferences("PREF", MODE_PRIVATE);
        SharedPreferences.Editor ed = sPref.edit();
        ed.putInt(SAVED_DIF, dificult);
        ed.commit();
    }

    public void loadIntPref(){
        sPref = getSharedPreferences("PREF", MODE_PRIVATE);
        dificult = sPref.getInt(SAVED_DIF, 2);
    }

    public int getDificult(){
        return dificult;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
        startActivity(intent);
    }
}