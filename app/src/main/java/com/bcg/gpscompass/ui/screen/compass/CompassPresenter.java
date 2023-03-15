package com.bcg.gpscompass.ui.screen.compass;

import android.hardware.SensorEvent;

import com.bcg.gpscompass.ui.base.BasePresenter;

public class CompassPresenter extends BasePresenter<CompassListener> {
    public CompassPresenter(CompassListener view) {
        super.getAttachView(view);
    }

    public void changeDirection(SensorEvent sensorEvent) {
        mView.setChangeDirectionCompass(sensorEvent);
    }

    public void setTextLocation(double latitude, double longitude) {
        mView.setTextForLocation(latitude, longitude);
    }

    public void openViewGoogleMaps() {
        mView.showViewGoogleMapsCompass();
    }

    public void calibrateCompass(int calibrate) {
        mView.getCalibrate(calibrate);
    }

    public void openWarning() {
        mView.showWarningCompass();
    }

    public void openLocation() {
        mView.showLocationCompass();
    }

    public void showIconLocation() {
        if (mView != null) {
            mView.showIconLocation();
        }
    }
}