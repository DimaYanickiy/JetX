package com.kof.jetx;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;

import java.util.Locale;
import java.util.Random;

public class Game extends AppCompatActivity {
    private boolean running = false;
    private int moneyOnBalance;
    private int money = 0;
    private double koef;
    private double randKoef;
    private double enteredKoef;
    private int dif;
    Random rand;

    public final String SAVED_DIF_BALANCE = "saved_balance";

    TextView koeficient;
    TextView balance;
    TextView textForGamer;
    TextInputEditText textInputKof;
    TextInputEditText textInputSum;

    ImageView rocket;

    private float rocketFirstX, rocketFirstY, rocketX, rocketY;

    int redText;
    int greenText;

    SettingsActivity sa = new SettingsActivity();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        loadIntPref();
        loadBalancePref();

        balance = (TextView)findViewById(R.id.balance);
        balance.setText(Integer.toString(moneyOnBalance));

        if(moneyOnBalance < 50) {
            moneyOnBalance+=500;
            saveBalancePref();
        }

        textForGamer = (TextView)findViewById(R.id.textForGamer);
        koeficient = (TextView)findViewById(R.id.koeficient);
        redText = getResources().getColor(R.color.text);
        greenText = getResources().getColor(R.color.greenText);
        rocket = (ImageView)findViewById(R.id.imageView2);

        rocketFirstX = rocket.getTranslationX();
        rocketFirstY = rocket.getTranslationY();

        Button buttonStartGame = (Button)findViewById(R.id.buttonStartGame);
        buttonStartGame.setOnClickListener(v -> {

            textInputSum = (TextInputEditText)findViewById(R.id.inputSum);

            if(!textInputSum.getText().toString().equals("")){
                money = Integer.parseInt(String.valueOf(textInputSum.getText()));
            } else {
                textForGamer.setText(R.string.enterSum);
            }

            if(!running && money > 0 && money <= moneyOnBalance){
                balance.setText(Integer.toString(moneyOnBalance-money));
                saveBalancePref();
                running = true;
                rocket.setTranslationX(rocketFirstX);
                rocket.setTranslationY(rocketFirstY);
                koef = 1.0;
                gameLoop();
            } else if(money <= moneyOnBalance){
                textForGamer.setText(R.string.notEnoughMoney);
            } else if(running){

            }
        });

        Button buttonStop = (Button)findViewById(R.id.buttonStop);
        buttonStop.setOnClickListener(v -> {
            moneyOnBalance = Integer.parseInt(String.valueOf(balance.getText()));
            if(running && enteredKoef == 0){
                running = false;
                moneyOnBalance += (int)money*koef;
                textForGamer.setTextColor(greenText);
                textForGamer.setText(R.string.youWin);
                saveBalancePref();
                balance.setText(Integer.toString(moneyOnBalance));
            }
        });

        Button buttonStopOnKoef = (Button)findViewById(R.id.buttonStopOnKoef);
        buttonStopOnKoef.setOnClickListener(v -> {

            textInputKof = (TextInputEditText)findViewById(R.id.inputTextKof);
            textInputSum = (TextInputEditText)findViewById(R.id.inputSum);

            if(!textInputSum.getText().toString().equals("")){
                money = Integer.parseInt(String.valueOf(textInputSum.getText()));
            }

            if(!textInputKof.getText().toString().equals("") && money > 0 && money < moneyOnBalance){
                enteredKoef = Double.parseDouble(String.valueOf(textInputKof.getText()));

                if(!running){
                    balance.setText(Integer.toString(moneyOnBalance-money));
                    saveBalancePref();
                    running = true;
                    koef = 1.0;
                    gameLoop();
                }

            } else if(textInputKof.getText().toString().equals("") || money <= 0 || money > moneyOnBalance){
                textForGamer.setText(R.string.enterKofOrSum);
            }
        });
        runTimer();
    }

    private void gameLoop(){
        generateRandomKoefToCrash();
        rocketX = rocketFirstX;
        rocketY = rocketFirstY;
        koeficient.setTextColor(greenText);
    }

    private void gameCrash(){
        running = false;
        rocket.clearAnimation();
        enteredKoef = 0;
        textForGamer.setTextColor(redText);
        textForGamer.setText(R.string.youLose);
        if(randKoef < koef){
            koeficient.setTextColor(redText);
        }
    }

    private void gameStop(){
        if(running){
            running = false;
            rocket.clearAnimation();
            textForGamer.setTextColor(greenText);
            textForGamer.setText(R.string.youWin);
            moneyOnBalance += (int)money*enteredKoef;
            saveBalancePref();
            balance.setText(Integer.toString(moneyOnBalance));
        }
        enteredKoef = 0;
    }
    private void rocketMove(){
        rocketX+=15;
        rocketY-=10;
        if(rocketX >= 765 || rocketY <= -510){
            rocketX = 765;
            rocketY = -510;
        }
        System.out.println(rocketX);
        System.out.println(rocketY);
        rocket.setTranslationX(rocketX);
        rocket.setTranslationY(rocketY);
    }

    private void generateRandomKoefToCrash(){
        rand = new Random();

        switch (dif){
            case 1:
                randKoef = (Double)(rand.nextDouble()*10)%10;
                if(randKoef < 1 && randKoef > 0){
                    randKoef += 1;
                } else if(randKoef < 0){
                    randKoef += 3;
                }
                break;
            case 2:
                randKoef = (Double)(rand.nextDouble()*10)%10;
                if(randKoef < 1 && randKoef > 0){
                    randKoef += 1;
                } else if(randKoef < 4 && randKoef > 2.5){
                    randKoef -= rand.nextDouble()%1;
                } else if(randKoef < 0){
                    randKoef += 2;
                } else if(randKoef > 5 && randKoef < 8){
                    randKoef -= 3;
                }
                break;
            case 3:
                randKoef = (Double)(rand.nextDouble()*10)%10;
                if(randKoef < 1 && randKoef > 0){
                    randKoef += 1;
                } else if(randKoef < 4 && randKoef > 2.5){
                    randKoef -= rand.nextDouble()%1;
                } else if(randKoef < 0){
                    randKoef += 2;
                } else if(randKoef > 5 && randKoef < 8){
                    randKoef -= 4;
                } else if(randKoef > 8 && randKoef < 9){
                    randKoef -= 7;
                } else if(randKoef > 9){
                    randKoef -= 8;
                } else if(randKoef > 4 && randKoef < 4.2){
                    randKoef += rand.nextInt()*10;
                }
                break;
        }

        System.out.println(randKoef);
    }

    private void runTimer() {
        final Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (running) {
                    koef+=0.1;
                    rocketMove();
                    if(randKoef < koef){
                        gameCrash();
                    }
                    if(enteredKoef < koef && randKoef > koef && enteredKoef != 0){
                        gameStop();
                    }
                    if(randKoef < enteredKoef && randKoef < koef && enteredKoef != 0){
                        gameCrash();
                    }

                    String text = String.format(Locale.getDefault(), "%.1f", koef);
                    koeficient.setText("x" + text);
                }
                handler.postDelayed(this, 500);
            }
        });
    }

    public void loadIntPref(){
        sa.sPref = getPreferences(MODE_PRIVATE);
        dif = sa.sPref.getInt(sa.SAVED_DIF, 2);
    }

    public void saveBalancePref(){
        sa.sPref = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor ed = sa.sPref.edit();
        ed.putInt(SAVED_DIF_BALANCE, moneyOnBalance);
        ed.commit();
    }

    public void loadBalancePref(){
        sa.sPref = getPreferences(MODE_PRIVATE);
        moneyOnBalance = sa.sPref.getInt(SAVED_DIF_BALANCE, 0);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        saveBalancePref();
        Intent intent = new Intent(Game.this, MainActivity.class);
        startActivity(intent);
    }
}