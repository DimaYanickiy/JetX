package com.kof.jetx;

import android.content.Context;

public interface GifPlayInterface {
    boolean checkInternetAllow();
    boolean checkBattary();
    boolean checkDeveloperMode();
    void organicStart();
    void nonOrganicStart();
    void startGame();
    void startPlay();
    void loadGif(Context context);
    void ifNotFirstRun();
    void ifFirstRun();
    void initAppsFlyer();

}
