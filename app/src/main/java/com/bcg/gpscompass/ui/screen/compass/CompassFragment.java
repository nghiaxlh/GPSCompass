package com.bcg.gpscompass.ui.screen.compass;

import static android.os.Looper.getMainLooper;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.TranslateAnimation;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.bcg.gpscompass.MainActivity;
import com.bcg.gpscompass.R;
import com.bcg.gpscompass.repository.GpsCompassRepositoryImpl;
import com.bcg.gpscompass.ui.base.BaseFragment;
import com.bcg.gpscompass.ui.screen.AppViewModelFactory;
import com.bcg.gpscompass.ui.screen.location.LocationFragment;
import com.bcg.gpscompass.ui.view.CompassImageView;
import com.bcg.gpscompass.utils.gps.GpsUtil;
import com.bcg.gpscompass.utils.location.LocationListenerCallback;
import com.bcg.gpscompass.utils.sensor.SensorManagerCompass;
import com.mapbox.android.core.location.LocationEngine;
import com.mapbox.android.core.location.LocationEngineProvider;
import com.mapbox.android.core.location.LocationEngineRequest;

public class CompassFragment extends BaseFragment<CompassPresenter> implements SensorEventListener, CompassListener, LocationListenerCallback.LocationUpdateListener, View.OnClickListener {

    long DEFAULT_INTERVAL_IN_MILLISECONDS = 1000L;

    private CompassViewModel mViewModel;
    private SensorManagerCompass mSensorManagerCompass;
    private CompassImageView mCustomImageCompassView;
    private TextView mTxtAddress;
    private ImageButton mBtnMap;
    private ImageButton mBtnLocation;
    private ImageButton mBtnWeather;
    private Typeface mTypeface;
    private int mCurrentMagnetic = 0;
    private float[] valuesAccel = new float[]{0f, 0f, 9.8f};
    private float[] valuesMagnetic = new float[]{0.5f, 0f, 0f};
    private float mAzimuth;

    private static final int CURRENT_REQUEST = 112;
    private float[] mGravity;
    private double currentInclinationX;
    private double currentInclinationY;
    private double startInclinationX;
    private double startInclinationY;
    private ImageView ivBallCompass;
    private float AA = 0.3F;
    long DEFAULT_MAX_WAIT_TIME = DEFAULT_INTERVAL_IN_MILLISECONDS * 5;
    private ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    getLastLocation();
                } else {
                    // Explain to the user that the feature is unavailable because the
                    // feature requires a permission that the user has denied. At the
                    // same time, respect the user's decision. Don't link to system
                    // settings in an effort to convince the user to change their
                    // decision.
                }
            });
    private LocationListenerCallback callback;
    private LocationEngine locationEngine;
    private String address;

    private double latitude;

    private double longitude;

    public CompassFragment() {
    }

    public static CompassFragment newInstance() {
        return new CompassFragment();
    }

    @Override
    protected CompassPresenter createPresenter() {
        return new CompassPresenter(this);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        mTypeface = ResourcesCompat.getFont(getActivity(), R.font.font_roboto_bold);
        mSensorManagerCompass = new SensorManagerCompass(requireActivity());
        mViewModel = new ViewModelProvider(this, new AppViewModelFactory(new GpsCompassRepositoryImpl())).get(CompassViewModel.class);
        callback = new LocationListenerCallback(requireActivity(), this);
    }

    public static void xyzAnim(View paramView, double[] paramArrayOfDouble1, double[] paramArrayOfDouble2, int paramInt) {
        try {
            TranslateAnimation localTranslateAnimation = new TranslateAnimation((float) paramArrayOfDouble1[0], (float) paramArrayOfDouble2[0], (float) paramArrayOfDouble1[1], (float) paramArrayOfDouble2[1]);
            localTranslateAnimation.setDuration(paramInt);
            localTranslateAnimation.setFillEnabled(true);
            localTranslateAnimation.setFillAfter(true);
            paramView.startAnimation(localTranslateAnimation);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected float[] xyzBall(float[] mFloat1, float[] mFloat2, float currentFloat) {
        if (mFloat2 == null) {
            return mFloat1;
        }
        for (int i = 0; i < mFloat1.length; i++) {
            mFloat2[i] += currentFloat * (mFloat1[i] - mFloat2[i]);
        }
        return mFloat2;
    }

    private void tiltDevice() {
//        double dbMath = Math.sqrt(mGravity[0] * mGravity[0] + mGravity[1] * mGravity[1] + mGravity[2] * mGravity[2]);
//        double[] dbListArray = new double[3];
//        dbListArray[0] = (mGravity[0] / dbMath);
//        dbListArray[1] = (mGravity[1] / dbMath);
//        dbListArray[2] = (mGravity[2] / dbMath);
//        currentInclinationX = (-Math.toDegrees(Math.asin(dbListArray[0])));
//        currentInclinationY = Math.toDegrees(Math.asin(dbListArray[1]));
//        double dbLayout = rlLimited.getWidth() / 2 - ivBallCompass.getWidth() / 2;
//        currentInclinationX = (dbLayout * -dbListArray[0]);
//        currentInclinationY = (dbLayout * dbListArray[1]);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_compass, container, false);
        mBtnMap = (ImageButton) view.findViewById(R.id.btn_map_compass);
        mBtnLocation = (ImageButton) view.findViewById(R.id.btn_location_compass);
        mCustomImageCompassView = (CompassImageView) view.findViewById(R.id.iv_compass);
        mTxtAddress = (TextView) view.findViewById(R.id.tv_address);
        //mTvDegreesDirection = (TextView) view.findViewById(R.id.tv_degrees_direction_home);
//        mTvCity.setTypeface(mTypeface);
//        mTvCity.setEllipsize(TextUtils.TruncateAt.MARQUEE);
//        mTvCity.setSelected(true);
//        mTvCity.setText("Wait...");
//        mTvDegreesDirection.setTypeface(mTypeface);
//        nativeExpressAdView = view.findViewById(R.id.ads_banner_home);

//        rlLimited = view.findViewById(R.id.rl_limit);
//        ivBallCompass = view.findViewById(R.id.iv_ball);

        mBtnWeather = view.findViewById(R.id.btn_weather_compass);
        mBtnWeather.setOnClickListener(this);
        mBtnLocation.setOnClickListener(this);
        mBtnMap.setOnClickListener(this);
        if (ContextCompat.checkSelfPermission(
                requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED) {
            getLastLocation();
        } else if (shouldShowRequestPermissionRationale("ABC")) {
//            showInContextUI(...);
        } else {
            requestPermissionLauncher.launch(
                    Manifest.permission.ACCESS_FINE_LOCATION);
        }
        return view;
    }

    @SuppressLint("MissingPermission")
    private void getLastLocation() {
        locationEngine = LocationEngineProvider.getBestLocationEngine(requireActivity());

        LocationEngineRequest request = new LocationEngineRequest.Builder(DEFAULT_INTERVAL_IN_MILLISECONDS)
                .setPriority(LocationEngineRequest.PRIORITY_NO_POWER)
                .setMaxWaitTime(DEFAULT_MAX_WAIT_TIME)
                .build();

        locationEngine.requestLocationUpdates(request, callback, getMainLooper());
        locationEngine.getLastLocation(callback);
    }

    @Override
    public void onResume() {
        super.onResume();
        mSensorManagerCompass.registerAccelerometerListener(this);
        mSensorManagerCompass.registerMagneListener(this);
        mSensorManagerCompass.registerOriListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        mSensorManagerCompass.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        mPresenter.changeDirection(sensorEvent);
        if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            mGravity = xyzBall(sensorEvent.values.clone(), mGravity, AA);
            tiltDevice();
            double[] a1 = new double[2];
            a1[0] = startInclinationX;
            a1[1] = startInclinationY;
            double[] a2 = new double[2];
            a2[0] = (-currentInclinationX);
            a2[1] = (-currentInclinationY);
            //xyzAnim(ivBallCompass, a1, a2, 210);
            startInclinationX = (-currentInclinationX);
            startInclinationY = (-currentInclinationY);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
        if (sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            switch (i) {
                case SensorManager.SENSOR_STATUS_ACCURACY_LOW:
                    mPresenter.calibrateCompass(SensorManager.SENSOR_STATUS_ACCURACY_LOW);
                    mCurrentMagnetic = SensorManager.SENSOR_STATUS_ACCURACY_LOW;
                    break;
                case SensorManager.SENSOR_STATUS_ACCURACY_MEDIUM:
                    mPresenter.calibrateCompass(SensorManager.SENSOR_STATUS_ACCURACY_MEDIUM);
                    mCurrentMagnetic = SensorManager.SENSOR_STATUS_ACCURACY_MEDIUM;
                    break;
                case SensorManager.SENSOR_STATUS_ACCURACY_HIGH:
                    mPresenter.calibrateCompass(SensorManager.SENSOR_STATUS_ACCURACY_HIGH);
                    mCurrentMagnetic = SensorManager.SENSOR_STATUS_ACCURACY_HIGH;
                    break;
            }
        }
    }

    @Override
    public void setChangeDirectionCompass(SensorEvent sensorEvent) {
        if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            valuesAccel = GpsUtil.lowPass(sensorEvent.values, valuesAccel);
            mSensorManagerCompass.setGravity(valuesAccel);
        } else if (sensorEvent.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            valuesMagnetic = GpsUtil.lowPass(sensorEvent.values,
                    valuesMagnetic);
            mSensorManagerCompass.setGeoMagnetic(valuesMagnetic);
        }
        mSensorManagerCompass.loadAzimuth();
        float newAzimuth = mSensorManagerCompass.getAzimuthSensor();
        if (mAzimuth != newAzimuth) {
            mAzimuth = newAzimuth;
            mCustomImageCompassView.setDegress(-mAzimuth);
            mCustomImageCompassView.invalidate();
            int degrees = Math.round(mAzimuth);
            String direction = GpsUtil.displayDirection(mAzimuth);
            //mTvDegreesDirection.setText(getString(R.string.text_direction, degrees, direction));
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.btn_map_compass:
                mPresenter.openViewGoogleMaps();
                break;
            case R.id.btn_location_compass:
                mPresenter.openLocation();
                break;
            case R.id.btn_weather_compass:
//                Intent mWeather = new Intent(getActivity(), MyWeatherActivity.class);
//                startActivity(mWeather);
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (locationEngine != null) {
            locationEngine.removeLocationUpdates(callback);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void showViewGoogleMapsCompass() {
//        Intent intent = new Intent(getActivity(), GoogleMapsActivity.class);
//        startActivity(intent);
    }

    @Override
    public void showLocationCompass() {
        LocationFragment fragment = LocationFragment.Companion.newInstance("ABCD", latitude, longitude);
        ((MainActivity) requireActivity()).addFragment(fragment);
    }

    @Override
    public void showIconLocation() {
        if (mBtnLocation != null) {
            mBtnLocation.setVisibility(View.VISIBLE);
            mBtnMap.setVisibility(View.VISIBLE);
        } else {
            mBtnLocation.setVisibility(View.INVISIBLE);
            mBtnMap.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void getCalibrate(int calibrate) {
//        if (1 == calibrate || 2 == calibrate) {
//            mBtnWarningApp.setColorFilter(ContextCompat.getColor(Objects.requireNonNull(getActivity()), R.color.color_warning));
//        } else {
//            mBtnWarningApp.setColorFilter(ContextCompat.getColor(Objects.requireNonNull(getActivity()), R.color.color_white));
//        }
    }

    @Override
    public void showWarningCompass() {
//        Intent intent = new Intent(getActivity(), HowToUseActivity.class);
//        intent.putExtra(AppConstantUtils.CALIBRATE_MAGENTIC_COMPASS, mCurrentMagnetic);
//        startActivity(intent);
    }

    @Override
    public void updateLocation(Location location) {
        if (location != null) {
            latitude = location.getLatitude();
            longitude = location.getLongitude();
            mViewModel.getFlowerList(latitude, longitude);
        }
    }
}
