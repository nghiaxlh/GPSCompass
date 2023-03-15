package com.bcg.gpscompass.ui.screen.compass;


import android.hardware.SensorEvent;

public interface CompassListener {
    void setChangeDirectionCompass(SensorEvent sensorEvent);

    void setTextForLocation(double lat, double lon);

    void showViewGoogleMapsCompass();

    void getCalibrate(int calibrate);

    void showWarningCompass();

    void showLocationCompass();

    void showIconLocation();
}
