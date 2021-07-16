package com.kof.jetx;

public interface SaveSettingsInterface {
    void initSp();
    String getDot();
    void setDot(String dot);
    String getWhatFirst();
    void setWhatFirst(String whatFirst);
    boolean isFirstRun();
    void setFirstRun(boolean firstRun);
    String getFirstFlyer();
    void setFirstFlyer(String sfflyer);
}
