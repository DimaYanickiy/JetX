package com.kof.jetx;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnStart = (Button)findViewById(R.id.buttonStart);
        btnStart.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, Game.class);
            startActivity(intent);
        });

        Button btnSettings = (Button)findViewById(R.id.buttonSettings);
        btnSettings.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent);
        });

        Button btnExit = (Button)findViewById(R.id.buttonExit);
        btnExit.setOnClickListener(v -> {
            finish();
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

}