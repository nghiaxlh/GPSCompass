package com.bcg.gpscompass.utils.sensor;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.hardware.Sensor;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import com.bcg.gpscompass.utils.gps.GpsUtil;

public class SensorManagerCompass {
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private Sensor mMagnetic;
    private Sensor mOrientation;

    private float[] mGravity;
    private float[] mGeoMagnetic;
    private float mAzimuthSensor;
    private int mOrient;
    private Context mContext;
    public SensorManagerCompass(Context context) {
        mSensorManager = (android.hardware.SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager
                .getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mMagnetic = mSensorManager
                .getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        mOrientation = mSensorManager
                .getDefaultSensor(Sensor.TYPE_ORIENTATION);
    }

    public void unregisterListener(SensorEventListener sensorEventListener) {
        mSensorManager.unregisterListener(sensorEventListener);
    }

    public void registerAccelerometerListener(SensorEventListener sensorEventListener) {
        mSensorManager.registerListener(sensorEventListener,
                mAccelerometer, android.hardware.SensorManager.SENSOR_DELAY_UI);
    }

    public void registerMagneListener(SensorEventListener sensorEventListener) {
        mSensorManager.registerListener(sensorEventListener, mMagnetic,
                android.hardware.SensorManager.SENSOR_DELAY_UI);
    }

    public void registerOriListener(SensorEventListener sensorEventListener) {
        mSensorManager.registerListener(sensorEventListener,
                mOrientation, android.hardware.SensorManager.SENSOR_DELAY_GAME);
    }
    public float getAzimuthSensor() {
        return mAzimuthSensor;
    }

    public float[] getGravity() {
        return mGravity;
    }

    public void setGravity(float[] gravity) {
        mGravity = gravity;
    }

    public void setGeoMagnetic(float[] geoMagnetic) {
        mGeoMagnetic = geoMagnetic;
    }

    public void loadAzimuth() {
        if (mGravity != null && mGeoMagnetic != null) {
            float acceler[] = new float[9];
            float mangetic[] = new float[9];
            boolean success = android.hardware.SensorManager.getRotationMatrix(acceler, mangetic, mGravity,
                    mGeoMagnetic);
            if (success) {
                float orientation[] = new float[3];
                android.hardware.SensorManager.getOrientation(acceler, orientation);
                mAzimuthSensor = (float) Math.toDegrees(orientation[0]);
                mAzimuthSensor = convertValue(mAzimuthSensor);
            }
        }
    }

    private float convertValue(float value) {
        if (value < 0) {
            value += 360;
        }
        if (mContext != null) {
            mOrient = GpsUtil.getScreenOrientation(mContext);
            if (mOrient == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
                value += 90;
            } else if (mOrient == ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE) {
                value -= 90;
            }
        }
        return value;
    }
}
