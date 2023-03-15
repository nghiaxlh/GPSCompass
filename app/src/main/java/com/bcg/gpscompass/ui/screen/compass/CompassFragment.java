package com.bcg.gpscompass.ui.screen.compass;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.SurfaceTexture;
import android.graphics.Typeface;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.TranslateAnimation;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bcg.gpscompass.R;
import com.bcg.gpscompass.ui.base.BaseFragment;
import com.bcg.gpscompass.ui.view.CompassImageView;
import com.bcg.gpscompass.utils.Constants;
import com.bcg.gpscompass.utils.gps.GpsLocationService;
import com.bcg.gpscompass.utils.gps.GpsUtil;
import com.bcg.gpscompass.utils.sensor.SensorManagerCompass;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class CompassFragment extends BaseFragment<CompassPresenter> implements SensorEventListener, CompassListener, View.OnClickListener {

    private SensorManagerCompass mSensorManagerCompass;
    private CompassImageView mCustomImageCompassView;
    private TextView mTxtAddress;
    private TextView mTvDegreesDirection;
    private ImageButton mBtnMap;
    private ImageButton mBtnLocation;
    private ImageButton mBtnWeather;
    private Typeface mTypeface;
    private int mCurrentMagnetic = 0;
    private float[] valuesAccel = new float[]{0f, 0f, 9.8f};
    private float[] valuesMagnetic = new float[]{0.5f, 0f, 0f};
    private float mAzimuth;

    private FusedLocationProviderClient fusedLocationProviderClient;
    private Location mLastLocation;
    private LocationRequest mLocationRequest;
    private AddressResultReceiver addressResultReceiver;
    private String mLocationOutput;
    private String mFullLocation;
    private static final int CURRENT_REQUEST = 112;
    private float[] mGravity;
    private double currentInclinationX;
    private double currentInclinationY;
    private double startInclinationX;
    private double startInclinationY;
    private ImageView ivBallCompass;
    private float AA = 0.3F;

    public CompassFragment() {
    }

    public static CompassFragment newInstance() {
        return new CompassFragment();
    }

    @Override
    protected CompassPresenter createPresenter() {
        return new CompassPresenter(this);
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        mTypeface = ResourcesCompat.getFont(getActivity(), R.font.font_roboto_bold);
        mSensorManagerCompass = new SensorManagerCompass(requireActivity());
        addressResultReceiver = new AddressResultReceiver(new Handler());
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(Objects.requireNonNull(getActivity()));
        mLocationOutput = "";
        mFullLocation = "";
        createLocationRequest();
        locationSetting();
    }

    private void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    @SuppressLint("MissingPermission")
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_compass, container, false);
        //RateUsDialogApp.showDialogRateApp(getActivity());
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

//        admobBannerUnit();
        getLastLocation();
        return view;
    }

    LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            if (locationResult == null) {
                return;
            }
            for (Location location : locationResult.getLocations()) {
                mPresenter.setTextLocation(location.getLatitude(), location.getLongitude());
                mLastLocation = location;
                getLocation();
            }
        }
    };

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

//    private void admobBannerUnit() {
//        final AdView mAdView = new AdView(Objects.requireNonNull(getContext()));
//        final AdRequest adRequest = new AdRequest.Builder().build();
//        mAdView.setAdSize(AdSize.BANNER);
//        mAdView.setAdUnitId(getString(R.string.banner_id_1));
////        nativeExpressAdView.addView(mAdView);
//        mAdView.loadAd(adRequest);
//        mAdView.setAdListener(new AdListener() {
//            @Override
//            public void onAdClosed() {
//                super.onAdClosed();
//            }
//
//            @Override
//            public void onAdOpened() {
//                super.onAdOpened();
//            }
//
//            @Override
//            public void onAdLeftApplication() {
//                super.onAdLeftApplication();
//            }
//
//            @Override
//            public void onAdFailedToLoad(int i) {
//                super.onAdFailedToLoad(i);
//            }
//
//            @Override
//            public void onAdLoaded() {
//                super.onAdLoaded();
//            }
//        });
//    }

    @SuppressLint("MissingPermission")
    private void getLastLocation() {
        fusedLocationProviderClient.getLastLocation()
                .addOnSuccessListener(Objects.requireNonNull(getActivity()), new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            mLastLocation = location;
                            mPresenter.setTextLocation(location.getLatitude(), location.getLongitude());
                            getLocation();
                        } else {
                        }
                    }
                });
    }

    private void getLocation() {
        if (!Geocoder.isPresent()) {
            Toast.makeText(getActivity(),
                    "Can't find current address, ",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        startIntentService();
    }

    protected void startIntentService() {
        Intent intent = new Intent(getActivity(), GpsLocationService.class);
        intent.putExtra(Constants.RECEIVER_EXTRA, addressResultReceiver);
        intent.putExtra(Constants.LOCATION_EXTRA, mLastLocation);
        getActivity().startService(intent);
    }

    @Override
    public void onResume() {
        super.onResume();
        mSensorManagerCompass.registerAccelerometerListener(this);
        mSensorManagerCompass.registerMagneListener(this);
        mSensorManagerCompass.registerOriListener(this);
        startLocationUpdates();
    }

    @SuppressLint("MissingPermission")
    private void startLocationUpdates() {
        fusedLocationProviderClient.requestLocationUpdates(mLocationRequest, mLocationCallback, null);
    }

    @Override
    public void onPause() {
        super.onPause();
        mSensorManagerCompass.unregisterListener(this);
        stopUpdateLocation();
    }

    private void stopUpdateLocation() {
        fusedLocationProviderClient.removeLocationUpdates(mLocationCallback);
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
    public void setTextForLocation(double lat, double lon) {
        String latitude = GpsUtil.decimalToDMS(lat) + GpsUtil.getSymbol(lat, true);
        String longitude = GpsUtil.decimalToDMS(lon) + GpsUtil.getSymbol(lon, false);
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CURRENT_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCamera();
            } else {
                Toast.makeText(getContext(), "The app was not allowed to access camera", Toast.LENGTH_LONG).show();
            }
        }
    }

    @SuppressLint("LongLogTag")
    private void getCamera() {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onStart() {
        super.onStart();
        getCamera();
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
//        Intent intent = new Intent(getActivity(), LocationActivity.class);
//        intent.putExtra(AppConstantUtils.LOCATION_EXTRA, mLastLocation);
//        intent.putExtra(AppConstantUtils.LOCATION_STRING_EXTRA, mFullLocation);
//        startActivity(intent);
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


    class AddressResultReceiver extends ResultReceiver {

        public AddressResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            if (resultData == null) {
                return;
            }
            mLocationOutput = resultData.getString(Constants.RESULT_KEY_ADDRESS);
            mFullLocation = resultData.getString(Constants.RESULT_KEY_ADDRESS_FULL);

            displayAddressOutput(mFullLocation);
            if (resultCode == Constants.SUCCESS_RESULT) {
                mPresenter.showIconLocation();
            }
        }
    }

    private void displayAddressOutput(String currentAddress) {
        mTxtAddress.setText(currentAddress);
    }

    private void locationSetting() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(mLocationRequest);
        Task<LocationSettingsResponse> result = LocationServices.getSettingsClient(getActivity()).checkLocationSettings(builder.build());
        result.addOnCompleteListener(new OnCompleteListener<LocationSettingsResponse>() {
            @Override
            public void onComplete(@NonNull Task<LocationSettingsResponse> task) {
                try {
                    LocationSettingsResponse response = task.getResult(ApiException.class);
                } catch (ApiException exception) {
                    switch (exception.getStatusCode()) {
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            try {
                                ResolvableApiException resolvable = (ResolvableApiException) exception;
                                resolvable.startResolutionForResult(getActivity(), Constants.REQUEST_SETTINGS);
                            } catch (IntentSender.SendIntentException | ClassCastException e) {
                                e.printStackTrace();
                            }
                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            break;
                    }
                }
            }
        });
    }
}
